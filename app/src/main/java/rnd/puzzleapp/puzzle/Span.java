package rnd.puzzleapp.puzzle;

import java.util.Locale;

/**
 * An exclusive range of integers.
 */
public class Span {
    private final int start;
    private final int end;

    private Span(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Checks if the given value is inside this span.
     * @param value the value to check
     * @return {@code true} if the value is inside this span, {@code false} otherwise.
     */
    public boolean contains(int value) {
        return start < value && value < end;
    }

    /**
     * Checks if the given span overlaps with this span.
     * @param span the span to check
     * @return {@code true} if the span overlaps with this span, {@code false} otherwise.
     */
    public boolean overlaps(Span span) {
        return end > span.start && start < span.end;
    }

    /**
     * Returns the exclusive start of this span.
     * @return the start of this span, which is never above the end of this span.
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the exclusive end of this span.
     * @return the end of this span, which is never below the start of this span.
     */
    public int getEnd() {
        return end;
    }

    /**
     * Returns the distance between the end and start of this span.
     * @return the size of this span, which may be zero.
     */
    public int size() {
        return Math.abs(end - start);
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

    /**
     * Creates a new span from the given exclusive bounds. The upper and lower bounds are automatically
     * determined by comparing {@code a} and {@code b}.
     * @param a the first bound, which may be larger than {@code b}
     * @param b the second bound, which may be smaller than {@code a}
     * @return a span from the given bounds
     */
    public static Span fromValues(int a, int b) {
        return a < b ? new Span(a, b) : new Span(b, a);
    }
}
