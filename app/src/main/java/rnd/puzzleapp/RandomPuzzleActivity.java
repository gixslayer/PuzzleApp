package rnd.puzzleapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import rnd.puzzleapp.graphics.ThumbnailRenderer;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleGenerator;
import rnd.puzzleapp.puzzle.RandomPuzzleGenerator;
import rnd.puzzleapp.storage.StorageManager;
import rnd.puzzleapp.storage.StoredPuzzle;

public class RandomPuzzleActivity extends Activity {
    private ImageView preview;
    private EditText puzzleName;
    private EditText minIslands;
    private EditText maxIslands;
    private EditText seedText;
    private Puzzle solution;
    private Puzzle puzzle;
    private Bitmap thumbnail;
    private int incrementalSeed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_puzzle);

        preview = findViewById(R.id.puzzle_preview);
        puzzleName = findViewById(R.id.puzzle_name);
        minIslands = findViewById(R.id.min_islands);
        maxIslands = findViewById(R.id.max_islands);
        seedText = findViewById(R.id.seed);
        Button generateButton = findViewById(R.id.generate_button);
        Button saveButton = findViewById(R.id.save_button);

        generateButton.setOnClickListener(this::onGenerate);
        saveButton.setOnClickListener(this::onSave);

        generate(4, 64, 0);
    }

    private void onGenerate(View view) {
        String minString = minIslands.getText().toString();
        String maxString = maxIslands.getText().toString();
        String seedString = seedText.getText().toString();
        int min = minString.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(minString);
        int max = maxString.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(maxString);
        int seed = seedString.isEmpty() ? incrementalSeed++ : Integer.parseInt(seedString);

        if(min == Integer.MIN_VALUE) {
            showToast("Please enter a minimum island count");
        } else if(max == Integer.MIN_VALUE) {
            showToast("Please enter a maximum island count");
        } else if(min < 2) {
            showToast("Minimum island count must be at least 2");
        } else if(max < min) {
            showToast("Maximum island count cannot be lower than minimum island count");
        } else {
            generate(min, max, seed);
        }
    }

    private void generate(int min, int max, int seed) {
        PuzzleGenerator generator = new RandomPuzzleGenerator(seed, min, max);
        solution = generator.generate(true);
        puzzle = solution.copy();
        puzzle.reset();
        ThumbnailRenderer renderer = new ThumbnailRenderer(solution);
        thumbnail = renderer.drawLarge();

        preview.setImageBitmap(thumbnail);
    }

    private void onSave(View view) {
        String name = puzzleName.getText().toString();

        if(name.isEmpty()) {
            showToast("Please enter a puzzle name");
        } else if(StorageManager.puzzleExists(this, name)) {
            showToast("A puzzle with this name already exists");
        } else {
            save(name);
        }
    }

    private void save(String name) {
        StoredPuzzle storedPuzzle = StoredPuzzle.create(name, puzzle, solution, thumbnail);
        if(StorageManager.save(this, storedPuzzle)) {
            showToast("Puzzle saved");
        } else {
            showToast("Error while saving puzzle");
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
