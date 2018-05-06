package rnd.puzzleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.function.Predicate;

import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleStatus;
import rnd.puzzleapp.storage.StorageManager;
import rnd.puzzleapp.storage.StoredPuzzle;
import rnd.puzzleapp.utils.Dialog;
import rnd.puzzleapp.utils.Threading;

public class ListPuzzlesActivity extends AppCompatActivity {
    private PuzzleAdapter puzzleAdapter;
    private String activePuzzle;
    private boolean generatingRandomPuzzle;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_puzzles);

        activePuzzle = null;
        generatingRandomPuzzle = false;
        puzzleAdapter = new PuzzleAdapter(this);
        gridView = findViewById(R.id.grid_list_puzzles);
        gridView.setAdapter(puzzleAdapter);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> startPuzzle(puzzleAdapter.getPuzzle(i)));
        registerForContextMenu(gridView);

        if(StorageManager.shouldGeneratePuzzles(this)) {
            generatePuzzles();
        } else {
            loadPuzzles();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(activePuzzle != null) {
            // Make a copy that is passed to the async task, as activePuzzle might be set to null before
            // the task runs, and cause a crash if passed directly.
            String activePuzzleCopy = activePuzzle;

            // If a user just returned from playing a puzzle, then update that puzzle.
            Threading.asyncProgressDialog(this, "Loading puzzle",
                    () -> StorageManager.load(this, activePuzzleCopy),
                    storedPuzzle -> storedPuzzle.ifPresent(puzzle -> {
                        puzzleAdapter.update(puzzle);
                        gridView.invalidateViews();
                    }));

            activePuzzle = null;
        } else if(generatingRandomPuzzle) {
            // If a user just returned from generating random puzzles, then reload the puzzle list.
            loadPuzzles();
            generatingRandomPuzzle = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_puzzles_options_menu, menu);

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_puzzles_context_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list_puzzles_option_recreate:
                Dialog.showConfirmationDialog(this,
                        "Recreate all puzzles",
                        "Are you sure you want to delete and recreate all puzzles? This cannot be undone, and removes all user created puzzles",
                        this::recreatePuzzles);
                return true;

            case R.id.list_puzzles_option_reset:
                Dialog.showConfirmationDialog(this,
                        "Reset all puzzles",
                        "Are you sure you want to reset all puzzles? This cannot be undone",
                        this::resetAllPuzzles);
                return true;

            case R.id.list_puzzles_option_random:
                createRandomPuzzle();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        StoredPuzzle storedPuzzle = puzzleAdapter.getPuzzle(info.position);

        switch (item.getItemId()) {
            case R.id.list_puzzles_context_play:
                startPuzzle(storedPuzzle);
                return true;

            case R.id.list_puzzles_context_solution:
                viewOrCreateSolution(storedPuzzle);
                return true;

            case R.id.list_puzzles_context_reset:
                Dialog.showConfirmationDialog(this,
                        "Reset puzzle",
                        "Are you sure you want to reset this puzzle? This cannot be undone",
                        () -> resetPuzzle(storedPuzzle));
                return true;

            case R.id.list_puzzles_context_delete:
                Dialog.showConfirmationDialog(this,
                        "Delete puzzle",
                        "Are you sure you want to delete this puzzle? This cannot be undone",
                        () -> deletePuzzle(storedPuzzle));
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void recreatePuzzles() {
        Threading.asyncProgressDialog(this, "Deleting all puzzles",
                () -> StorageManager.deleteAll(this),
                this::generatePuzzles);
    }

    private void generatePuzzles() {
        Threading.asyncProgressDialog(this, "Generating puzzles",
                () -> StorageManager.generatePuzzles(this),
                this::loadPuzzles);
    }

    private void loadPuzzles() {
        Threading.asyncProgressDialog(this, "Loading puzzles",
                () -> StorageManager.load(this),
                puzzles -> {
                    puzzleAdapter.updateAll(puzzles);
                    gridView.invalidateViews();
                });
    }

    private void resetAllPuzzles() {
        Predicate<StoredPuzzle> isModified = sp -> sp.getPuzzle().getStatus() != PuzzleStatus.Untouched;

        Threading.asyncProgressDialog(this, "Resetting all puzzles",
                () -> puzzleAdapter.getPuzzles().stream().filter(isModified).forEach(sp -> {
                    sp.getPuzzle().reset();
                    sp.markDirty();
                    StorageManager.save(this, sp);
                }),
                gridView::invalidateViews);
    }

    private void resetPuzzle(StoredPuzzle storedPuzzle) {
        Puzzle puzzle = storedPuzzle.getPuzzle();

        if(puzzle.getStatus() != PuzzleStatus.Untouched) {
            puzzle.reset();
            storedPuzzle.markDirty();

            Threading.asyncProgressDialog(this, "Saving puzzle",
                    () -> StorageManager.save(this, storedPuzzle),
                    () -> {
                        puzzleAdapter.update(storedPuzzle);
                        gridView.invalidateViews();
                    });
        }
    }

    private void deletePuzzle(StoredPuzzle puzzle) {
        puzzleAdapter.remove(puzzle);

        Threading.asyncProgressDialog(this, "Deleting puzzle",
                () -> StorageManager.delete(this, puzzle),
                gridView::invalidateViews);
    }

    private void createRandomPuzzle() {
        // Would really prefer to use a navigation drawer, but don't have the time to rewrite everything into fragments.
        activePuzzle = null;
        generatingRandomPuzzle = true;
        Intent intent = new Intent(this, RandomPuzzleActivity.class);

        startActivity(intent);
    }

    private void startPuzzle(StoredPuzzle puzzle) {
        activePuzzle = puzzle.getName();
        generatingRandomPuzzle = false;
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra(PuzzleActivity.PUZZLE_NAME_KEY, activePuzzle);
        intent.putExtra(PuzzleActivity.IS_SOLUTION_KEY, false);

        startActivity(intent);
    }

    private void startSolution(StoredPuzzle puzzle) {
        activePuzzle = null;
        generatingRandomPuzzle = false;
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra(PuzzleActivity.PUZZLE_NAME_KEY, puzzle.getName());
        intent.putExtra(PuzzleActivity.IS_SOLUTION_KEY, true);

        startActivity(intent);
    }

    private void viewOrCreateSolution(StoredPuzzle puzzle) {
        if(puzzle.getSolution().isPresent()) {
            startSolution(puzzle);
        } else {
            // TODO: Prompt for solver.
        }
    }
}
