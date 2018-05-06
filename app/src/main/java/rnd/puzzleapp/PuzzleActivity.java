package rnd.puzzleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Optional;

import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.storage.StorageManager;
import rnd.puzzleapp.storage.StoredPuzzle;
import rnd.puzzleapp.utils.Dialog;

public class PuzzleActivity extends AppCompatActivity {
    public static final String PUZZLE_NAME_KEY = "puzzle_name";
    public static final String IS_SOLUTION_KEY = "is_solution";

    private StoredPuzzle storedPuzzle;
    private PuzzleView puzzleView;
    private PuzzleController puzzleController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        String puzzleName = getIntent().getStringExtra(PUZZLE_NAME_KEY);
        boolean isSolution = getIntent().getBooleanExtra(IS_SOLUTION_KEY, false);
        puzzleView = findViewById(R.id.puzzleview);

        setTitle(String.format(isSolution ? "%s [solution]" : "%s", puzzleName));

        // Ideally this loading would be done in an async task, but that would break the current
        // PuzzleView.
        Optional<StoredPuzzle> puzzle = StorageManager.load(this, puzzleName);

        if(!puzzle.isPresent()) {
            // If no puzzle is supplied, it will cause a crash, so provide a dummy puzzle.
            puzzleView.setPuzzle(new Puzzle());
            Toast.makeText(this, "Could not load puzzle", Toast.LENGTH_SHORT).show();
        } else if(isSolution && !puzzle.get().getSolution().isPresent()) {
            // If no puzzle is supplied, it will cause a crash, so provide a dummy puzzle.
            puzzleView.setPuzzle(new Puzzle());
            Toast.makeText(this, "Could not find solution", Toast.LENGTH_SHORT).show();
        } else {
            storedPuzzle = puzzle.get();

            puzzleView.setPuzzle(isSolution ? storedPuzzle.getSolution().get() : storedPuzzle.getPuzzle());
            puzzleController = puzzleView.getPuzzleController();
            puzzleController.setViewOnly(isSolution);
            puzzleController.setOnPuzzleChangedListener(p -> storedPuzzle.markDirty());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(storedPuzzle.isDirty()) {
            // Not doing this in an async task as I want to make sure the puzzle is actually saved
            // before this activity returns to avoid a potential race condition where the puzzle is
            // being saved by this activity, while being loaded by the list puzzles activity.
            StorageManager.save(this, storedPuzzle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.puzzle_options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.puzzle_option_reset:
                Dialog.showConfirmationDialog(this,
                        "Reset puzzle",
                        "Are you sure you want to reset this puzzle? This cannot be undone",
                        this::resetPuzzle);
                return true;

            case R.id.puzzle_option_help:
                viewHelp();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void resetPuzzle() {
        puzzleController.resetPuzzle();
        puzzleView.invalidate();
    }

    private void viewHelp() {
        // TODO: Start game/control info activity/fragment.
    }
}
