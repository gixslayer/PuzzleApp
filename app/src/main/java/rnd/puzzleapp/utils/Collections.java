package rnd.puzzleapp.utils;

import java.util.Iterator;
import java.util.List;

/**
 * Utility methods that work on collections.
 */
public class Collections {
    /**
     * Compares two collections using pairwise compareTo.
     * @param a the first collection
     * @param b the second collection
     * @param <T> the type of the underlying elements
     * @return The value of the first comparison unequal to zero, 1 if collection a is larger,
     * -1 if collection b is larger, or 0 if both collections are of the same size and all elements compare
     * equal pairwise.
     */
    public static <T extends Comparable<T>> int iteratorCompare(Iterable<T> a, Iterable<T> b) {
        Iterator<T> iteratorA = a.iterator();
        Iterator<T> iteratorB = b.iterator();

        while(iteratorA.hasNext() && iteratorB.hasNext()) {
            int compare = iteratorA.next().compareTo(iteratorB.next());

            if(compare != 0) {
                return compare;
            }
        }

        return iteratorA.hasNext() ? 1 : iteratorB.hasNext() ? -1 : 0;
    }

    /**
     * Inserts an element to the already sorted list, such that the list remains sorted.
     * @param list the sorted list to insert the element into
     * @param element the element to insert
     * @param <T> the type of the element
     */
    public static <T extends Comparable<T>> void addSorted(List<T> list, T element) {
        int start = 0;
        int end = list.size();
        int mid = (end - start) / 2;
        boolean found = false;

        while(start != end && !found) {
            int compare = list.get(mid).compareTo(element);

            if(compare < 0) {
                start = mid + 1;
            } else if(compare > 0) {
                end = mid;
            } else {
                found = true;
            }

            mid = start + (end - start) / 2;
        }

        list.add(mid, element);
    }

    /**
     * Checks if the collection is sorted, based on {@code compareTo}.
     * @param iterable the collection to check
     * @param <T> the type of the underlying elements
     * @return {@code true} if the collection is sorted, {@code false} otherwise.
     */
    public static <T extends Comparable<T>> boolean isSorted(Iterable<T> iterable) {
        Iterator<T> iterator = iterable.iterator();

        if(iterator.hasNext()) {
            T prev = iterator.next();

            while(iterator.hasNext()) {
                T cur = iterator.next();

                if(prev.compareTo(cur) > 0) {
                    return false;
                }

                prev = cur;
            }
        }

        return true;
    }
}
