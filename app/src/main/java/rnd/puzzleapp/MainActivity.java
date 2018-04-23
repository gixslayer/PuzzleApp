package rnd.puzzleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleGenerator;
import rnd.puzzleapp.puzzle.RandomPuzzleGenerator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //PuzzleGenerator generator = new RandomPuzzleGenerator(16, 3, 6);
        //Puzzle puzzle = generator.generate();
    }

    public void clicked(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
