package rnd.puzzleapp.utils;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Represents an operation that accepts a {@link FileInputStream} and returns an instance loaded from that stream.
 * @param <T> the instance type
 */
@FunctionalInterface
public interface FileLoader<T> {
    /**
     * Load a new instance from the given {@code stream}.
     * @param stream the stream to load a new instance from
     * @return the loaded instance, or {@code null} if the loading failed.
     * @throws IOException if the loading encountered an exception
     */
    T load(FileInputStream stream) throws IOException;
}
