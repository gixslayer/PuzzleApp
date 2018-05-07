package rnd.puzzleapp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import rnd.puzzleapp.puzzle.Bridge;
import rnd.puzzleapp.puzzle.Island;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleStatus;

import static rnd.puzzleapp.utils.Functional.doIf;

/**
 * Controller class that processes input from a {@link PuzzleView}.
 */
public class PuzzleController {
    private final Puzzle puzzle;
    private final List<Consumer<Puzzle>> puzzleChangedListeners;
    private final List<BiConsumer<Island, SelectionMode>> selectionChangedListeners;
    private Island selectedIsland;
    private SelectionMode selectedMode;
    private boolean viewOnly;

    /**
     * Creates a new controller for the given puzzle.
     * @param puzzle the puzzle
     */
    public PuzzleController(Puzzle puzzle) {
        this.puzzle = puzzle;
        this.puzzleChangedListeners = new ArrayList<>();
        this.selectionChangedListeners = new ArrayList<>();
        this.viewOnly = false;
    }

    /**
     * Registers a new callback method invoked when the puzzle changes.
     * @param puzzleChangedListener the callback method
     */
    public void setOnPuzzleChangedListener(Consumer<Puzzle> puzzleChangedListener) {
        puzzleChangedListeners.add(puzzleChangedListener);
    }

    /**
     * Registers a new callback method invoked when the island selection changes.
     * @param selectionChangedListener the callback method
     */
    public void setOnSelectionChangedListener(BiConsumer<Island, SelectionMode> selectionChangedListener) {
        selectionChangedListeners.add(selectionChangedListener);
    }

    /**
     * Sets this controller in a view only mode, in which the puzzle cannot be altered.
     * @param value the new value
     */
    public void setViewOnly(boolean value) {
        viewOnly = value;
    }

    /**
     * Checks whether this controller is in view only mode.
     * @return {@code true} if in view only mode, {@code false} otherwise
     */
    public boolean isViewOnly() {
        return viewOnly;
    }

    /**
     * View requested the puzzle to be reset.
     */
    public void resetPuzzle() {
        if(!viewOnly && puzzle.getStatus() != PuzzleStatus.Untouched) {
            puzzle.reset();

            notifyPuzzleChangedListeners(puzzle);
        }
    }

    /**
     * View detected a long press on the given puzzle coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void onLongPress(int x, int y) {
        if(!viewOnly) {
            puzzle.getIsland(x, y).ifPresent(i -> changeSelection(i, SelectionMode.delete));
            puzzle.getBridge(x, y).ifPresent(this::deleteBridge);
        }
    }

    /**
     * View detected a single tap on the given puzzle coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void onSingleTap(int x, int y) {
        if(!viewOnly) {
            puzzle.getIsland(x, y).ifPresent(this::onTapIsland);
            puzzle.getBridge(x, y).ifPresent(this::placeBridge);
        }
    }

    /**
     * The given island was just tapped.
     * @param island the island
     */
    private void onTapIsland(Island island) {
        if(selectedIsland == null) {
            changeSelection(island, SelectionMode.place);
        } else if(selectedIsland == island) {
            clearSelection();
        } else {
            completeSelection(island);
        }
    }

    /**
     * Clears the current selection.
     */
    private void clearSelection() {
        changeSelection(null, SelectionMode.place);
    }

    /**
     * Changes the current selection to the given island and mode.
     * @param island the newly selected island
     * @param selectionMode the new selection mode
     */
    private void changeSelection(Island island, SelectionMode selectionMode) {
        if(selectedIsland != island || selectedMode != selectionMode) {
            selectedIsland = island;
            selectedMode = selectionMode;

            notifySelectionChangedListeners(island, selectionMode);
        }
    }

    /**
     * The current selection was completed by the given island.
     * @param island the island that completed the selection
     */
    private void completeSelection(Island island) {
        Bridge bridge = Bridge.create(selectedIsland, island);

        if(selectedMode == SelectionMode.delete && deleteBridge(bridge)) {
            clearSelection();
        } else if(selectedMode == SelectionMode.place && placeBridge(bridge)) {
            clearSelection();
        }
    }

    /**
     * Remove the given bridge and notify listeners if the bridge was removed.
     * @param bridge the bridge to remove
     * @return {@code true} if the bridge was removed, {@code false} otherwise
     */
    private boolean deleteBridge(Bridge bridge) {
        return doIf(puzzle.deleteBridge(bridge), () -> notifyPuzzleChangedListeners(puzzle));
    }

    /**
     * Place the given bridge and notify listeners if the bridge was placed.
     * @param bridge the bridge to place
     * @return {@code true} if the bridge was placed, {@code false} otherwise
     */
    private boolean placeBridge(Bridge bridge) {
        return doIf(puzzle.placeBridge(bridge), () -> notifyPuzzleChangedListeners(puzzle));
    }

    /**
     * Notify listeners the puzzle has changed.
     * @param puzzle the changed puzzle
     */
    private void notifyPuzzleChangedListeners(Puzzle puzzle) {
        puzzleChangedListeners.forEach(l -> l.accept(puzzle));
    }

    /**
     * Notify listeners the island selection has changed.
     * @param island the newly selected island
     * @param mode the new selection mode
     */
    private void notifySelectionChangedListeners(Island island, SelectionMode mode) {
        selectionChangedListeners.forEach(l -> l.accept(island, mode));
    }

    /**
     * The island selection modes.
     */
    public enum SelectionMode {
        /**
         * Selection tries to place a bridge.
         */
        place,
        /**
         * Selection tries to delete a bridge.
         */
        delete
    }
}
