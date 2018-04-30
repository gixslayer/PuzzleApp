package rnd.puzzleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleGenerator;
import rnd.puzzleapp.puzzle.RandomPuzzleGenerator;

public class PuzzleActivity extends AppCompatActivity {
    public static Puzzle puzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        PuzzleView puzzleView = findViewById(R.id.puzzleview);
        puzzleView.setPuzzle(puzzle);
    }
}
