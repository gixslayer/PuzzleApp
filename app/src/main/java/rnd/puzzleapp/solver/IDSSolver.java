package rnd.puzzleapp.solver;

import java.util.Stack;

import rnd.puzzleapp.puzzle.Bridge;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleStatus;

public class IDSSolver implements PuzzleSolver {
    private final Stack<Puzzle> searchPath;
    private final int maxDepthLimit;
    private int depthLimit;

    public IDSSolver(int maxDepthLimit) {
        this.searchPath = new Stack<>();
        this.maxDepthLimit = maxDepthLimit;
        this.depthLimit = 1;
    }

    @Override
    public SolveResult solve(Puzzle puzzle) {
        for(depthLimit = 1; depthLimit <= maxDepthLimit; ++depthLimit) {
            searchPath.clear();
            searchPath.push(puzzle);

            SolveResult result = expand(puzzle);

            if(result != null) {
                return result;
            }
        }

        return new SolveResult(puzzle, false);
    }

    private SolveResult expand(Puzzle puzzle) {
        if(puzzle.getStatus() == PuzzleStatus.Solved) {
            return new SolveResult(puzzle, true);
        } else if(searchPath.size() > depthLimit) {
            return null;
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
