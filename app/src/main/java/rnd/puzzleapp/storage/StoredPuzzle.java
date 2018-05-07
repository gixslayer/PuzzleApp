package rnd.puzzleapp.storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import rnd.puzzleapp.graphics.ThumbnailRenderer;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.serialization.PuzzleSerializer;
import rnd.puzzleapp.utils.FileSystem;

/**
 * Represents a puzzle that is stored on the local storage.
 */
public class StoredPuzzle {
    private static final String PUZZLE_NAME = "puzzle";
    private static final String SOLUTION_NAME = "solution";
    private static final String THUMBNAIL_NAME = "thumbnail";

    private final String name;
    private final Puzzle puzzle;
    private final Puzzle solution;
    private Bitmap thumbnail;
    private boolean isDirty;
    private boolean isThumbnailDirty;

    private StoredPuzzle(String name, Puzzle puzzle, Puzzle solution, Bitmap thumbnail, boolean isDirty) {
        this.name = name;
        this.puzzle = puzzle;
        this.solution = solution;
        this.thumbnail = thumbnail;
        this.isDirty = isDirty;
        this.isThumbnailDirty = false;
    }

    /**
     * Returns the name of this stored puzzle.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the puzzle of this stored puzzle.
     * @return the puzzle
     */
    public Puzzle getPuzzle() {
        return puzzle;
    }

    /**
     * Returns the solution of this stored puzzle, if one exists.
     * @return the solution if one exists, or an empty optional otherwise
     */
    public Optional<Puzzle> getSolution() {
        return Optional.ofNullable(solution);
    }

    /**
     * Returns the thumbnail of this stored puzzle. Note that this thumbnail will not reflect changes
     * made to the puzzle until this stored puzzle is saved.
     * @return the thumbnail
     */
    public Bitmap getThumbnail() {
        return thumbnail;
    }

    /**
     * Checks if this stored puzzle is marked as dirty, meaning it contains unsaved changes.
     * @return {@code true} if this stored puzzle is marked dirty, {@code false} otherwise.
     */
    public boolean isDirty() {
        return isDirty;
    }

    /**
     * Marks this stored puzzle as dirty, meaning it contains unsaved changes.
     */
    public void markDirty() {
        isDirty = true;
        isThumbnailDirty = true;
    }

    /**
     * Attempts to save this stored puzzle to the local storage. If required the thumbnail is rendered
     * before writing it to local storage. If this stored puzzle is saved successfully, the dirty mark
     * is cleared.
     * @param path the directory to save the puzzle in
     * @return {@code true} if the puzzle was saved successfully, {@code false} otherwise.
     */
    public boolean save(File path) {
        File puzzlePath = new File(path, PUZZLE_NAME);
        File solutionPath = new File(path, SOLUTION_NAME);
        File thumbnailPath = new File(path, THUMBNAIL_NAME);
        boolean saved = true;

        if(isThumbnailDirty) {
            thumbnail = renderThumbnail(puzzle);
            isThumbnailDirty = false;
        }

        saved &= FileSystem.save(puzzlePath, StoredPuzzle::puzzleSaver, puzzle);
        saved &= FileSystem.save(solutionPath, StoredPuzzle::puzzleSaver, solution);
        saved &= FileSystem.save(thumbnailPath, StoredPuzzle::thumbnailSaver, thumbnail);

        if(saved) {
            isDirty = false;
        }

        return saved;
    }

    /**
     * Attempts to load a stored puzzle from the local storage. If no stored thumbnail exists one is
     * rendered from the loaded puzzle.
     * @param path the directory to load the puzzle from
     * @return the loaded puzzle, or an empty optional if the loading failed.
     */
    public static Optional<StoredPuzzle> load(File path) {
        File puzzlePath = new File(path, PUZZLE_NAME);
        File solutionPath = new File(path, SOLUTION_NAME);
        File thumbnailPath = new File(path, THUMBNAIL_NAME);

        Optional<Puzzle> loadedPuzzle = FileSystem.load(puzzlePath, StoredPuzzle::puzzleLoader);

        if(loadedPuzzle.isPresent()) {
            Optional<Puzzle> loadedSolution = FileSystem.loadIfExists(solutionPath, StoredPuzzle::puzzleLoader);
            Optional<Bitmap> loadedThumbnail = FileSystem.loadIfExists(thumbnailPath, BitmapFactory::decodeStream);

            Puzzle puzzle = loadedPuzzle.get();
            Puzzle solution = loadedSolution.orElse(null);
            Bitmap thumbnail = loadedThumbnail.orElse(renderThumbnail(puzzle));

            return Optional.of(new StoredPuzzle(path.getName(), puzzle, solution, thumbnail, false));
        }

        return Optional.empty();
    }

    /**
     * Creates a new stored puzzle.
     * @param name the name of the puzzle, which must be a valid directory name
     * @param puzzle the actual puzzle
     * @param solution the solution of the puzzle if one exists, {@code null} otherwise
     * @param thumbnail the current thumbnail of the puzzle, or {@code null} to create one
     * @return the newly created instance, which is not yet saved to local storage.
     */
    public static StoredPuzzle create(String name, @NonNull Puzzle puzzle, Puzzle solution, Bitmap thumbnail) {
        if(thumbnail == null) {
            thumbnail = renderThumbnail(puzzle);
        }

        return new StoredPuzzle(name, puzzle, solution, thumbnail, true);
    }

    private static Bitmap renderThumbnail(Puzzle puzzle) {
        return new ThumbnailRenderer(puzzle).draw();
    }

    private static Puzzle puzzleLoader(FileInputStream stream) throws IOException {
        return PuzzleSerializer.INSTANCE.deserialize(new DataInputStream(stream));
    }

    private static void puzzleSaver(FileOutputStream stream, Puzzle instance) throws IOException {
        PuzzleSerializer.INSTANCE.serialize(new DataOutputStream(stream), instance);
    }

    private static void thumbnailSaver(FileOutputStream stream, Bitmap thumbnail) throws IOException {
        // NOTE: quality is ignored with PNG format.
        if (!thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
            throw new IOException("Failed to compress thumbnail");
        }
    }
}
