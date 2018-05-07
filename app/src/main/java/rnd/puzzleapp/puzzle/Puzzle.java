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

/**
 * A puzzle that consists of a collection of {@link Bridge} and {@link Island} instances.
 */
public class Puzzle implements Comparable<Puzzle> {
    // NOTE: Online this rule is mentioned, but project description doesn't explicitly state this.
    public static final int MAX_BRIDGE_COUNT = 2;

    private static final FastCopyTag FAST_COPY_TAG = new FastCopyTag();

    private final List<Island> islands;
    private final List<Bridge> bridges;

    /**
     * Creates a new empty puzzles without any islands or bridges.
     */
    public Puzzle() {
        this.islands = new ArrayList<>();
        this.bridges = new ArrayList<>();
    }

    /**
     * Copy constructor.
     * @param other the instance to copy
     */
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

    /**
     * Fast copy constructor.
     * @param other the instance to copy
     * @param tag tag used to correctly invoke this constructor
     */
    private Puzzle(Puzzle other, FastCopyTag tag) {
        this.islands = other.islands;
        this.bridges = new ArrayList<>(other.bridges);
    }

    /**
     * Creates a copy of this puzzle and all contained bridges and islands.
     * @return a copied instance of this puzzle
     */
    public Puzzle copy() {
        return new Puzzle(this);
    }

    /**
     * Creates a fast copy of this puzzle, by making a shallow copy of the collection of bridges.
     * The collection of islands should never be mutated on an instance returned by this method.
     * @return a copied instance of this puzzle
     */
    public Puzzle fastCopy() {
        // NOTE: A fast semi-shallow copy method that is only intended for solvers to minimize copy
        // overhead and allocation count (to reduce pressure on the GC).
        return new Puzzle(this, FAST_COPY_TAG);
    }

    /**
     * Resets this puzzle by removing all bridges.
     */
    public void reset() {
        bridges.clear();
    }

    /**
     * Returns a stream of all islands that are directly connected to the given island.
     * @param island the island to check from
     * @return a stream of all directly connected islands
     */
    public Stream<Island> getNeighbors(Island island) {
        return bridges.stream()
                .filter(b -> b.hasEndpoint(island))
                .map(b -> getOtherEndpoint(b, island))
                .distinct();
    }

    /**
     * Returns the other endpoint of the given bridge and island. Note that this puzzle is expected
     * to contain the bridge (and thus both endpoints).
     * @param bridge the bridge to return the other endpoint of
     * @param island the known endpoint in the bridge
     * @return the other endpoint of the given bridge
     */
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

    /**
     * Checks if all islands have their required bridge count.
     * @return {@code true} if all islands have the required bridge count, {@code false} otherwise
     */
    private boolean hasRequiredBridgeCount() {
        return islands.stream().allMatch(i -> i.getRequiredBridges() == getBridgeCount(i));
    }

    /**
     * Checks if the current puzzle forms a connected graph.
     * @return {@code true} if the puzzle is connected, {@code false} otherwise
     */
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

    /**
     * Checks if this puzzle is currently solved.
     * @return {@code true} if this puzzle is solved, {@code false} otherwise
     */
    private boolean isSolved() {
        return hasRequiredBridgeCount() && isConnected();
    }

    /**
     * Gets the solved status of this puzzle. Note that it is assumed this puzzle is not untouched,
     * and thus contains at least one bridge.
     * @return the solved status
     */
    private PuzzleStatus getSolvedStatus() {
        return isSolved() ? PuzzleStatus.Solved : PuzzleStatus.Unsolved;
    }

    /**
     * Gets the amount of bridges that have the given island as an endpoint.
     * @param island the island to check
     * @return the amount of bridges
     */
    public long getBridgeCount(Island island) {
        return bridges.stream().filter(b -> b.hasEndpoint(island)).count();
    }

    /**
     * Gets the amount of bridges that are equal to the given bridge.
     * @param bridge the bridge to check
     * @return the amount of bridges
     */
    public long getBridgeCount(Bridge bridge) {
        return bridges.stream().filter(bridge::equals).count();
    }

    /**
     * Attempts to add the given bridge to this puzzle.
     * @param bridge the bridge to place
     * @return {@code true} if the bridge was added, {@code false} otherwise
     */
    public boolean placeBridge(Bridge bridge) {
        return doIf(canPlaceBridge(bridge), () -> addSorted(bridges, bridge));
    }

    /**
     * Adds the given bridge to this puzzle. Note that it is assumed the given bridge can actually
     * be placed. This method is intended to improve performance by eliminating checks, as this method
     * assumes those checks all pass.
     * @param bridge the bridge to add
     */
    public void addBridge(Bridge bridge) {
        addSorted(bridges, bridge);
    }

