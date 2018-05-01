package rnd.puzzleapp.storage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class StoredPuzzleAdapter extends BaseAdapter {
    private final List<StoredPuzzle> puzzles;

    public StoredPuzzleAdapter(Context context) {
        this.puzzles = StorageManager.load(context);
    }

    @Override
    public int getCount() {
        return puzzles.size();
    }

    @Override
    public Object getItem(int i) {
        return puzzles.get(i);
    }

    @Override
    public long getItemId(int i) {
        // TODO: Do we care about mapping elements to some id at all?
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
