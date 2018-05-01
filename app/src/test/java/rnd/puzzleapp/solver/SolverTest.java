package rnd.puzzleapp.solver;

import org.junit.Test;

import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleGenerator;
import rnd.puzzleapp.puzzle.RandomPuzzleGenerator;

public class SolverTest {

    @Test
    public void trySolve() {
        int nodes = 16;
        PuzzleGenerator generator = new RandomPuzzleGenerator(2, nodes, nodes);
        Puzzle solution = generator.generate(true);
        Puzzle puzzle = solution.copy();
        puzzle.getBridges().clear();

        SolveStrategy strategy = new HeuristicSolver();
        PuzzleSolver solver = new PuzzleSolver(puzzle, strategy);
        SolveResult result = solver.solve();

        System.out.printf("Status: %s", result.getState());
    }
}
