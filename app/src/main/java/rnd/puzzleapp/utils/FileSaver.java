package rnd.puzzleapp.utils;

import java.io.FileOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface FileSaver<T> {
    void save(FileOutputStream  stream, T instance) throws IOException;
}
