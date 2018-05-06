package rnd.puzzleapp.storage;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import rnd.puzzleapp.graphics.ThumbnailRenderer;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleDifficulty;
import rnd.puzzleapp.puzzle.PuzzleGenerator;
import rnd.puzzleapp.puzzle.RandomPuzzleGenerator;

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

    public static void generatePuzzles(Context context) {
        for (PuzzleDifficulty difficulty : PuzzleDifficulty.values()) {
            for (int i = 1; i <= 8; ++i) {
                generate(context, difficulty, i);
            }
        }
    }

    public static boolean shouldGeneratePuzzles(Context context) {
        return !new File(context.getFilesDir(), PUZZLES_PATH).exists();
    }

    private static boolean generate(Context context, PuzzleDifficulty difficulty, int i) {
        long seed = ((long)difficulty.hashCode() << 32L) | i;
        PuzzleGenerator generator = new RandomPuzzleGenerator(seed, difficulty.getMinNodes(), difficulty.getMaxNodes());
        Puzzle solution = generator.generate(true);
        Puzzle puzzle = solution.copy();
        puzzle.getBridges().clear();
        Bitmap thumbnail = new ThumbnailRenderer(puzzle).draw();
        String name = String.format(Locale.US, "%s %d", difficulty.getName(), i);
        StoredPuzzle storedPuzzle = StoredPuzzle.create(name, puzzle, solution, thumbnail);

        return save(context, storedPuzzle);
    }

    public static boolean deleteAll(Context context) {
        File puzzlesPath = getPuzzlesPath(context);
        boolean ok = true;

        for(File puzzlePath : puzzlesPath.listFiles(File::isDirectory)) {
            for(File file : puzzlePath.listFiles()) {
                ok &= file.delete();
            }

            ok &= puzzlePath.delete();
        }

        ok &= puzzlesPath.delete();

        return ok;
    }

    public static boolean delete(Context context, StoredPuzzle puzzle) {
        File puzzlePath = getPuzzlePath(context, puzzle);
        boolean ok = true;

        for(File file : puzzlePath.listFiles()) {
            ok &= file.delete();
        }

        ok &= puzzlePath.delete();

        return ok;
    }

    public static boolean puzzleExists(Context context, String name) {
        File puzzlesPath = new File(context.getFilesDir(), PUZZLES_PATH);

        return puzzlesPath.exists() && new File(puzzlesPath, name).exists();
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
