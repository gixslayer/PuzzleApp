package rnd.puzzleapp.utils;

/**
 * Represents an operation that accepts no input argument and returns no result.
 * Unlike most other functional interfaces, {@link Action} is expected to operate via side-effects.
 * This is a functional interface whose functional method is {@link Action#perform()}.
 */
@FunctionalInterface
public interface Action {
    /**
     * Performs this operation.
     */
    void perform();
}
