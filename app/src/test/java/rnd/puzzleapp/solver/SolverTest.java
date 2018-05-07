package rnd.puzzleapp.solver;

import org.junit.BeforeClass;
import org.junit.Test;

import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleGenerator;
import rnd.puzzleapp.puzzle.RandomPuzzleGenerator;

public class SolverTest {
    private static Puzzle puzzle;

    @BeforeClass
    public static void setup() {
        puzzle = generatePuzzle(2, 8);
    }

    private static Puzzle generatePuzzle(long seed, int nodes) {
        PuzzleGenerator generator = new RandomPuzzleGenerator(seed, nodes, nodes);
        Puzzle solution = generator.generate(true);
        Puzzle puzzle = solution.copy();
        puzzle.getBridges().clear();

        return puzzle;
    }

    @Test
    public void heuristic() {
        PuzzleSolver solver = new HeuristicSolver();

        SolveResult result = solver.solve(puzzle);

        System.out.printf("Heuristic solved: %s\n", result.isSolved() ? "yes" : "no");
    }

    @Test
    public void bfs() {
        PuzzleSolver solver = new BFSSolver();

        SolveResult result = solver.solve(puzzle);

        System.out.printf("BFS solved: %s\n", result.isSolved() ? "yes" : "no");
    }

    @Test
    public void dfs() {
        PuzzleSolver solver = new DFSSolver();

        SolveResult result = solver.solve(puzzle);

        System.out.printf("DFS solved: %s\n", result.isSolved() ? "yes" : "no");
    }

    @Test
    public void ids() {
        PuzzleSolver solver = new IDSSolver();

        SolveResult result = solver.solve(puzzle);

        System.out.printf("IDS solved: %s\n", result.isSolved() ? "yes" : "no");
    }

    @Test
    public void smart() {
        PuzzleSolver solver = new SmartSolver();

        SolveResult result = solver.solve(puzzle);

        System.out.printf("Smart solved: %s\n", result.isSolved() ? "yes" : "no");
    }

    @Test
    public void heuristicLarge() {
        PuzzleSolver solver = new HeuristicSolver();
        Puzzle largePuzzle = generatePuzzle(1, 16);

        SolveResult result = solver.solve(largePuzzle);

        System.out.printf("Heuristic large solved: %s\n", result.isSolved() ? "yes" : "no");
    }

    @Test
    public void smartLarge() {
        PuzzleSolver solver = new SmartSolver();
        Puzzle largePuzzle = generatePuzzle(2, 16);

        SolveResult result = solver.solve(largePuzzle);

        System.out.printf("Smart large solved: %s\n", result.isSolved() ? "yes" : "no");
    }

    @Test
    public void smartHuge() {
        PuzzleSolver solver = new SmartSolver();
        Puzzle largePuzzle = generatePuzzle(2, 32);

        SolveResult result = solver.solve(largePuzzle);

        System.out.printf("Smart huge solved: %s\n", result.isSolved() ? "yes" : "no");
    }
}
