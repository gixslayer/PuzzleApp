package rnd.puzzleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.List;

import rnd.puzzleapp.storage.StorageManager;
import rnd.puzzleapp.storage.StoredPuzzle;

public class ListPuzzlesActivity extends AppCompatActivity {
    private PuzzleAdapter puzzleAdapter;
    private String activePuzzle;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_puzzles);

        List<StoredPuzzle> storedPuzzles = StorageManager.load(this);
        puzzleAdapter = new PuzzleAdapter(this, storedPuzzles);
        activePuzzle = null;

        gridView = findViewById(R.id.grid_list_puzzles);
        gridView.setAdapter(puzzleAdapter);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> startPuzzle(puzzleAdapter.getPuzzle(i)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If a user just returned from playing a puzzle, then update that puzzle.
        if(activePuzzle != null) {
            StorageManager.load(this, activePuzzle).ifPresent(puzzle -> {
                puzzleAdapter.update(puzzle);
                gridView.invalidateViews();
            });

            activePuzzle = null;
        }
    }

    private void startPuzzle(StoredPuzzle puzzle) {
        activePuzzle = puzzle.getName();
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra(PuzzleActivity.PUZZLE_NAME_KEY, activePuzzle);

        startActivity(intent);
    }
}
