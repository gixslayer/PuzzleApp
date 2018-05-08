package rnd.puzzleapp.solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import rnd.puzzleapp.puzzle.Bridge;
import rnd.puzzleapp.puzzle.Island;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleStatus;

import static rnd.puzzleapp.utils.Collections.iteratorCompare;

public class SmartSolver implements PuzzleSolver {
    private final Set<Puzzle> searchSpaceSet;

    public SmartSolver() {
        this.searchSpaceSet = new TreeSet<>((p, t1) -> iteratorCompare(p.getBridges(), t1.getBridges()));
    }

    @Override
    public SolveResult solve(Puzzle puzzle) {
        Puzzle puzzleCopy = puzzle.copy();
        List<Bridge> possibleMoves = puzzleCopy.getPossibleBridges();
        searchSpaceSet.clear();

        if(!placeInitialForcedMoves(puzzleCopy, possibleMoves)) {
            // Unsolvable.
            return new SolveResult(puzzleCopy, false);
        }

        return trySolve(puzzleCopy, possibleMoves);
    }

    private boolean placeInitialForcedMoves(Puzzle puzzle, List<Bridge> possibleMoves) {
        List<Bridge> forcedMinDegreeOneMoves = getAllMinDegreeOneForcedMoves(puzzle, possibleMoves);
        List<Bridge> forcedCompleteMoves = getAllCompleteForcedMoves(puzzle, possibleMoves);

        for(Bridge move : forcedMinDegreeOneMoves) {
            if(!puzzle.placeBridge(move)) {
                // Unsolvable.
                return false;
            }
        }

        for(Bridge move : forcedCompleteMoves) {
            if(shouldPlace(puzzle, move) && !puzzle.placeBridge(move)) {
                // Unsolvable.
                return false;
            }
        }

        return true;
    }

    private SolveResult trySolve(Puzzle puzzle, List<Bridge> possibleMoves) {
        if(placeAllForcedMoves(puzzle, possibleMoves)) {
            if (puzzle.getStatus() == PuzzleStatus.Solved) {
                return new SolveResult(puzzle, true);
            }

            // Sort moves on heuristic that tries to determine the likelihood a move is correct based
            // on the total number of remaining bridges of both endpoints.
            List<Bridge> moves = puzzle.getPossibleBridges(possibleMoves);
            moves.sort(new BridgeComparator(puzzle));

            for (Bridge move : moves) {
                Puzzle newPuzzle = puzzle.fastCopy();
                newPuzzle.addBridge(move);

                if (!searchSpaceSet.contains(newPuzzle)) {
                    searchSpaceSet.add(newPuzzle);

                    SolveResult result = trySolve(newPuzzle.fastCopy(), moves);

                    if (result.isSolved()) {
                        return result;
                    }
                }
            }
        }

        // Unsolvable.
        return new SolveResult(puzzle, false);
    }

    private boolean placeAllForcedMoves(Puzzle puzzle, List<Bridge> possibleMoves) {
        List<Bridge> forcedMoves;

        do {
            forcedMoves = getForcedMoves(puzzle, possibleMoves);

            for (Bridge move : forcedMoves) {
                if(shouldPlace(puzzle, move) && !puzzle.placeBridge(move)) {
                    // Unsolvable.
                    return false;
                }
            }

            possibleMoves = puzzle.getPossibleBridges(possibleMoves);
        } while(!forcedMoves.isEmpty());

        return true;
    }

    private boolean shouldPlace(Puzzle puzzle, Bridge bridge) {
        return canAcceptBridge(puzzle, bridge.getX1(), bridge.getY1())
                && canAcceptBridge(puzzle, bridge.getX2(), bridge.getY2())
                && puzzle.getBridgeCount(bridge) < Puzzle.MAX_BRIDGE_COUNT;
    }

    private boolean canAcceptBridge(Puzzle puzzle, int x, int y) {
        Function<Island, Boolean> canAcceptBridge = island ->
                puzzle.getBridgeCount(island) < island.getRequiredBridges();

        return puzzle.getIsland(x, y).map(canAcceptBridge).orElse(false);
    }

