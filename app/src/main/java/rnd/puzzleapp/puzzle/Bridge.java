package rnd.puzzleapp.puzzle;

import android.support.annotation.NonNull;

import java.util.Locale;
import java.util.Optional;

public class Bridge implements Comparable<Bridge> {
    private final Island firstEndpoint;
    private final Island secondEndpoint;

    private Bridge(Island firstEndpoint, Island secondEndpoint) {
        this.firstEndpoint = firstEndpoint;
        this.secondEndpoint = secondEndpoint;
    }

    public Bridge copy(Puzzle puzzle) {
        Optional<Island> first = puzzle.getIsland(firstEndpoint.getX(), firstEndpoint.getY());
        Optional<Island> second = puzzle.getIsland(secondEndpoint.getX(), secondEndpoint.getY());

        if(!first.isPresent() || !second.isPresent()) {
            throw new IllegalArgumentException("Puzzle does not contain both endpoints");
        }

        return new Bridge(first.get(), second.get());
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
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Bridge) {
            Bridge other = (Bridge)obj;

            return firstEndpoint.equals(other.firstEndpoint) && secondEndpoint.equals(other.secondEndpoint);
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

    @Override
    public int compareTo(@NonNull Bridge bridge) {
        int compareFirst = firstEndpoint.compareTo(bridge.firstEndpoint);

        return compareFirst != 0 ? compareFirst : secondEndpoint.compareTo(bridge.secondEndpoint);
    }

    public static Bridge create(Island endpointA, Island endpointB) {
        int compare = endpointA.compareTo(endpointB);
        Island firstEndpoint = compare <= 0 ? endpointA : endpointB;
        Island secondEndpoint = compare > 0 ? endpointA : endpointB;

        return new Bridge(firstEndpoint, secondEndpoint);
    }
}
