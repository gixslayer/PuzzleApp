package rnd.puzzleapp.utils;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Represents an operation that accepts a {@link FileOutputStream} and an {@code instance} to be saved to that stream.
 * @param <T> the instance type
 */
@FunctionalInterface
public interface FileSaver<T> {
    /**
     * Save the {@code instance} to the given {@code stream}.
     * @param stream the stream to save the {@code instance} to
     * @param instance the instance to save
     * @throws IOException if the saving encountered an exception
     */
    void save(FileOutputStream  stream, T instance) throws IOException;
}
