package rnd.puzzleapp.puzzle;

import org.junit.Test;

import static org.junit.Assert.*;

public class SpanTest {

    @Test
    public void overlaps() {
        Span s1 = Span.fromValues(-1, 1);
        Span s2 = Span.fromValues(0, 0);
        Span s3 = Span.fromValues(-2, 0);
        Span s4 = Span.fromValues(0, 1);

        assertTrue(s1.overlaps(s1));
        assertFalse(s2.overlaps(s2));
        assertTrue(s3.overlaps(s3));
        assertTrue(s4.overlaps(s4));

        assertTrue(s1.overlaps(s2));
        assertTrue(s1.overlaps(s3));
        assertTrue(s1.overlaps(s4));
        assertFalse(s2.overlaps(s3));
        assertFalse(s2.overlaps(s4));
        assertFalse(s3.overlaps(s4));

        assertTrue(s1.overlaps(s1) == s1.overlaps(s1));
        assertTrue(s1.overlaps(s2) == s2.overlaps(s1));
        assertTrue(s1.overlaps(s3) == s3.overlaps(s1));
        assertTrue(s1.overlaps(s4) == s4.overlaps(s1));
        assertTrue(s2.overlaps(s3) == s3.overlaps(s2));
        assertTrue(s2.overlaps(s4) == s4.overlaps(s2));
        assertTrue(s3.overlaps(s4) == s4.overlaps(s3));
    }

    @Test
    public void overlaps2() {
        Span s1 = Span.fromValues(0, 1);
        Span s2 = Span.fromValues(-1, 0);
        Span s3 = Span.fromValues(-1, 1);

        assertTrue(s1.overlaps(s3));
        assertTrue(s2.overlaps(s3));
    }
}