package rnd.puzzleapp.puzzle;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class BridgeTest {
    private static List<Bridge> edges = new ArrayList<>();
    private static List<Island> nodes = Arrays.asList(
            new Island(-1, -1, 0),
            new Island(0, -1, 0),
            new Island(1, -1, 0),
            new Island(-1, 0, 0),
            new Island(0, 0, 0),
            new Island(1, 0, 0),
            new Island(-1, 1, 0),
            new Island(0, 1, 0),
            new Island(1, 1, 0)
    );

    @BeforeClass
    public static void createEdges() {
        for(Island n1 : nodes) {
            for(Island n2 : nodes) {
                if(n1 == n2) continue;
                if(n1.getX() != n2.getX() && n1.getY() != n2.getY()) continue;

                //edges.add(new Bridge(n1, n2));
                edges.add(Bridge.create(n1, n2));
            }
        }
    }

    private static Island n(int i) {
        return nodes.get(i-1);
    }

    private static Bridge e(int a, int b) {
        Island n1 = n(a);
        Island n2 = n(b);
        Optional<Bridge> edge = edges.stream()
                .filter(e -> e.hasEndpoint(n1) && e.hasEndpoint(n2))
                .findFirst();

        if(!edge.isPresent()) {
            throw new IllegalArgumentException("Edge does not exists");
        }

        return edge.get();
    }

    @Test
    public void intersectsShortParallel() {
        assertFalse(e(1,2).intersects(e(2,3)));
        assertFalse(e(4,5).intersects(e(5,6)));
        assertFalse(e(7,8).intersects(e(8,9)));
        assertFalse(e(1,4).intersects(e(4,7)));
        assertFalse(e(2,5).intersects(e(5,8)));
        assertFalse(e(3,6).intersects(e(6,9)));
    }

    @Test
    public void intersectsShortPerpendicular() {
        assertFalse(e(5,2).intersects(e(5,6)));
        assertFalse(e(5,6).intersects(e(5,8)));
        assertFalse(e(5,8).intersects(e(5,4)));
        assertFalse(e(5,4).intersects(e(5,2)));
    }

    @Test
    public void intersectsLongParallel() {
        assertFalse(e(1,3).intersects(e(4,6)));
        assertFalse(e(1,3).intersects(e(7,9)));
        assertFalse(e(4,6).intersects(e(7,9)));
        assertFalse(e(1,7).intersects(e(2,8)));
        assertFalse(e(1,7).intersects(e(3,9)));
        assertFalse(e(2,8).intersects(e(3,9)));
    }

    @Test
    public void intersectsLongPerpendicular() {
        assertFalse(e(1,3).intersects(e(1,7)));
        assertFalse(e(1,3).intersects(e(2,8)));
        assertFalse(e(1,3).intersects(e(3,9)));
        assertFalse(e(4,6).intersects(e(1,7)));
        assertTrue(e(4,6).intersects(e(2,8)));
        assertFalse(e(4,6).intersects(e(3,9)));
        assertFalse(e(7,9).intersects(e(1,7)));
        assertFalse(e(7,9).intersects(e(2,8)));
        assertFalse(e(7,9).intersects(e(3,9)));
    }

    @Test
    public void intersectsShortInLongHorizontal() {
        assertTrue(e(1,2).intersects(e(1,3)));
        assertTrue(e(2,3).intersects(e(1,3)));
        assertTrue(e(4,5).intersects(e(4,6)));
        assertTrue(e(5,6).intersects(e(4,6)));
        assertTrue(e(7,8).intersects(e(7,9)));
        assertTrue(e(8,9).intersects(e(7,9)));
    }

    @Test
    public void intersectsShortInLongVertical() {
        assertTrue(e(1,4).intersects(e(1,7)));
        assertTrue(e(4,7).intersects(e(1,7)));
        assertTrue(e(2,5).intersects(e(2,8)));
        assertTrue(e(5,8).intersects(e(2,8)));
        assertTrue(e(3,6).intersects(e(3,9)));
        assertTrue(e(6,9).intersects(e(3,9)));
    }

    @Test
    public void intersectsSymmetric() {
        for(Bridge e1 : edges) {
            for(Bridge e2 : edges) {
                if(e1 == e2) continue;

                assertTrue(e1.intersects(e2) == e2.intersects(e1));
            }
        }
    }

    @Test
    public void intersectsSelf() {
        edges.forEach(e -> assertTrue(e.intersects(e)));
    }

    @Test
    public void crosses() {
        assertTrue(e(1,3).crosses(n(2)));
        assertTrue(e(4,6).crosses(n(5)));
        assertTrue(e(7,9).crosses(n(8)));
        assertTrue(e(1,7).crosses(n(4)));
        assertTrue(e(2,8).crosses(n(5)));
        assertTrue(e(3,9).crosses(n(6)));
    }

    @Test
    public void crossesEndpoints() {
        edges.forEach(e -> assertFalse(e.crosses(e.getFirstEndpoint())));
        edges.forEach(e -> assertFalse(e.crosses(e.getSecondEndpoint())));
    }
}