package rnd.puzzleapp.puzzle;

/**
 * The status of a {@link Puzzle}.
 */
public enum PuzzleStatus {
    /**
     * The puzzle is currently in a solved state.
     */
    Solved,
    /**
     * The puzzle is currently in an altered, but unsolved, state.
     */
    Unsolved,
    /**
     * The puzzle has not been altered.
     */
    Untouched
}
