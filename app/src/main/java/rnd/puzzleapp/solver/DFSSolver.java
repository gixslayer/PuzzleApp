package rnd.puzzleapp.solver;

import java.util.Stack;

import rnd.puzzleapp.puzzle.Bridge;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleStatus;

public class DFSSolver implements PuzzleSolver {
    private final Stack<Puzzle> searchPath;

    public DFSSolver() {
        this.searchPath = new Stack<>();
    }

    @Override
    public SolveResult solve(Puzzle puzzle) {
        searchPath.push(puzzle);

        SolveResult result = expand(puzzle);

        return result != null ? result : new SolveResult(puzzle, false);
    }

    private SolveResult expand(Puzzle puzzle) {
        if(puzzle.getStatus() == PuzzleStatus.Solved) {
            return new SolveResult(puzzle, true);
        } else if(!isSolvable(puzzle)) {
            return null;
        }

        for (Bridge move : puzzle.getPossibleBridges()) {
            Puzzle newPuzzle = puzzle.copy();
            newPuzzle.placeBridge(move);

            if (!searchPath.contains(newPuzzle)) {
                searchPath.push(newPuzzle);
                SolveResult result = expand(newPuzzle);

                if (result != null) {
                    return result;
                }

                searchPath.pop();
            }
        }

        return null;
    }

    private boolean isSolvable(Puzzle puzzle) {
        return puzzle.getIslands().stream()
                .noneMatch(i -> puzzle.getBridgeCount(i) > i.getRequiredBridges());
    }
}
