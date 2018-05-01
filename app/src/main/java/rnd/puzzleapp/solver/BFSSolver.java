package rnd.puzzleapp.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import rnd.puzzleapp.puzzle.Bridge;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleStatus;

import static rnd.puzzleapp.utils.Collections.iteratorCompare;

public class BFSSolver implements SolveStrategy {
    private final List<Puzzle> searchSpace;
    private final Set<Puzzle> searchSpaceSet;

    public BFSSolver() {
        this.searchSpace = new ArrayList<>();
        this.searchSpaceSet = new TreeSet<>((puzzle, t1) -> iteratorCompare(puzzle.getBridges(), t1.getBridges()));
    }

    @Override
    public SolveResult solve(Puzzle puzzle) {
        searchSpace.clear();
        searchSpace.add(puzzle.copy());
        int currentIndex = 0;

        while(currentIndex < searchSpace.size()) {
            Puzzle currentPuzzle = searchSpace.get(currentIndex);

            for(Bridge bridge : currentPuzzle.getPossibleBridges()) {
                Puzzle newPuzzle = currentPuzzle.copy();
                newPuzzle.placeBridge(bridge);
                PuzzleStatus status = newPuzzle.getStatus();

                if(status == PuzzleStatus.Solved) {
                    return new SolveResult(newPuzzle, SolveState.Solved);
                } else if(shouldAddToSearchSpace(newPuzzle)) {
                    addToSearchSpace(newPuzzle);
                }
            }

            ++currentIndex;
        }

        return new SolveResult(searchSpace.get(0), SolveState.Unsolved);
    }

    private boolean shouldAddToSearchSpace(Puzzle puzzle) {
        return isSolvable(puzzle) && !searchSpaceSet.contains(puzzle);
    }

    private boolean isSolvable(Puzzle puzzle) {
        return puzzle.getIslands().stream()
                .noneMatch(i -> puzzle.getBridgeCount(i) > i.getRequiredBridges());
    }

    private void addToSearchSpace(Puzzle puzzle) {
        searchSpace.add(puzzle);
        searchSpaceSet.add(puzzle);
    }
}
