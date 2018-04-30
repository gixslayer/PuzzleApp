package rnd.puzzleapp.utils;

import java.io.FileInputStream;
import java.io.IOException;

@FunctionalInterface
public interface FileLoader<T> {
    T load(FileInputStream stream) throws IOException;
}
