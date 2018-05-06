package rnd.puzzleapp;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import rnd.puzzleapp.puzzle.PuzzleDifficulty;
import rnd.puzzleapp.storage.StoredPuzzle;

public class PuzzleComparator implements Comparator<StoredPuzzle> {
    public static final PuzzleComparator INSTANCE = new PuzzleComparator();

    private final Map<String, Integer> difficultyMapping;

    private PuzzleComparator() {
        this.difficultyMapping = new HashMap<>();

        int i = 0;
        for(PuzzleDifficulty difficulty : PuzzleDifficulty.values()) {
            difficultyMapping.put(difficulty.getName(), i++);
        }
    }

    @Override
    public int compare(StoredPuzzle puzzle, StoredPuzzle t1) {
        String nameA = puzzle.getName();
        String nameB = t1.getName();
        int difficultyA = getDifficulty(nameA);
        int difficultyB = getDifficulty(nameB);

        // First group on incremental difficulty, then sort on name within equal difficulty.
        if(difficultyA != difficultyB) {
            return Integer.compare(difficultyA, difficultyB);
        } else {
            return nameA.compareTo(nameB);
        }
    }

    private int getDifficulty(String name) {
        for(Map.Entry<String, Integer> entry : difficultyMapping.entrySet()) {
            if(name.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Any other puzzles (user generated) fall into the same category, and appear at the end.
        return difficultyMapping.size();
    }
}
