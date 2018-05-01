package rnd.puzzleapp.solver;

import org.junit.Test;

import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleGenerator;
import rnd.puzzleapp.puzzle.RandomPuzzleGenerator;

public class SolverTest {

    @Test
    public void trySolve() {
        PuzzleGenerator generator = new RandomPuzzleGenerator(2, 12, 12);
        Puzzle solution = generator.generate(true);
        Puzzle puzzle = solution.copy();
        puzzle.getBridges().clear();

        PuzzleSolver solver = new PuzzleSolver(puzzle, new BFSSolver());
        SolveResult result = solver.solve();

        System.out.printf("Status: %s", result.getState());
    }
}
