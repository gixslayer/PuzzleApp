package rnd.puzzleapp.utils;

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
}