    private List<Bridge> getAllCompleteForcedMoves(Puzzle puzzle, List<Bridge> possibleMoves) {
        List<Bridge> forcedMoves = new ArrayList<>();

        puzzle.getIslands().forEach(i -> {
            List<Bridge> incidentWith = getBridgesIncidentWith(possibleMoves, i);

            if(incidentWith.size() == i.getRequiredBridges()) {
                forcedMoves.addAll(incidentWith);
            }
        });

        return forcedMoves;
    }

    private List<Bridge> getAllMinDegreeOneForcedMoves(Puzzle puzzle, List<Bridge> possibleMoves) {
        List<Bridge> forcedMoves = new ArrayList<>();

        puzzle.getIslands().forEach(i -> {
            List<Bridge> incidentWith = getBridgesIncidentWith(possibleMoves, i);
            List<Bridge> distinctIncidentWith = incidentWith.stream().distinct().collect(Collectors.toList());

            if(distinctIncidentWith.size() * 2 - 1 == i.getRequiredBridges()) {
                forcedMoves.addAll(distinctIncidentWith);
            }
        });

        return forcedMoves.stream().distinct().collect(Collectors.toList());
    }

    private List<Bridge> getForcedMoves(Puzzle puzzle, List<Bridge> possibleMoves) {
        List<Bridge> forcedMoves = new ArrayList<>();

        puzzle.getIslands().forEach(i -> {
            List<Bridge> incidentWith = getBridgesIncidentWith(possibleMoves, i);
            List<Bridge> distinctIncidentWith = incidentWith.stream().distinct().collect(Collectors.toList());
            List<Bridge> distinctDegreeAboveOne = getDistinctDegreeAboveOne(puzzle, distinctIncidentWith, i);
            int remainingDegree = getRemainingDegree(puzzle, i);

            if (incidentWith.size() == remainingDegree) {
                forcedMoves.addAll(incidentWith);
            } else if(distinctIncidentWith.size() == 1) {
                forcedMoves.addAll(distinctIncidentWith);
            } else if(i.getRequiredBridges() == 1 && distinctDegreeAboveOne.size() == 1) {
                forcedMoves.addAll(distinctDegreeAboveOne);
            } else if(i.getRequiredBridges() > 1 && remainingDegree == 1
                    && distinctDegreeAboveOne.size() == 1 && allNeighborsOfDegreeOne(puzzle, i)) {
                forcedMoves.addAll(distinctDegreeAboveOne);
            }
        });

        return forcedMoves;
    }

    private boolean allNeighborsOfDegreeOne(Puzzle puzzle, Island island) {
        Predicate<Island> ofDegreeOne = i -> i.getRequiredBridges() == 1;

        return puzzle.getNeighbors(island).allMatch(ofDegreeOne);
    }

    private List<Bridge> getDistinctDegreeAboveOne(Puzzle puzzle, List<Bridge> distinct, Island island) {
        return distinct.stream().filter(b -> puzzle.getOtherEndpoint(b, island).getRequiredBridges() > 1).collect(Collectors.toList());
    }

    private List<Bridge> getBridgesIncidentWith(List<Bridge> bridges, Island island) {
        return bridges.stream().filter(b -> b.hasEndpoint(island)).collect(Collectors.toList());
    }

    private int getRemainingDegree(Puzzle puzzle, Island island) {
        return island.getRequiredBridges() - getCurrentDegree(puzzle, island);
    }

    private int getCurrentDegree(Puzzle puzzle, Island island) {
        return (int)puzzle.getBridges().stream().filter(b -> b.hasEndpoint(island)).count();
    }

    private class BridgeComparator implements Comparator<Bridge> {
        private final Puzzle puzzle;

        public BridgeComparator(Puzzle puzzle) {
            this.puzzle = puzzle;
        }

        private int getRemainingDegree(Island island) {
            return island.getRequiredBridges() - (int)puzzle.getBridgeCount(island);
        }

        private int getRemainingDegree(int x, int y) {
            return puzzle.getIsland(x, y).map(this::getRemainingDegree).orElse(0);
        }

        private int getRemainingDegree(Bridge bridge) {
            return getRemainingDegree(bridge.getX1(), bridge.getY1()) + getRemainingDegree(bridge.getX2(), bridge.getY2());
        }

        @Override
        public int compare(Bridge bridge, Bridge t1) {
            return -Integer.compare(getRemainingDegree(bridge), getRemainingDegree(t1));
        }
    }
}
