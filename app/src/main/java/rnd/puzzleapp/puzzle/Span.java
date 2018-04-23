package rnd.puzzleapp.puzzle;

import java.util.Locale;

public class Span {
    private final int start;
    private final int end;

    public Span(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean contains(int value) {
        return start < value && value < end;
    }

    public boolean overlaps(Span span) {
        return (end > span.start && start < span.end);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int size() {
        return end - start;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Span) {
            Span other = (Span)obj;

            return start == other.start && end == other.end;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return start ^ end;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%d,%d)", start, end);
    }

    public static Span fromValues(int a, int b) {
        return a < b ? new Span(a, b) : new Span(b, a);
    }
}
