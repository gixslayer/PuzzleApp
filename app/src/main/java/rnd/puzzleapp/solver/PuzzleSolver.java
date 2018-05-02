package rnd.puzzleapp.solver;

import rnd.puzzleapp.puzzle.Puzzle;

public interface PuzzleSolver {

    // NOTE: Any implementation should not mutate the passed Puzzle instance, mutate a copy instead.
    SolveResult solve(Puzzle puzzle);
}
