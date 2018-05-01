package rnd.puzzleapp.solver;

import rnd.puzzleapp.puzzle.Puzzle;

public class SolveResult {
    private final Puzzle puzzle;
    private final SolveState state;

    public SolveResult(Puzzle puzzle, SolveState state) {
        this.puzzle = puzzle;
        this.state = state;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public SolveState getState() {
        return state;
    }
}
