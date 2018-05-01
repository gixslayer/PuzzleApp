package rnd.puzzleapp.solver;

import rnd.puzzleapp.puzzle.Puzzle;

public class SolveResult {
    private final Puzzle puzzle;
    private final boolean isSolved;

    public SolveResult(Puzzle puzzle, boolean isSolved) {
        this.puzzle = puzzle;
        this.isSolved = isSolved;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public boolean isSolved() {
        return isSolved;
    }
}
