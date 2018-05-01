package rnd.puzzleapp.solver;

import org.junit.Test;

import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleGenerator;
import rnd.puzzleapp.puzzle.RandomPuzzleGenerator;

public class SolverTest {

    @Test
    public void heuristic() {
        int nodes = 16;
        PuzzleGenerator generator = new RandomPuzzleGenerator(2, nodes, nodes);
        Puzzle solution = generator.generate(true);
        Puzzle puzzle = solution.copy();
        puzzle.getBridges().clear();

        PuzzleSolver solver = new HeuristicSolver();
        SolveResult result = solver.solve(puzzle);

        System.out.printf("Heuristic solved: %s\n", result.isSolved() ? "yes" : "no");
    }

    @Test
    public void bfs() {
        int nodes = 8;
        PuzzleGenerator generator = new RandomPuzzleGenerator(2, nodes, nodes);
        Puzzle solution = generator.generate(true);
        Puzzle puzzle = solution.copy();
        puzzle.getBridges().clear();

        PuzzleSolver solver = new BFSSolver();
        SolveResult result = solver.solve(puzzle);

        System.out.printf("BFS solved: %s\n", result.isSolved() ? "yes" : "no");
    }
}
