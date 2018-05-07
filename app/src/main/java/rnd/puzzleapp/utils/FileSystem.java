package rnd.puzzleapp.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

/**
 * Utility methods that aid in filesystem operations.
 */
public class FileSystem {
    /**
     * Attempts to load the {@code file} using the given {@code loader}.
     * @param file the file to load from
     * @param loader the loader to use for loading from the file
     * @param <T> the type returned by the {@code loader}
     * @return the loaded instance returned by the {@code loader}, or an empty optional if the loading failed.
     */
    public static <T> Optional<T> load(File file, FileLoader<T> loader) {
        try (FileInputStream stream = new FileInputStream(file)) {
            return Optional.ofNullable(loader.load(stream));
        } catch (IOException e) {
            Log.e("PUZZLE_APP", String.format("Could not load file %s: %s", file.getName(), e.getMessage()));
        }

        return Optional.empty();
    }

    /**
     * Attempts to load the {@code file}, if it exists, using the given {@code loader}.
     * @param file the file to load from
     * @param loader the loader to use for loading from the file
     * @param <T> the type returned by the {@code loader}
     * @return the loaded instance returned by the {@code loader}, or an empty optional if either the
     * {@code file} does not exists or the loading failed.
     */
    public static <T> Optional<T> loadIfExists(File file, FileLoader<T> loader) {
        if(file.exists()) {
            try (FileInputStream stream = new FileInputStream(file)) {
                return Optional.ofNullable(loader.load(stream));
            } catch (IOException e) {
                Log.e("PUZZLE_APP", String.format("Could not load file %s: %s", file.getName(), e.getMessage()));
            }
        }

        return Optional.empty();
    }

    /**
     * Attempts to save the given {@code instance} to the {@code file} using the given {@code saver}.
     * @param file the file to save to
     * @param saver the saver to use for saving to the file
     * @param instance the instance to save
     * @param <T> the type of the instance to save
     * @return {@code true} if the {@code instance} was saved successfully, or {@code false} otherwise.
     */
    public static <T> boolean save(File file, FileSaver<T> saver, T instance) {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            saver.save(stream, instance);

            return true;
        } catch (IOException e) {
            Log.e("PUZZLE_APP", String.format("Could not save file %s: %s", file.getName(), e.getMessage()));
        }

        return false;
    }
}
