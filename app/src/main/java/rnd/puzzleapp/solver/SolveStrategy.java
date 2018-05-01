package rnd.puzzleapp.solver;

import rnd.puzzleapp.puzzle.Puzzle;

public interface SolveStrategy {

    SolveResult solve(Puzzle puzzle);
}
