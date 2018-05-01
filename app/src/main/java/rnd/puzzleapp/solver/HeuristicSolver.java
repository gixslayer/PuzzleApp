package rnd.puzzleapp.solver;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import rnd.puzzleapp.puzzle.Bridge;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleStatus;

import static rnd.puzzleapp.utils.Collections.iteratorCompare;

public class HeuristicSolver implements SolveStrategy {
    private final Queue<State> searchSpace;
    private final Set<Puzzle> searchSpaceSet;

    public HeuristicSolver() {
        this.searchSpace = new PriorityQueue<>((state, t1) -> -Long.compare(state.heuristic, t1.heuristic));
        this.searchSpaceSet = new TreeSet<>((puzzle, t1) -> iteratorCompare(puzzle.getBridges(), t1.getBridges()));
    }

    @Override
    public SolveResult solve(Puzzle puzzle) {
        searchSpace.clear();
        searchSpace.add(new State(puzzle.copy()));

        while(!searchSpace.isEmpty()) {
            Puzzle currentPuzzle = searchSpace.remove().puzzle;

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
        }

        return new SolveResult(puzzle, SolveState.Unsolved);
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
        public Puzzle puzzle;
        public long heuristic;

        public State(Puzzle puzzle) {
            this.puzzle = puzzle;
            this.heuristic = puzzle.getIslands().stream()
                    .filter(i -> puzzle.getBridgeCount(i) == i.getRequiredBridges())
                    .count();
        }
    }
}