package rnd.puzzleapp.storage;

import android.content.Context;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StorageManager {
    private static final String PUZZLES_PATH = "puzzles";

    public static List<StoredPuzzle> load(Context context) {
        // .flatMap(Optional::stream) would've been nice, but requires Java 9.
        return Arrays.stream(getPuzzlesPath(context).listFiles(File::isDirectory))
                .map(StoredPuzzle::load)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static Optional<StoredPuzzle> load(Context context, String name) {
        return StoredPuzzle.load(getPuzzlePath(context, name));
    }

    public static boolean save(Context context, StoredPuzzle puzzle) {
        return puzzle.save(getPuzzlePath(context, puzzle));
    }

    private static File getPuzzlesPath(Context context) {
        File puzzlesPath = new File(context.getFilesDir(), PUZZLES_PATH);
        if(!puzzlesPath.exists()) {
            puzzlesPath.mkdir();
        }

        return puzzlesPath;
    }

    private static File getPuzzlePath(Context context, StoredPuzzle puzzle) {
        return getPuzzlePath(context, puzzle.getName());
    }

    private static File getPuzzlePath(Context context, String name) {
        File puzzlePath = new File(getPuzzlesPath(context), name);
        if(!puzzlePath.exists()) {
            puzzlePath.mkdir();
        }

        return puzzlePath;
    }
}
