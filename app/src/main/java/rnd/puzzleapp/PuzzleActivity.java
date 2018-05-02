package rnd.puzzleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Optional;

import rnd.puzzleapp.storage.StorageManager;
import rnd.puzzleapp.storage.StoredPuzzle;

public class PuzzleActivity extends AppCompatActivity {
    public static final String PUZZLE_NAME_KEY = "puzzle_name";

    private StoredPuzzle storedPuzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        String puzzleName = getIntent().getStringExtra(PUZZLE_NAME_KEY);
        Optional<StoredPuzzle> puzzle = StorageManager.load(this, puzzleName);

        setTitle("PuzzleApp - " + puzzleName);

        if(!puzzle.isPresent()) {
            // TODO: Handle error.
            Toast.makeText(this, "Could not load puzzle", Toast.LENGTH_SHORT).show();
        } else {
            PuzzleView puzzleView = findViewById(R.id.puzzleview);
            storedPuzzle = puzzle.get();

            puzzleView.setPuzzle(storedPuzzle.getPuzzle());
            puzzleView.getPuzzleController().setOnPuzzleChangedListener(p -> storedPuzzle.markDirty());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(storedPuzzle.isDirty()) {
            StorageManager.save(this, storedPuzzle);
        }
    }
}
