package rnd.puzzleapp.puzzle;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static rnd.puzzleapp.utils.Collections.addSorted;
import static rnd.puzzleapp.utils.Collections.iteratorCompare;
import static rnd.puzzleapp.utils.Functional.crossApply;
import static rnd.puzzleapp.utils.Functional.doIf;

public class Puzzle implements Comparable<Puzzle> {
    // TODO: Online this rule is mentioned, but project description doesn't explicitly state this.
    public static final int MAX_BRIDGE_COUNT = 2;

    private static final FastCopyTag FAST_COPY_TAG = new FastCopyTag();

    private final List<Island> islands;
    private final List<Bridge> bridges;

    public Puzzle() {
        // TODO: Ensure a puzzle contains at least 2 islands.
        this.islands = new ArrayList<>();
        this.bridges = new ArrayList<>();
    }

    private Puzzle(Puzzle other) {
        this.islands = other.islands.stream()
                .sequential()
                .map(Island::copy)
                .collect(Collectors.toList());
        this.bridges = other.bridges.stream()
                .sequential()
                .map(Bridge::copy)
                .collect(Collectors.toList());
    }

    private Puzzle(Puzzle other, FastCopyTag tag) {
        this.islands = other.islands;
        this.bridges = new ArrayList<>(other.bridges);
    }

    public Puzzle copy() {
        return new Puzzle(this);
    }

    public Puzzle fastCopy() {
        // NOTE: A fast semi-shallow copy method that is only intended for solvers to minimize copy
        // overhead and allocation count (to reduce pressure on the GC).
        return new Puzzle(this, FAST_COPY_TAG);
    }

    public void reset() {
        bridges.clear();
    }

    public Stream<Island> getNeighbors(Island island) {
        return bridges.stream()
                .filter(b -> b.hasEndpoint(island))
                .map(b -> getOtherEndpoint(b, island))
                .distinct();
    }

    public Island getOtherEndpoint(Bridge bridge, Island island) {
        boolean isFirstEndpoint = bridge.getX1() == island.getX() && bridge.getY1() == island.getY();
        int x = isFirstEndpoint ? bridge.getX2() : bridge.getX1();
        int y = isFirstEndpoint ? bridge.getY2() : bridge.getY1();
        Optional<Island> otherIsland = getIsland(x, y);

        if(!otherIsland.isPresent()) {
            throw new IllegalArgumentException("Other endpoint does not exist");
        }

        return otherIsland.get();
    }

    private boolean hasRequiredBridgeCount() {
        return islands.stream().allMatch(i -> i.getRequiredBridges() == getBridgeCount(i));
    }

    private boolean isConnected() {
        Set<Island> marked = new HashSet<>();
        Queue<Island> queue = new ArrayDeque<>();

        // Add a starting island to the queue. Since the graph must be connected this can be an arbitrary island.
        queue.add(islands.get(0));

        while(!queue.isEmpty()) {
            Island island = queue.remove();

            // Mark the current island as reachable from the starting island.
            marked.add(island);

            // Only add neighbors to the queue that haven't been marked and aren't already in the queue.
            getNeighbors(island)
                    .filter(n -> !queue.contains(n))
                    .filter(n -> !marked.contains(n))
                    .forEach(queue::add);
        }

        // Check if all islands have been marked, thus making the graph connected.
        return islands.stream().allMatch(marked::contains);
    }

    private boolean isSolved() {
        return hasRequiredBridgeCount() && isConnected();
    }

    private PuzzleStatus getSolvedStatus() {
        return isSolved() ? PuzzleStatus.Solved : PuzzleStatus.Unsolved;
    }

    public long getBridgeCount(Island island) {
        return bridges.stream().filter(b -> b.hasEndpoint(island)).count();
    }

    public long getBridgeCount(Bridge bridge) {
        return bridges.stream().filter(bridge::equals).count();
    }

    public int getBridgeCount() {
        return bridges.size();
    }

    public int getIslandCount() {
        return islands.size();
    }

    public boolean placeBridge(Bridge bridge) {
        return doIf(canPlaceBridge(bridge), () -> addSorted(bridges, bridge));
    }

    public void addBridge(Bridge bridge) {
        // NOTE: This method is only intended for solvers in order to improve performance.
        // NOTE: Also used for serialization.
        addSorted(bridges, bridge);
    }

    public void addIsland(Island island) {
        // NOTE: Used for serialization.
        addSorted(islands, island);
    }

    public boolean deleteBridge(Bridge bridge) {
        return bridges.remove(bridge);
    }

    public boolean canPlaceBridge(Bridge bridge) {
        return bridge.isStraight()
                && !bridge.isLoop()
                && bridges.stream().filter(bridge::equals).count() < MAX_BRIDGE_COUNT
                && islands.stream().noneMatch(bridge::crosses)
                && (bridges.stream().noneMatch(bridge::intersects)
                || bridges.stream().anyMatch(bridge::equals));
    }

    public PuzzleStatus getStatus() {
        return bridges.isEmpty() ? PuzzleStatus.Untouched : getSolvedStatus();
    }

    public Optional<Bridge> getBridge(int x, int y) {
        Island island = new Island(x, y, 0);

        return bridges.stream().filter(b -> b.crosses(island)).findFirst();
    }

    public Optional<Island> getIsland(int x, int y) {
        return islands.stream().filter(i -> i.getX() == x && i.getY() == y).findFirst();
    }

    public int getWidth() {
        return islands.stream().map(Island::getX).max(Integer::compare).map(i -> i + 1).orElse(0);
    }

    public int getHeight() {
        return islands.stream().map(Island::getY).max(Integer::compare).map(i -> i + 1).orElse(0);
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Bridge> getBridges() {
        return bridges;
    }

    public List<Bridge> getPossibleBridges() {
        return crossApply(islands, Bridge::create)
                .filter(this::canPlaceBridge)
                .collect(Collectors.toList());
    }

    public List<Bridge> getPossibleBridges(List<Bridge> possibleBridges) {
        return possibleBridges.stream().filter(this::canStillPlaceBridge).collect(Collectors.toList());
    }

    private boolean canStillPlaceBridge(Bridge bridge) {
        if(!(bridges.stream().filter(bridge::equals).count() < MAX_BRIDGE_COUNT
                && (bridges.stream().noneMatch(bridge::intersects)
                || bridges.stream().anyMatch(bridge::equals)))) {
            return false;
        }

        Island island1 = getIsland(bridge.getX1(), bridge.getY1()).get();
        Island island2 = getIsland(bridge.getX2(), bridge.getY2()).get();

        return getBridgeCount(island1) < island1.getRequiredBridges()
                && getBridgeCount(island2) < island2.getRequiredBridges();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Puzzle) {
            Puzzle other = (Puzzle)obj;

            return compareTo(other) == 0;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(@NonNull Puzzle puzzle) {
        // NOTE: Assumes both bridges and islands are sorted based on compareTo
        int islandCompare = iteratorCompare(islands, puzzle.islands);

        return islandCompare != 0 ? islandCompare : iteratorCompare(bridges, puzzle.bridges);
    }

    private static class FastCopyTag { }
}
