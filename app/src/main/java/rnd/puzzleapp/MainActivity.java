package rnd.puzzleapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import rnd.puzzleapp.graphics.ThumbnailRenderer;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleGenerator;
import rnd.puzzleapp.puzzle.RandomPuzzleGenerator;
import rnd.puzzleapp.storage.StorageManager;
import rnd.puzzleapp.storage.StoredPuzzle;

public class MainActivity extends AppCompatActivity {

    static long seed = 0;
    static Puzzle puzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        genPuzzle(null);
    }

    public void genPuzzle(View view) {
        PuzzleGenerator generator = new RandomPuzzleGenerator(++seed, 4, 64);
        puzzle = generator.generate();
        ThumbnailRenderer thumbnailRenderer = new ThumbnailRenderer(puzzle);
        Bitmap thumbnail = thumbnailRenderer.drawLarge();
        ImageView imageView = findViewById(R.id.thumbnail);

        imageView.setImageBitmap(thumbnail);

        //Puzzle p = puzzle.copy();
        //p.getBridges().clear();
        //StorageManager.save(this, StoredPuzzle.create(String.format("puzzle_%d", seed - 1), p, puzzle, thumbnail));

    }

    public void clicked(View view) {
        Intent intent = new Intent(this, PuzzleActivity.class);
        PuzzleActivity.puzzle = puzzle;
        startActivity(intent);
    }
}
