package rnd.puzzleapp.puzzle;

import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Locale;

/**
 * A bridge between two {@link Island} in a {@link Puzzle}.
 */
public class Bridge implements Comparable<Bridge> {
    private static final Comparator<Bridge> COMPARATOR = Comparator
            .comparing(Bridge::getX1)
            .thenComparing(Bridge::getY1)
            .thenComparing(Bridge::getX2)
            .thenComparing(Bridge::getY2);

    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    /**
     * Creates a new bridge instance. Note that it is expected the location of the first island is
     * always either to the left of or above the second island, unless the bridge isn't straight or
     * forms a loop.
     * @param x1 the x coordinate of the first island
     * @param y1 the y coordinate of the first island
     * @param x2 the x coordinate of the second island
     * @param y2 the y coordinate of the second island
     */
    public Bridge(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Copy constructor.
     * @param other the instance to copy
     */
    private Bridge(Bridge other) {
        this.x1 = other.x1;
        this.y1 = other.y1;
        this.x2 = other.x2;
        this.y2 = other.y2;
    }

    /**
     * Creates a copy of this bridge.
     * @return a copied instance of this bridge
     */
    public Bridge copy() {
        return new Bridge(this);
    }

    /**
     * Checks whether the given island is one of the endpoints of this bridge.
     * @param island the island to check
     * @return {@code true} if the given island is an endpoint, {@code false} otherwise.
     */
    public boolean hasEndpoint(Island island) {
        return hasEndpoint(island.getX(), island.getY());
    }

    /**
     * Checks whether the given coordinates of an island are of one of the endpoints of this bridge.
     * @param x the x coordinate of the island to check
     * @param y the y coordinate of the island to check
     * @return {@code true} if the island at the given coordinates is an endpoint, {@code false} otherwise.
     */
    public boolean hasEndpoint(int x, int y) {
        return (x1 == x && y1 == y) || (x2 == x && y2 == y);
    }

    /**
     * Returns the x coordinate of the first island.
     * @return the x coordinate
     */
    public int getX1() {
        return x1;
    }

    /**
     * Returns the y coordinate of the first island.
     * @return the y coordinate
     */
    public int getY1() {
        return y1;
    }

    /**
     * Returns the x coordinate of the second island.
     * @return the x coordinate
     */
    public int getX2() {
        return x2;
    }

    /**
     * Returns the y coordinate of the second island.
     * @return the y coordinate
     */
    public int getY2() {
        return y2;
    }

    /**
     * Returns the orientation of this bridge. Note that this method should only be called if this
     * bridge is straight, as determined by {@link Bridge#isStraight()}.
     * @return the orientation of this bridge
     */
    public Orientation getOrientation() {
        if(!isStraight()) {
            throw new IllegalStateException("Bridge must be straight");
        }

        return x1 == x2 ? Orientation.Vertical : Orientation.Horizontal;
    }

    /**
     * Returns the horizontal span of this bridge. Note that this method should only be called if this
     * bridge is straight, as determined by {@link Bridge#isStraight()}.
     * @return the horizontal span of this bridge
     */
    public Span getHorizontalSpan() {
        if(!isStraight()) {
            throw new IllegalStateException("Bridge must be straight");
        }

        return Span.fromValues(x1, x2);
    }

    /**
     * Returns the vertical span of this bridge. Note that this method should only be called if this
     * bridge is straight, as determined by {@link Bridge#isStraight()}.
     * @return the vertical span of this bridge
     */
    public Span getVerticalSpan() {
        if(!isStraight()) {
            throw new IllegalStateException("Bridge must be straight");
        }

        return Span.fromValues(y1, y2);
    }

    /**
     * Checks if this bridge is straight, by checking if the endpoints are either aligned horizontally
     * or vertically.
     * @return {@code true} if this bridge is straight, {@code false} otherwise
     */
    public boolean isStraight() {
        return x1 == x2 ^ y1 == y2;
    }

    /**
     * Checks if this bridge forms a loop, by checking if both endpoints are the same island.
     * @return {@code true} if this bridge is a loop, {@code false} otherwise
     */
    public boolean isLoop() {
        return x1 == x2 && y1 == y2;
    }

    /**
     * Checks if this bridge intersects with the given bridge.
     * @param bridge the bridge to check
     * @return {@code true} if this bridge intersects, {@code false} otherwise
     */
    public boolean intersects(Bridge bridge) {
        Span h1 = getHorizontalSpan();
        Span v1 = getVerticalSpan();
        Span h2 = bridge.getHorizontalSpan();
        Span v2 = bridge.getVerticalSpan();

        return (h1.overlaps(h2) || h1.equals(h2)) && (v1.overlaps(v2) || v1.equals(v2));
    }

    /**
     * Checks if this bridge crosses over the given island.
     * @param island the island to check
     * @return {@code true} if this bridge crosses over, {@code false} otherwise
     */
    public boolean crosses(Island island) {
        return crosses(island.getX(), island.getY());
    }

    /**
     * Checks if this bridge crosses over the island at the given coordinates.
     * @param x the x coordinate of the island to check
     * @param y the y coordinate of the island to check
     * @return {@code true} if this bridge crosses over, {@code false} otherwise
     */
    public boolean crosses(int x, int y) {
        return getHorizontalSpan().contains(x) && y1 == y || getVerticalSpan().contains(y) && x1 == x;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Bridge) {
            Bridge other = (Bridge)obj;

            return x1 == other.x1 && y1 == other.y1 && x2 == other.x2 && y2 == other.y2;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return x1 ^ y1 ^ x2 ^ y2;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%d,%d)-(%d,%d)", x1, y1, x2, y2);
    }

    @Override
    public int compareTo(@NonNull Bridge bridge) {
        return COMPARATOR.compare(this, bridge);
    }

    /**
     * Creates a new bridge from the given islands. The first and second island is determined automatically
     * by comparing {@code endpointA} with {@code endpointB}.
     * @param endpointA the first endpoint
     * @param endpointB the second endpoint
     * @return a bridge from the given endpoints
     */
    public static Bridge create(Island endpointA, Island endpointB) {
        int compare = endpointA.compareTo(endpointB);
        Island firstEndpoint = compare <= 0 ? endpointA : endpointB;
        Island secondEndpoint = compare > 0 ? endpointA : endpointB;

        return new Bridge(firstEndpoint.getX(), firstEndpoint.getY(), secondEndpoint.getX(), secondEndpoint.getY());
    }
}
