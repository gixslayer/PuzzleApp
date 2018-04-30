package rnd.puzzleapp.puzzle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

import static rnd.puzzleapp.utils.Functional.doIf;

public class Puzzle {
    // TODO: Online this rule is mentioned, but project description doesn't explicitly state this.
    public static final int MAX_BRIDGE_COUNT = 2;

    private final List<Island> islands;
    private final List<Bridge> bridges;

    public Puzzle() {
        // TODO: Ensure a puzzle contains at least 2 islands.
        this.islands = new ArrayList<>();
        this.bridges = new ArrayList<>();
    }

    public Stream<Island> getNeighbors(Island island) {
        return bridges.stream()
                .filter(b -> b.hasEndpoint(island))
                .map(b -> b.getOtherEndpoint(island))
                .distinct();
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

    public boolean placeBridge(Bridge bridge) {
        return doIf(canPlaceBridge(bridge), () -> bridges.add(bridge));
    }

    public boolean deleteBridge(Bridge bridge) {
        return bridges.remove(bridge);
    }

    public boolean canPlaceBridge(Bridge bridge) {
        return bridge.isStraight()
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

    public Optional<Island> getIsland(int i) {
        int width = getWidth();

        return getIsland(i % width, i / width);
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
}
