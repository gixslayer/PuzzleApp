package rnd.puzzleapp.utils;

import java.util.Iterator;
import java.util.List;

public class Collections {
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
