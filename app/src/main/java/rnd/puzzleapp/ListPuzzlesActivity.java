package rnd.puzzleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import rnd.puzzleapp.storage.StorageManager;
import rnd.puzzleapp.storage.StoredPuzzle;

public class ListPuzzlesActivity extends AppCompatActivity {
    // TODO: Returning to this Activity from the PuzzleActivity should update the respective puzzle.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_puzzles);

        GridView gridView = findViewById(R.id.grid_list_puzzles);
        List<StoredPuzzle> storedPuzzles = StorageManager.load(this);
        PuzzleAdapter puzzleAdapter = new PuzzleAdapter(this, storedPuzzles);

        gridView.setAdapter(puzzleAdapter);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            StoredPuzzle puzzle = puzzleAdapter.getPuzzle(i);
            Intent intent = new Intent(this, PuzzleActivity.class);
            intent.putExtra(PuzzleActivity.PUZZLE_NAME_KEY, puzzle.getName());

            startActivity(intent);
        });
    }
}
