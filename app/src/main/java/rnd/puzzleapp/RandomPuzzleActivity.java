package rnd.puzzleapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
    }

    private void onGenerate(View view) {
        int min = Integer.parseInt(minIslands.getText().toString());
        int max = Integer.parseInt(maxIslands.getText().toString());
        String seedString = seedText.getText().toString();
        int seed = seedString.isEmpty() ? incrementalSeed++ : Integer.parseInt(seedString);

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

        StoredPuzzle storedPuzzle = StoredPuzzle.create(name, puzzle, solution, thumbnail);
        StorageManager.save(this, storedPuzzle);
    }
}
