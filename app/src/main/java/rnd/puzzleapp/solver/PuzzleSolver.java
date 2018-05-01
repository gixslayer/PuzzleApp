package rnd.puzzleapp.solver;

import rnd.puzzleapp.puzzle.Puzzle;

public class PuzzleSolver {
    private final Puzzle puzzle;
    private final SolveStrategy strategy;

    public PuzzleSolver(Puzzle puzzle, SolveStrategy strategy) {
        this.puzzle = puzzle.copy();
        this.strategy = strategy;
    }

    public SolveResult solve() {
        return strategy.solve(puzzle);
    }
}
