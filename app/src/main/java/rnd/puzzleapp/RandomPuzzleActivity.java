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
import rnd.puzzleapp.utils.Threading;

/**
 * An activity to generate random puzzles.
 */
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

        // Quickly generate a small bitmap as a placeholder.
        Threading.async(() -> generate(2, 2, 0), preview::setImageBitmap);
    }

    /**
     * Invoked when the user clicked the generate button.
     */
    private void onGenerate(View view) {
        String minString = minIslands.getText().toString();
        String maxString = maxIslands.getText().toString();
        String seedString = seedText.getText().toString();
        int min = minString.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(minString);
        int max = maxString.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(maxString);
        int seed = seedString.isEmpty() ? incrementalSeed++ : Integer.parseInt(seedString);

        if(min == Integer.MIN_VALUE) {
            showToast(getString(R.string.enter_min_island_count));
        } else if(max == Integer.MIN_VALUE) {
            showToast(getString(R.string.enter_max_island_count));
        } else if(min < 2) {
            showToast(getString(R.string.min_island_count_must_be_2));
        } else if(max < min) {
            showToast(getString(R.string.max_island_count_cannot_be_below_min));
        } else {
            Threading.asyncProgressDialog(this, getString(R.string.generating_puzzle),
                    () -> generate(min, max, seed),
                    preview::setImageBitmap);
        }
    }

    /**
     * Generate a new random puzzle.
     * @param min the minimum island count
     * @param max the maximum island count
     * @param seed the seed
     * @return a large thumbnail of the generated puzzle
     */
    private Bitmap generate(int min, int max, int seed) {
        PuzzleGenerator generator = new RandomPuzzleGenerator(seed, min, max);
        solution = generator.generate(true);
        puzzle = solution.copy();
        puzzle.reset();
        ThumbnailRenderer renderer = new ThumbnailRenderer(solution);
        thumbnail = renderer.drawLarge();

        return thumbnail;
    }

    /**
     * Invoked when the user clicked the save button.
     */
    private void onSave(View view) {
        String name = puzzleName.getText().toString();

        if(name.isEmpty()) {
            showToast(getString(R.string.enter_puzzle_name));
        } else if(StorageManager.puzzleExists(this, name)) {
            showToast(getString(R.string.puzzle_already_exists));
        } else {
            Threading.asyncProgressDialog(this, getString(R.string.saving_puzzle),
                    () -> save(name),
                    succeeded -> showToast(succeeded
                            ? getString(R.string.puzzle_saved)
                            : getString(R.string.error_saving_puzzle))
            );
            // TODO: Back to list puzzles activity.
        }
    }

    /**
     * Attempts to save the randomly generated puzzle.
     * @param name the name to save the puzzle under
     * @return {@code true} if the puzzle was saved successfully, {@code false} otherwise
     */
    private boolean save(String name) {
        return StorageManager.save(this, StoredPuzzle.create(name, puzzle, solution, null));
    }

    /**
     * Shows a toast with the given message.
     * @param text the message of the toast
     */
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
