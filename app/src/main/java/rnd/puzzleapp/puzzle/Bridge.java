package rnd.puzzleapp.puzzle;

import android.support.annotation.NonNull;

import java.util.Locale;

public class Bridge implements Comparable<Bridge> {
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    public Bridge(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    private Bridge(Bridge other) {
        this.x1 = other.x1;
        this.y1 = other.y1;
        this.x2 = other.x2;
        this.y2 = other.y2;
    }

    public Bridge copy() {
        return new Bridge(this);
    }

    public boolean hasEndpoint(Island island) {
        return hasEndpoint(island.getX(), island.getY());
    }

    public boolean hasEndpoint(int x, int y) {
        return (x1 == x && y1 == y) || (x2 == x && y2 == y);
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public Orientation getOrientation() {
        if(!isStraight()) {
            throw new IllegalStateException("Bridge must be straight");
        }

        return x1 == x2 ? Orientation.Vertical : Orientation.Horizontal;
    }

    public Span getHorizontalSpan() {
        if(!isStraight()) {
            throw new IllegalStateException("Bridge must be straight");
        }

        return Span.fromValues(x1, x2);
    }

    public Span getVerticalSpan() {
        if(!isStraight()) {
            throw new IllegalStateException("Bridge must be straight");
        }

        return Span.fromValues(y1, y2);
    }

    public boolean isStraight() {
        return x1 == x2 ^ y1 == y2;
    }

    public boolean isLoop() {
        return x1 == x2 && y1 == y2;
    }

    public boolean intersects(Bridge bridge) {
        Span h1 = getHorizontalSpan();
        Span v1 = getVerticalSpan();
        Span h2 = bridge.getHorizontalSpan();
        Span v2 = bridge.getVerticalSpan();

        return (h1.overlaps(h2) || h1.equals(h2)) && (v1.overlaps(v2) || v1.equals(v2));
    }

    public boolean crosses(Island island) {
        return getHorizontalSpan().contains(island.getX()) && y1 == island.getY()
                || getVerticalSpan().contains(island.getY()) && x1 == island.getX();
    }

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
        int compareX1 = Integer.compare(x1, bridge.x1);
        if(compareX1 != 0) {
            return compareX1;
        }

        int compareY1 = Integer.compare(y1, bridge.y1);
        if(compareY1 != 0) {
            return compareY1;
        }

        int compareX2 = Integer.compare(x2, bridge.x2);
        if(compareX2 != 0) {
            return compareX2;
        }

        return Integer.compare(y2, bridge.y2);
    }

    public static Bridge create(Island endpointA, Island endpointB) {
        int compare = endpointA.compareTo(endpointB);
        Island firstEndpoint = compare <= 0 ? endpointA : endpointB;
        Island secondEndpoint = compare > 0 ? endpointA : endpointB;

        return new Bridge(firstEndpoint.getX(), firstEndpoint.getY(), secondEndpoint.getX(), secondEndpoint.getY());
    }
}
