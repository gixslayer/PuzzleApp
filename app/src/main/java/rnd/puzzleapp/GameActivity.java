package rnd.puzzleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import rnd.puzzleapp.puzzle.Island;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleGenerator;
import rnd.puzzleapp.puzzle.RandomPuzzleGenerator;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        PuzzleGenerator generator = new RandomPuzzleGenerator(17, 3, 16);
        Puzzle puzzle = generator.generate();

        TableLayout tableLayout = findViewById(R.id.gameGrid);

        for(int y = 0; y < puzzle.getHeight(); ++y) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            for(int x = 0; x < puzzle.getWidth(); ++x) {
                TextView cell = new TextView(this);
                Island island = puzzle.getIsland(x, y).orElse(null);

                cell.setLayoutParams(new TableRow.LayoutParams(150, 150));
                cell.setText(island == null ? "" : island.toString());
                cell.setClickable(true);
                cell.setTag(island);
                cell.setOnClickListener(view -> {
                    Island island1 = (Island)view.getTag();
                    String str = island1 == null ? "-" : island1.toString();

                    Toast.makeText(GameActivity.this, str, Toast.LENGTH_SHORT).show();
                });

                row.addView(cell);
            }

            tableLayout.addView(row);
        }
    }
}
