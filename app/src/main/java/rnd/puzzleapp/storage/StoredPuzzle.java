package rnd.puzzleapp.storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

public class StoredPuzzle {
    private static final String PUZZLE_NAME = "puzzle";
    private static final String SOLUTION_NAME = "solution";
    private static final String THUMBNAIL_NAME = "thumbnail";

    private final String name;
    private final Puzzle puzzle;
    private final Puzzle solution;
    private final Bitmap thumbnail;
    private boolean isDirty;

    private StoredPuzzle(String name, Puzzle puzzle, Puzzle solution, Bitmap thumbnail, boolean isDirty) {
        this.name = name;
        this.puzzle = puzzle;
        this.solution = solution;
        this.thumbnail = thumbnail;
        this.isDirty = isDirty;
    }

    public String getName() {
        return name;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public Optional<Puzzle> getSolution() {
        return Optional.ofNullable(solution);
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void markDirty() {
        isDirty = true;
    }

    public boolean save(File path) {
        File puzzlePath = new File(path, PUZZLE_NAME);
        File solutionPath = new File(path, SOLUTION_NAME);
        File thumbnailPath = new File(path, THUMBNAIL_NAME);
        boolean saved = true;

        saved &= FileSystem.save(puzzlePath, StoredPuzzle::puzzleSaver, puzzle);
        saved &= FileSystem.save(solutionPath, StoredPuzzle::puzzleSaver, solution);
        saved &= FileSystem.save(thumbnailPath, StoredPuzzle::thumbnailSaver, thumbnail);

        if(saved) {
            isDirty = false;
        }

        return saved;
    }

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

    public static StoredPuzzle create(String name, Puzzle puzzle, Puzzle solution, Bitmap thumbnail) {
        if(puzzle == null) {
            throw new IllegalArgumentException("puzzle cannot be null");
        } else if(thumbnail == null) {
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
