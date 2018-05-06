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

import java.util.List;
import java.util.function.Predicate;

import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleStatus;
import rnd.puzzleapp.storage.StorageManager;
import rnd.puzzleapp.storage.StoredPuzzle;

public class ListPuzzlesActivity extends AppCompatActivity {
    private PuzzleAdapter puzzleAdapter;
    private String activePuzzle;
    private boolean generatingRandomPuzzle;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_puzzles);

        if(StorageManager.shouldGeneratePuzzles(this)) {
            StorageManager.generatePuzzles(this);
        }

        List<StoredPuzzle> storedPuzzles = StorageManager.load(this);
        puzzleAdapter = new PuzzleAdapter(this, storedPuzzles);
        activePuzzle = null;
        generatingRandomPuzzle = false;

        gridView = findViewById(R.id.grid_list_puzzles);
        gridView.setAdapter(puzzleAdapter);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> startPuzzle(puzzleAdapter.getPuzzle(i)));
        registerForContextMenu(gridView);
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
        } else if(generatingRandomPuzzle) {
            // If a user just returned from generating random puzzles, then reload the puzzle list.
            puzzleAdapter.updateAll(StorageManager.load(this));
            gridView.invalidateViews();

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
                recreatePuzzles();
                return true;

            case R.id.list_puzzles_option_reset:
                resetAllPuzzles();
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
                resetPuzzle(storedPuzzle);
                return true;

            case R.id.list_puzzles_context_delete:
                deletePuzzle(storedPuzzle);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void recreatePuzzles() {
        StorageManager.deleteAll(this);
        StorageManager.generatePuzzles(this);

        puzzleAdapter.updateAll(StorageManager.load(this));
        gridView.invalidateViews();
    }

    private void resetAllPuzzles() {
        Predicate<StoredPuzzle> isModified = sp -> sp.getPuzzle().getStatus() != PuzzleStatus.Untouched;

        puzzleAdapter.getPuzzles().stream().filter(isModified).forEach(sp -> {
            sp.getPuzzle().reset();
            sp.markDirty();
            StorageManager.save(this, sp);
        });

        gridView.invalidateViews();
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

    private void resetPuzzle(StoredPuzzle storedPuzzle) {
        Puzzle puzzle = storedPuzzle.getPuzzle();

        if(puzzle.getStatus() != PuzzleStatus.Untouched) {
            puzzle.reset();
            storedPuzzle.markDirty();

            StorageManager.save(this, storedPuzzle);
            puzzleAdapter.update(storedPuzzle);
            gridView.invalidateViews();
        }
    }

    private void deletePuzzle(StoredPuzzle puzzle) {
        puzzleAdapter.remove(puzzle);
        StorageManager.delete(this, puzzle);
        gridView.invalidateViews();
    }
}
