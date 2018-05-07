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

/**
 * Manages operations pertaining to the local storage.
 */
public class StorageManager {
    private static final String PUZZLES_PATH = "puzzles";

    /**
     * Loads stored puzzles from the local storage.
     * @param context the context to load puzzles in
     * @return a list of all stored puzzles that loaded successfully.
     */
    public static List<StoredPuzzle> load(Context context) {
        // .flatMap(Optional::stream) would've been nice, but requires Java 9.
        return Arrays.stream(getPuzzlesPath(context).listFiles(File::isDirectory))
                .map(StoredPuzzle::load)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Loads a stored puzzle from the local storage.
     * @param context the context to load the puzzle in
     * @param name the name of the directory that contains the stored puzzle
     * @return the loaded stored puzzle, or an empty optional if the loading failed.
     */
    public static Optional<StoredPuzzle> load(Context context, String name) {
        return StoredPuzzle.load(getPuzzlePath(context, name));
    }

    /**
     * Saves a stored puzzle to the local storage.
     * @param context the context to save the puzzle in
     * @param puzzle the stored puzzle to save
     * @return {@code true} of the puzzle was saved successfully, {@code false} otherwise.
     */
    public static boolean save(Context context, StoredPuzzle puzzle) {
        return puzzle.save(getPuzzlePath(context, puzzle));
    }

    /**
     * Generates and saves all the default puzzles.
     * @param context the context to save the puzzles in
     */
    public static void generatePuzzles(Context context) {
        for (PuzzleDifficulty difficulty : PuzzleDifficulty.values()) {
            for (int i = 1; i <= 8; ++i) {
                generate(context, difficulty, i);
            }
        }
    }

    /**
     * Determines whether the default puzzles should be generated, by checking if the directory that
     * contains all the puzzles does not exists.
     * @param context the context to check in
     * @return {@code true} if the default puzzles should be generated, {@code false} otherwise.
     */
    public static boolean shouldGeneratePuzzles(Context context) {
        return !new File(context.getFilesDir(), PUZZLES_PATH).exists();
    }

    /**
     * Generates and saves a default puzzle.
     * @param context the context to save the puzzle in
     * @param difficulty the difficulty of the puzzle
     * @param i the puzzle number
     * @return {@code true} if the generated puzzle was stored successfully, {@code false} otherwise.
     */
    private static boolean generate(Context context, PuzzleDifficulty difficulty, int i) {
        // Construct a deterministic pseudo-random seed from the difficulty and puzzle number.
        long seed = ((long)difficulty.getName().hashCode() << 32L) | i;
        PuzzleGenerator generator = new RandomPuzzleGenerator(seed, difficulty.getMinNodes(), difficulty.getMaxNodes());
        Puzzle solution = generator.generate(true);
        Puzzle puzzle = solution.copy();
        puzzle.reset();
        Bitmap thumbnail = new ThumbnailRenderer(puzzle).draw();
        String name = String.format(Locale.US, "%s %d", difficulty.getName(), i);
        StoredPuzzle storedPuzzle = StoredPuzzle.create(name, puzzle, solution, thumbnail);

        return save(context, storedPuzzle);
    }

    /**
     * Deletes all the stored puzzles, including the directory containing all the stored puzzles, from
     * the local storage.
     * @param context the context to delete in
     * @return {@code true} if all puzzles where deleted successfully, {@code false} otherwise.
     */
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

    /**
     * Deletes a stored puzzle from the local storage.
     * @param context the context to delete in
     * @param puzzle the stored puzzle to delete
     * @return {@code true} if the puzzle was deleted successfully, {@code false} otherwise.
     */
    public static boolean delete(Context context, StoredPuzzle puzzle) {
        File puzzlePath = getPuzzlePath(context, puzzle);
        boolean ok = true;

        for(File file : puzzlePath.listFiles()) {
            ok &= file.delete();
        }

        ok &= puzzlePath.delete();

        return ok;
    }

    /**
     * Checks whether a stored puzzle exists on the local storage.
     * @param context the context to check in
     * @param name the name of the directory containing the stored puzzle
     * @return {@code true} if the puzzle exists, {@code false} otherwise.
     */
    public static boolean puzzleExists(Context context, String name) {
        File puzzlesPath = new File(context.getFilesDir(), PUZZLES_PATH);

        return puzzlesPath.exists() && new File(puzzlesPath, name).exists();
    }

    /**
     * Gets the directory containing all the stored puzzles, creating it if needed.
     * @param context the context to get the directory in
     * @return the directory containing all the stored puzzles.
     */
    private static File getPuzzlesPath(Context context) {
        File puzzlesPath = new File(context.getFilesDir(), PUZZLES_PATH);
        if(!puzzlesPath.exists()) {
            puzzlesPath.mkdir();
        }

        return puzzlesPath;
    }

    /**
     * Gets the directory containing the stored puzzle, creating it if needed.
     * @param context the context to get the directory ib
     * @param puzzle the stored puzzle
     * @return the directory containing the puzzle
     */
    private static File getPuzzlePath(Context context, StoredPuzzle puzzle) {
        return getPuzzlePath(context, puzzle.getName());
    }

    /**
     * Gets the directory containing the stored puzzle, creating it if needed.
     * @param context the context to get the directory ib
     * @param name the name of the directory containing the puzzle
     * @return the directory containing the puzzle
     */
    private static File getPuzzlePath(Context context, String name) {
        File puzzlePath = new File(getPuzzlesPath(context), name);
        if(!puzzlePath.exists()) {
            puzzlePath.mkdir();
        }

        return puzzlePath;
    }
}
