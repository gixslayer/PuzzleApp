package rnd.puzzleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rnd.puzzleapp.storage.StoredPuzzle;

/**
 * A simple adapter to populate a {@link android.widget.GridView} with puzzle instances.
 */
public class PuzzleAdapter extends BaseAdapter {
    private final Context context;
    private final List<StoredPuzzle> puzzles;

    /**
     * Creates a new adapter without any puzzles.
     * @param context the context of this adapter
     */
    public PuzzleAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    /**
     * Creates a new adapter with the given list of puzzles.
     * @param context the context of this adapter
     * @param puzzles the list of puzzles
     */
    public PuzzleAdapter(Context context, List<StoredPuzzle> puzzles) {
        this.context = context;
        this.puzzles = puzzles;

        sortPuzzles();
    }

    /**
     * Updates or adds the given puzzle.
     * @param puzzle the puzzle to update
     */
    public void update(StoredPuzzle puzzle) {
        remove(puzzle);
        puzzles.add(puzzle);
        sortPuzzles();
    }

    /**
     * Replaces all puzzles in this adapter with the given puzzles.
     * @param newPuzzles the new puzzles this adapter should contain
     */
    public void updateAll(Collection<StoredPuzzle> newPuzzles) {
        puzzles.clear();
        puzzles.addAll(newPuzzles);
        sortPuzzles();
    }

    /**
     * Remove all occurrences of the given puzzle.
     * @param puzzle the puzzle to remove
     */
    public void remove(StoredPuzzle puzzle) {
        puzzles.removeIf(p -> p.getName().equals(puzzle.getName()));
    }

    @Override
    public int getCount() {
        return puzzles.size();
    }

    @Override
    public Object getItem(int i) {
        return getPuzzle(i);
    }

    /**
     * Get the puzzle at the given position.
     * @param i the position
     * @return the puzzle at that position
     */
    public StoredPuzzle getPuzzle(int i) {
        return puzzles.get(i);
    }

    /**
     * Returns the list of puzzles in this adapter.
     * @return the list of puzzles
     */
    public List<StoredPuzzle> getPuzzles() {
        return puzzles;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        StoredPuzzle puzzle = puzzles.get(i);
        View gridItemView = view;

        if(gridItemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            gridItemView = inflater.inflate(R.layout.grid_item_list_puzzles, viewGroup, false);
        }

        TextView nameView = gridItemView.findViewById(R.id.puzzles_grid_item_name);
        TextView stateView = gridItemView.findViewById(R.id.puzzles_grid_item_state);
        ImageView thumbnailView = gridItemView.findViewById(R.id.puzzles_grid_item_thumbnail);

        nameView.setText(puzzle.getName());
        stateView.setText(puzzle.getPuzzle().getStatus().toString());
        thumbnailView.setImageBitmap(puzzle.getThumbnail());

        return gridItemView;
    }

    /**
     * Sort the puzzles based on incremental difficulty.
     */
    private void sortPuzzles() {
        puzzles.sort(PuzzleComparator.INSTANCE);
    }
}
