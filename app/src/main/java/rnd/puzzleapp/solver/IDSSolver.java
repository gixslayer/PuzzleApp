package rnd.puzzleapp.solver;

import java.util.List;
import java.util.Stack;

import rnd.puzzleapp.puzzle.Bridge;
import rnd.puzzleapp.puzzle.Island;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleStatus;

public class IDSSolver implements PuzzleSolver {
    private final Stack<Puzzle> searchPath;
    private final int maxDepthLimit;
    private int depthLimit;

    public IDSSolver() {
        this(Integer.MAX_VALUE);
    }

    public IDSSolver(int maxDepthLimit) {
        this.searchPath = new Stack<>();
        this.maxDepthLimit = maxDepthLimit;
        this.depthLimit = 1;
    }

    @Override
    public SolveResult solve(Puzzle puzzle) {
        searchPath.clear();
        Puzzle puzzleCopy = puzzle.copy();

        for(depthLimit = minDepth(puzzle); depthLimit <= maxDepthLimit; ++depthLimit) {
            searchPath.clear();

            SolveResult result = expand(puzzleCopy, puzzle.getPossibleBridges());

            if(result != null) {
                return result;
            }
        }

        return new SolveResult(puzzle, false);
    }

    private int minDepth(Puzzle puzzle) {
        // No point in trying to solve puzzles using less moves than required, so compute a lower bound.
        return puzzle.getIslands().stream()
                .map(Island::getRequiredBridges)
                .reduce(Integer::sum)
                .orElse(2) / 2;
    }

    private SolveResult expand(Puzzle puzzle, List<Bridge> prevPossibleBridges) {
        searchPath.push(puzzle);

        if(puzzle.getStatus() == PuzzleStatus.Solved) {
            return new SolveResult(puzzle, true);
        } else if(searchPath.size() > depthLimit) {
            return null;
        } else if(!isSolvable(puzzle)) {
            return null;
        }

        List<Bridge> possibleBridges = puzzle.getPossibleBridges(prevPossibleBridges);

        for (Bridge move : possibleBridges) {
            Puzzle newPuzzle = puzzle.fastCopy();
            newPuzzle.placeBridgeUnchecked(move);

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
