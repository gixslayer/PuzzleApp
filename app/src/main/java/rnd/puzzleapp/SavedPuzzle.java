package rnd.puzzleapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import rnd.puzzleapp.graphics.ThumbnailRenderer;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.serialization.PuzzleSerializer;
import rnd.puzzleapp.utils.FileSystem;

public class SavedPuzzle {
    private final String name;
    private final Puzzle puzzle;
    private final Puzzle solution;
    private final Bitmap thumbnail;

    public SavedPuzzle(String name, Puzzle puzzle, Puzzle solution, Bitmap thumbnail) {
        this.name = name;
        this.puzzle = puzzle;
        this.solution = solution;
        this.thumbnail = thumbnail;
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

    public static Optional<SavedPuzzle> load(Context context, String name) {
        Optional<Puzzle> puzzle = loadPuzzle(context, name + ".puzzle");

        if(puzzle.isPresent()) {
            Optional<Puzzle> solution = loadPuzzleIfExists(context, name + ".solution");
            Optional<Bitmap> thumbnail = loadThumbnailIfExists(context, name + ".thumbnail");

            return Optional.of(new SavedPuzzle(name, puzzle.get(), solution.orElse(null),
                    thumbnail.orElse(renderThumbnail(puzzle.get()))));
        }

        return Optional.empty();
    }

    private static Bitmap renderThumbnail(Puzzle puzzle) {
        return new ThumbnailRenderer(puzzle).draw();
    }

    private static Optional<Puzzle> loadPuzzle(Context context, String name) {
        return FileSystem.load(context, name, SavedPuzzle::puzzleLoader);
    }

    private static Optional<Puzzle> loadPuzzleIfExists(Context context, String name) {
        return FileSystem.loadIfExists(context, name, SavedPuzzle::puzzleLoader);
    }

    private static Optional<Bitmap> loadThumbnailIfExists(Context context, String name) {
        return FileSystem.loadIfExists(context, name, BitmapFactory::decodeStream);
    }

    private static Puzzle puzzleLoader(FileInputStream stream) throws IOException {
        return PuzzleSerializer.INSTANCE.deserialize(new DataInputStream(stream));
    }
}
