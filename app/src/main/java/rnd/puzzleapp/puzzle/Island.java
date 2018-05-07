package rnd.puzzleapp.puzzle;

import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Locale;

/**
 * An island in a {@link Puzzle}.
 */
public class Island implements Comparable<Island> {
    private static final Comparator<Island> COMPARATOR = Comparator
            .comparing(Island::getX)
            .thenComparing(Island::getY)
            .thenComparing(Island::getRequiredBridges);

    private final int x;
    private final int y;
    private final int requiredBridges;

    /**
     * Creates a new island instance.
     * @param x the x coordinate of this island
     * @param y the y coordinate of this island
     * @param requiredBridges the required number of bridges of this island, which should normally be
     *                        between 1 and 8
     */
    public Island(int x, int y, int requiredBridges) {
        this.x = x;
        this.y = y;
        this.requiredBridges = requiredBridges;
    }

    /**
     * Copy constructor.
     * @param other the instance to copy
     */
    private Island(Island other) {
        this.x = other.x;
        this.y = other.y;
        this.requiredBridges = other.requiredBridges;
    }

    /**
     * Creates a copy of this island.
     * @return a copied instance of this island
     */
    public Island copy() {
        return new Island(this);
    }

    /**
     * Returns the x coordinate of this island.
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate of this island.
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the number of bridges this island is required to have for a valid solution.
     * @return the required number of bridges
     */
    public int getRequiredBridges() {
        return requiredBridges;
    }

    /**
     * Checks if the given bridge crosses over this island.
     * @param bridge the bridge to check
     * @return {@code true} if the given bridge crosses over, {@code false} otherwise.
     */
    public boolean crosses(Bridge bridge) {
        return bridge.crosses(this);
    }

    @Override
    public int hashCode() {
        return x ^ y ^ requiredBridges;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Island) {
            Island other = (Island)obj;

            return x == other.x && y == other.y && requiredBridges == other.requiredBridges;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%d, %d (%d)", x, y, requiredBridges);
    }

    @Override
    public int compareTo(@NonNull Island island) {
        return COMPARATOR.compare(this, island);
    }
}
