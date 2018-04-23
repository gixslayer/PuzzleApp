package rnd.puzzleapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import rnd.puzzleapp.puzzle.Island;
import rnd.puzzleapp.puzzle.Puzzle;

public class PuzzleAdapter extends BaseAdapter {
    private final Puzzle puzzle;
    private final Context context;

    public PuzzleAdapter(Puzzle puzzle, Context context) {
        this.puzzle = puzzle;
        this.context = context;
    }

    @Override
    public int getCount() {
        return puzzle.getWidth() * puzzle.getHeight();
    }

    @Override
    public Object getItem(int i) {
        return puzzle.getIsland(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;

        if(view == null) {
            textView = new TextView(context);
            textView.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
            //textView.setPadding(0, 0, 0, 0);
        } else {
            textView = (TextView)view;
        }

        Island island = puzzle.getIsland(i).orElse(null);

        textView.setText(island == null ? "" + i : island.toString());

        return textView;
    }
}
