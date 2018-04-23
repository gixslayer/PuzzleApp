package rnd.puzzleapp.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Serializer<T> {
    void serialize(DataOutputStream stream, T instance) throws IOException;
    T deserialize(DataInputStream stream) throws IOException;
}
