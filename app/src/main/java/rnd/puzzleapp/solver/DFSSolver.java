package rnd.puzzleapp.solver;

import java.util.List;
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
        searchPath.clear();
        SolveResult result = expand(puzzle.copy(), puzzle.getPossibleBridges());

        return result != null ? result : new SolveResult(puzzle, false);
    }

    private SolveResult expand(Puzzle puzzle, List<Bridge> prevPossibleBridges) {
        searchPath.push(puzzle);

        if(puzzle.getStatus() == PuzzleStatus.Solved) {
            return new SolveResult(puzzle, true);
        } else if(!isSolvable(puzzle)) {
            return null;
        }

        List<Bridge> possibleBridges = puzzle.getPossibleBridges(prevPossibleBridges);

        for (Bridge move : possibleBridges) {
            Puzzle newPuzzle = puzzle.fastCopy();
            newPuzzle.addBridge(move);

            if (!searchPath.contains(newPuzzle)) {
                SolveResult result = expand(newPuzzle, possibleBridges);

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
