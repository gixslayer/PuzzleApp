package rnd.puzzleapp.puzzle;

/**
 * Defines an interface to generate {@link Puzzle} instances.
 */
public interface PuzzleGenerator {
    /**
     * Generate a new puzzle instance.
     * @param keepBridges determine if the generated puzzle should retain all placed bridges
     * @return the generated puzzle, which either has no bridges or is in a solved state, based on
     * the value of {@code keepBridges}.
     */
    Puzzle generate(boolean keepBridges);
}
