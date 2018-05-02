package rnd.puzzleapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Locale;

import rnd.puzzleapp.graphics.ThumbnailRenderer;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleGenerator;
import rnd.puzzleapp.puzzle.RandomPuzzleGenerator;
import rnd.puzzleapp.storage.StorageManager;
import rnd.puzzleapp.storage.StoredPuzzle;

public class MainActivity extends AppCompatActivity {

    static long seed = 0;
    static Puzzle solution;
    static Bitmap thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        genPuzzle(null);
    }

    public void genPuzzle(View view) {
        PuzzleGenerator generator = new RandomPuzzleGenerator(++seed, 4, 64);
        solution = generator.generate();
        ThumbnailRenderer thumbnailRenderer = new ThumbnailRenderer(solution);
        thumbnail = thumbnailRenderer.drawLarge();
        ImageView imageView = findViewById(R.id.thumbnail);

        imageView.setImageBitmap(thumbnail);
    }

    public void clicked(View view) {
        Puzzle puzzle = solution.copy();
        puzzle.getBridges().clear();
        String name = String.format(Locale.US, "puzzle_%d", seed - 1);

        if(StorageManager.save(this, StoredPuzzle.create(name, puzzle, solution, thumbnail))) {
            Intent intent = new Intent(this, PuzzleActivity.class);
            intent.putExtra("puzzle_name", name);
            startActivity(intent);
        }
    }
}
