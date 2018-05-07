package rnd.puzzleapp.solver;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import rnd.puzzleapp.puzzle.Bridge;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleStatus;

import static rnd.puzzleapp.utils.Collections.iteratorCompare;

public class HeuristicSolver implements PuzzleSolver {
    private final Queue<State> searchSpace;
    private final Set<Puzzle> searchSpaceSet;

    public HeuristicSolver() {
        this.searchSpace = new PriorityQueue<>((state, t1) -> -Long.compare(state.heuristic, t1.heuristic));
        this.searchSpaceSet = new TreeSet<>((puzzle, t1) -> iteratorCompare(puzzle.getBridges(), t1.getBridges()));
    }

    @Override
    public SolveResult solve(Puzzle puzzle) {
        searchSpace.clear();
        searchSpaceSet.clear();
        searchSpace.add(new State(puzzle.copy()));

        List<Bridge> possibleBridges = puzzle.getPossibleBridges();

        while(!searchSpace.isEmpty()) {
            Puzzle currentPuzzle = searchSpace.remove().puzzle;

            for(Bridge bridge : currentPuzzle.getPossibleBridges(possibleBridges)) {
                Puzzle newPuzzle = currentPuzzle.fastCopy();
                newPuzzle.addBridge(bridge);
                PuzzleStatus status = newPuzzle.getStatus();

                if(status == PuzzleStatus.Solved) {
                    return new SolveResult(newPuzzle, true);
                } else if(shouldAddToSearchSpace(newPuzzle)) {
                    addToSearchSpace(newPuzzle);
                }
            }
        }

        return new SolveResult(puzzle, false);
    }

    private boolean shouldAddToSearchSpace(Puzzle puzzle) {
        return isSolvable(puzzle) && !searchSpaceSet.contains(puzzle);
    }

    private boolean isSolvable(Puzzle puzzle) {
        return puzzle.getIslands().stream()
                .noneMatch(i -> puzzle.getBridgeCount(i) > i.getRequiredBridges());
    }

    private void addToSearchSpace(Puzzle puzzle) {
        searchSpace.add(new State(puzzle));
        searchSpaceSet.add(puzzle);
    }

    private class State {
        Puzzle puzzle;
        long heuristic;

        State(Puzzle puzzle) {
            this.puzzle = puzzle;
            this.heuristic = puzzle.getIslands().stream()
                    .filter(i -> puzzle.getBridgeCount(i) == i.getRequiredBridges())
                    .count();
        }
    }
}
