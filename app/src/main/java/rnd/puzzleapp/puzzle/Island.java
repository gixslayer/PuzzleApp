package rnd.puzzleapp.puzzle;

import android.support.annotation.NonNull;

import java.util.Locale;

public class Island implements Comparable<Island> {
    private final int x;
    private final int y;
    private final int requiredBridges;

    public Island(int x, int y, int requiredBridges) {
        this.x = x;
        this.y = y;
        this.requiredBridges = requiredBridges;
    }

    private Island(Island other) {
        this.x = other.x;
        this.y = other.y;
        this.requiredBridges = other.requiredBridges;
    }

    public Island copy() {
        return new Island(this);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRequiredBridges() {
        return requiredBridges;
    }

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

            return other.x == x && other.y == y && other.requiredBridges == requiredBridges;
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
        int compareX = Integer.compare(x, island.x);
        if(compareX != 0) {
            return compareX;
        }

        int compareY = Integer.compare(y, island.y);
        if(compareY != 0) {
            return compareY;
        }

        return Integer.compare(requiredBridges, island.requiredBridges);
    }
}
