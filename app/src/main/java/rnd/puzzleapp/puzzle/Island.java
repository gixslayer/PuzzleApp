package rnd.puzzleapp.puzzle;

import java.util.Locale;

public class Island {
    private final int x;
    private final int y;
    private final int requiredBridges;

    public Island(int x, int y, int requiredBridges) {
        this.x = x;
        this.y = y;
        this.requiredBridges = requiredBridges;
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
}
