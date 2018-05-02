package rnd.puzzleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Optional;

import rnd.puzzleapp.storage.StorageManager;
import rnd.puzzleapp.storage.StoredPuzzle;

public class PuzzleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        PuzzleView puzzleView = findViewById(R.id.puzzleview);
        String puzzleName = getIntent().getStringExtra("puzzle_name");
        Optional<StoredPuzzle> storedPuzzle = StorageManager.load(this, puzzleName);
        storedPuzzle.ifPresent(p -> puzzleView.setPuzzle(p.getPuzzle()));

        if(!storedPuzzle.isPresent()) {
            // TODO: Handle error.
            Toast.makeText(this, "Could not load puzzle", Toast.LENGTH_SHORT).show();
        }
    }
}
