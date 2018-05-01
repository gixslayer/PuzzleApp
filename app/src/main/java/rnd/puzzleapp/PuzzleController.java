package rnd.puzzleapp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import rnd.puzzleapp.puzzle.Bridge;
import rnd.puzzleapp.puzzle.Island;
import rnd.puzzleapp.puzzle.Puzzle;

import static rnd.puzzleapp.utils.Functional.doIf;

public class PuzzleController {
    private final Puzzle puzzle;
    private final List<Consumer<Puzzle>> puzzleChangedListeners;
    private final List<BiConsumer<Island, SelectionMode>> selectionChangedListeners;
    private Island selectedIsland;
    private SelectionMode selectedMode;

    public PuzzleController(Puzzle puzzle) {
        this.puzzle = puzzle;
        this.puzzleChangedListeners = new ArrayList<>();
        this.selectionChangedListeners = new ArrayList<>();
    }

    public void setOnPuzzleChangedListener(Consumer<Puzzle> puzzleChangedListener) {
        puzzleChangedListeners.add(puzzleChangedListener);
    }

    public void setOnSelectionChangedListener(BiConsumer<Island, SelectionMode> selectionChangedListener) {
        selectionChangedListeners.add(selectionChangedListener);
    }

    public void onLongPress(int x, int y) {
        puzzle.getIsland(x, y).ifPresent(i -> changeSelection(i, SelectionMode.delete));
        puzzle.getBridge(x, y).ifPresent(this::deleteBridge);
    }

    public void onSingleTap(int x, int y) {
        puzzle.getIsland(x, y).ifPresent(this::onTapIsland);
        puzzle.getBridge(x, y).ifPresent(this::placeBridge);
    }

    private void onTapIsland(Island island) {
        if(selectedIsland == null) {
            changeSelection(island, SelectionMode.place);
        } else if(selectedIsland == island) {
            clearSelection();
        } else {
            completeSelection(island);
        }
    }

    private void clearSelection() {
        changeSelection(null, SelectionMode.place);
    }

    private void changeSelection(Island island, SelectionMode selectionMode) {
        if(selectedIsland != island || selectedMode != selectionMode) {
            selectedIsland = island;
            selectedMode = selectionMode;

            notifySelectionChangedListeners(island, selectionMode);
        }
    }

    private void completeSelection(Island island) {
        Bridge bridge = Bridge.create(selectedIsland, island);

        if(selectedMode == SelectionMode.delete && deleteBridge(bridge)) {
            clearSelection();
        } else if(selectedMode == SelectionMode.place && placeBridge(bridge)) {
            clearSelection();
        }
    }

    private boolean deleteBridge(Bridge bridge) {
        return doIf(puzzle.deleteBridge(bridge), () -> notifyPuzzleChangedListeners(puzzle));
    }

    private boolean placeBridge(Bridge bridge) {
        return doIf(puzzle.placeBridge(bridge), () -> notifyPuzzleChangedListeners(puzzle));
    }

    private void notifyPuzzleChangedListeners(Puzzle puzzle) {
        puzzleChangedListeners.forEach(l -> l.accept(puzzle));
    }

    private void notifySelectionChangedListeners(Island island, SelectionMode mode) {
        selectionChangedListeners.forEach(l -> l.accept(island, mode));
    }

    public enum SelectionMode {
        place,
        delete
    }
}
