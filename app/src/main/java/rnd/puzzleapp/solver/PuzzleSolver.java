package rnd.puzzleapp.solver;

import rnd.puzzleapp.puzzle.Puzzle;

public interface PuzzleSolver {

    SolveResult solve(Puzzle puzzle);
}
