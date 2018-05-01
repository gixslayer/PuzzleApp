package rnd.puzzleapp.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static rnd.puzzleapp.utils.Collections.addSorted;
import static rnd.puzzleapp.utils.Collections.isSorted;
import static rnd.puzzleapp.utils.Functional.repeatN;

public class CollectionsTest {

    @Test
    public void sortedAdd() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4));

        addSorted(list, 0);
        addSorted(list, 1);
        addSorted(list, -4);
        addSorted(list, 4);
        addSorted(list, 8);
        addSorted(list, 6);
        addSorted(list, 3);

        assertTrue(isSorted(list));
    }

    @Test
    public void sortedAddRandomList() {
        List<Integer> list = new ArrayList<>();
        Random random = new Random(123456789);

        repeatN(1000, () -> addSorted(list, random.nextInt()));

        assertTrue(isSorted(list));
    }
}
