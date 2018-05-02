package rnd.puzzleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.List;

import rnd.puzzleapp.storage.StorageManager;
import rnd.puzzleapp.storage.StoredPuzzle;

public class ListPuzzlesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_puzzles);

        GridView gridView = findViewById(R.id.grid_list_puzzles);
        List<StoredPuzzle> storedPuzzles = StorageManager.load(this);
        PuzzleAdapter puzzleAdapter = new PuzzleAdapter(this, storedPuzzles);

        gridView.setAdapter(puzzleAdapter);
    }
}
