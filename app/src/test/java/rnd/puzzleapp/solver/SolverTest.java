package rnd.puzzleapp.solver;

import org.junit.Test;

import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleGenerator;
import rnd.puzzleapp.puzzle.RandomPuzzleGenerator;

public class SolverTest {

    @Test
    public void heuristic() {
        int nodes = 16;
        PuzzleSolver solver = new HeuristicSolver();
        PuzzleGenerator generator = new RandomPuzzleGenerator(2, nodes, nodes);
        Puzzle solution = generator.generate(true);
        Puzzle puzzle = solution.copy();
        puzzle.getBridges().clear();

        SolveResult result = solver.solve(puzzle);

        System.out.printf("Heuristic solved: %s\n", result.isSolved() ? "yes" : "no");
    }

    @Test
    public void bfs() {
        int nodes = 8;
        PuzzleSolver solver = new BFSSolver();
        PuzzleGenerator generator = new RandomPuzzleGenerator(2, nodes, nodes);
        Puzzle solution = generator.generate(true);
        Puzzle puzzle = solution.copy();
        puzzle.getBridges().clear();

        SolveResult result = solver.solve(puzzle);

        System.out.printf("BFS solved: %s\n", result.isSolved() ? "yes" : "no");
    }

    @Test
    public void dfs() {
        int nodes = 8;
        PuzzleSolver solver = new DFSSolver();
        PuzzleGenerator generator = new RandomPuzzleGenerator(2, nodes, nodes);
        Puzzle solution = generator.generate(true);
        Puzzle puzzle = solution.copy();
        puzzle.getBridges().clear();

        SolveResult result = solver.solve(puzzle);

        System.out.printf("DFS solved: %s\n", result.isSolved() ? "yes" : "no");
    }

    @Test
    public void ids() {
        int nodes = 7;
        PuzzleSolver solver = new IDSSolver(16);
        PuzzleGenerator generator = new RandomPuzzleGenerator(2, nodes, nodes);
        Puzzle solution = generator.generate(true);
        Puzzle puzzle = solution.copy();
        puzzle.getBridges().clear();

        SolveResult result = solver.solve(puzzle);

        System.out.printf("IDS solved: %s\n", result.isSolved() ? "yes" : "no");
    }
}
