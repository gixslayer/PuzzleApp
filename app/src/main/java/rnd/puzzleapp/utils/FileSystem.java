package rnd.puzzleapp.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

public class FileSystem {
    public static <T> Optional<T> load(File file, FileLoader<T> loader) {
        try (FileInputStream stream = new FileInputStream(file)) {
            return Optional.ofNullable(loader.load(stream));
        } catch (IOException e) {
            Log.e("PUZZLE_APP", String.format("Could not load file %s: %s", file.getName(), e.getMessage()));
        }

        return Optional.empty();
    }

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