    /**
     * Adds the given island to this puzzle. Note that it is assumed the given island can actually
     * be added. This method is intended to improve performance by eliminating checks, as this method
     * assumes those checks all pass.
     * @param island the island to add
     */
    public void addIsland(Island island) {
        addSorted(islands, island);
    }

    /**
     * Deletes a single occurrence of the given bridge, if one exists.
     * @param bridge the bridge to delete
     * @return {@code true} is a bridge was deleted, {@code false} otherwise
     */
    public boolean deleteBridge(Bridge bridge) {
        return bridges.remove(bridge);
    }

    /**
     * Checks if the given bridge can be placed in this puzzle without violating any of the game rules.
     * @param bridge the bridge to check
     * @return {@code true} if the given bridge can be placed, {@code false} otherwise
     */
    public boolean canPlaceBridge(Bridge bridge) {
        return bridge.isStraight()
                && !bridge.isLoop()
                && getBridgeCount(bridge) < MAX_BRIDGE_COUNT
                && islands.stream().noneMatch(bridge::crosses)
                && (bridges.stream().noneMatch(bridge::intersects)
                || bridges.stream().anyMatch(bridge::equals));
    }

    /**
     * Gets the current status of this puzzle.
     * @return the current status
     */
    public PuzzleStatus getStatus() {
        return bridges.isEmpty() ? PuzzleStatus.Untouched : getSolvedStatus();
    }

    /**
     * Returns the first bridge that crosses over the island at the given coordinates, if one exists.
     * @param x the x coordinate of the island
     * @param y the y coordinate of the island
     * @return the bridge that crosses over the given island, or an empty optional if no such bridge exists
     */
    public Optional<Bridge> getBridge(int x, int y) {
        return bridges.stream().filter(b -> b.crosses(x, y)).findFirst();
    }

    /**
     * Returns the island at the given coordinates, if one exists.
     * @param x the x coordinate of the island
     * @param y the y coordinate of the island
     * @return the island at the given coordinates, or an empty optional if no such island exists
     */
    public Optional<Island> getIsland(int x, int y) {
        return islands.stream().filter(i -> i.getX() == x && i.getY() == y).findFirst();
    }

    /**
     * Returns the width of this puzzle.
     * @return the width, as number of islands
     */
    public int getWidth() {
        // NOTE: the leftmost island should be at x = 0, thus the width is the highest x + 1.
        return islands.stream().map(Island::getX).max(Integer::compare).map(i -> i + 1).orElse(0);
    }

    /**
     * Returns the height of this puzzle.
     * @return the height, as number of islands
     */
    public int getHeight() {
        // NOTE: the topmost island should be at y = 0, thus the height is the highest y + 1.
        return islands.stream().map(Island::getY).max(Integer::compare).map(i -> i + 1).orElse(0);
    }

    /**
     * Returns the collection of islands in this puzzle.
     * @return the collection of islands
     */
    public List<Island> getIslands() {
        return islands;
    }

    /**
     * Returns the collection of bridges in this puzzle.
     * @return the collection of bridges
     */
    public List<Bridge> getBridges() {
        return bridges;
    }

    /**
     * Returns the list of all bridges that could currently be placed in this puzzle.
     * @return the list of bridges
     */
    public List<Bridge> getPossibleBridges() {
        return crossApply(islands, Bridge::create)
                .filter(this::canPlaceBridge)
                .collect(Collectors.toList());
    }

    /**
     * Returns the list of all bridges that could currently be placed in this puzzle, by filtering
     * from the given list of possible bridges. This method is intended to improve solver performance
     * as filtering down from {@link Puzzle#getPossibleBridges()} becomes rather expensive for larger
     * puzzles. Note that unlike {@link Puzzle#getPossibleBridges()} this method does not considers a
     * bridge with an endpoint that already is at or over the required amount of bridges as a bridge
     * than can be placed.
     * @param possibleBridges the list of bridges to filter from
     * @return the new list of possible bridges
     */
    public List<Bridge> getPossibleBridges(List<Bridge> possibleBridges) {
        return possibleBridges.stream().filter(this::canStillPlaceBridge).collect(Collectors.toList());
    }

    /**
     * Checks if the given bridge that could previously be placed can still be placed in this puzzle.
     * @param bridge the bridge to check
     * @return {@code true} if the given bridge can still be place, {@code false} otherwise
     */
    private boolean canStillPlaceBridge(Bridge bridge) {
        if(!(getBridgeCount(bridge) < MAX_BRIDGE_COUNT
                && (bridges.stream().noneMatch(bridge::intersects)
                || bridges.stream().anyMatch(bridge::equals)))) {
            return false;
        }

        // The bridge can only be placed if both endpoints are below the required bridge count.
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
