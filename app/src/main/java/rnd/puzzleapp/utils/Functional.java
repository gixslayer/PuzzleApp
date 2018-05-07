package rnd.puzzleapp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * Utility methods that aid in functional style programming.
 */
public class Functional {
    /**
     * Performs an operation if {@code condition} is {@code true}, and returns whether the operation was performed.
     * @param condition the condition that determines if {@code action} is performed
     * @param action the operation to perform
     * @return {@code condition}
     */
    public static boolean doIf(boolean condition, Action action) {
        if(condition) {
            action.perform();
        }

        return condition;
    }

    /**
     * Applies a function to each element of the cross product of a collection.
     * @param iterable the collection to take the cross product of
     * @param function the function that accepts a pair of elements from the cross product
     * @param <T> the element type of the collection
     * @param <R> the return type of the {@code function}
     * @return A stream of the results of each {@code function} invocation
     */
    public static <T, R> Stream<R> crossApply(Iterable<T> iterable, BiFunction<T, T, R> function) {
        List<R> result = new ArrayList<>();

        for(T a : iterable) {
            for(T b : iterable) {
                result.add(function.apply(a, b));

            }
        }

        return result.stream();
    }

    /**
     * Repeats the given {@code action} {@code n} times.
     * @param n the amount of times to repeat the action
     * @param action the action to perform
     */
    public static void repeatN(int n, Action action) {
        for(int i = 0; i < n; ++i) {
            action.perform();
        }
    }
}
