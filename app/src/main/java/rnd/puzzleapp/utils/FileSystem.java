package rnd.puzzleapp.utils;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class FileSystem {
    public static <T> Optional<T> load(Context context, String name, FileLoader<T> loader) {
        try (FileInputStream stream = context.openFileInput(name)) {
            return Optional.ofNullable(loader.load(stream));
        } catch (FileNotFoundException e) {
            Log.e("PUZZLE_APP", String.format("Could not find file %s: %s", name, e.getMessage()));
        } catch (IOException e) {
            Log.e("PUZZLE_APP", String.format("Could not load file %s: %s", name, e.getMessage()));
        }

        return Optional.empty();
    }

    public static <T> Optional<T> loadIfExists(Context context, String name, FileLoader<T> loader) {
        if(exists(context, name)) {
            try (FileInputStream stream = context.openFileInput(name)) {
                return Optional.ofNullable(loader.load(stream));
            } catch (FileNotFoundException e) {
                Log.e("PUZZLE_APP", String.format("Could not find file %s: %s", name, e.getMessage()));
            } catch (IOException e) {
                Log.e("PUZZLE_APP", String.format("Could not load file %s: %s", name, e.getMessage()));
            }
        }

        return Optional.empty();
    }

    public static boolean exists(Context context, String name) {
        return Arrays.stream(context.fileList()).anyMatch(name::equals);
    }
}
