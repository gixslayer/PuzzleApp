package rnd.puzzleapp.puzzle;

import java.util.Locale;

public class Bridge {
    private final Island firstEndpoint;
    private final Island secondEndpoint;

    public Bridge(Island firstEndpoint, Island secondEndpoint) {
        this.firstEndpoint = firstEndpoint;
        this.secondEndpoint = secondEndpoint;
    }

    public boolean hasEndpoint(Island island) {
        return firstEndpoint == island || secondEndpoint == island;
    }

    public Island getOtherEndpoint(Island island) {
        return firstEndpoint == island ? secondEndpoint : firstEndpoint;
    }

    public Island getFirstEndpoint() {
        return firstEndpoint;
    }

    public Island getSecondEndpoint() {
        return secondEndpoint;
    }

    public Orientation getOrientation() {
        if(!isStraight()) {
            throw new IllegalStateException("Bridge must be straight");
        }

        return firstEndpoint.getX() == secondEndpoint.getX() ? Orientation.Vertical : Orientation.Horizontal;
    }

    public Span getHorizontalSpan() {
        if(!isStraight()) {
            throw new IllegalStateException("Bridge must be straight");
        }

        return Span.fromValues(firstEndpoint.getX(), secondEndpoint.getX());
    }

    public Span getVerticalSpan() {
        if(!isStraight()) {
            throw new IllegalStateException("Bridge must be straight");
        }

        return Span.fromValues(firstEndpoint.getY(), secondEndpoint.getY());
    }

    public boolean isStraight() {
        return firstEndpoint.getX() == secondEndpoint.getX() ^ firstEndpoint.getY() == secondEndpoint.getY();
    }

    public boolean intersects(Bridge bridge) {
        Span h1 = getHorizontalSpan();
        Span v1 = getVerticalSpan();
        Span h2 = bridge.getHorizontalSpan();
        Span v2 = bridge.getVerticalSpan();

        return (h1.overlaps(h2) || h1.equals(h2)) && (v1.overlaps(v2) || v1.equals(v2));
    }

    public boolean crosses(Island island) {
        return getHorizontalSpan().contains(island.getX()) && firstEndpoint.getY() == island.getY()
                || getVerticalSpan().contains(island.getY()) && firstEndpoint.getX() == island.getX();

        //return getHorizontalSpan().contains(island.getX()) && getVerticalSpan().contains(island.getY());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Bridge) {
            Bridge other = (Bridge)obj;

            return firstEndpoint.equals(other.firstEndpoint) && secondEndpoint.equals(other.secondEndpoint) ||
                    firstEndpoint.equals(other.secondEndpoint) && secondEndpoint.equals(other.firstEndpoint);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return firstEndpoint.hashCode() ^ secondEndpoint.hashCode();
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s - %s", firstEndpoint, secondEndpoint);
    }
}
