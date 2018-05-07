package rnd.puzzleapp.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import rnd.puzzleapp.puzzle.Bridge;

/**
 * Serializes {@link Bridge} instances to/from data streams.
 */
public class BridgeSerializer implements Serializer<Bridge> {
    public static final BridgeSerializer INSTANCE = new BridgeSerializer();

    private BridgeSerializer() {
        // NOTE: Private constructor for the Singleton pattern.
    }

    @Override
    public void serialize(DataOutputStream stream, Bridge instance) throws IOException {
        stream.writeInt(instance.getX1());
        stream.writeInt(instance.getY1());
        stream.writeInt(instance.getX2());
        stream.writeInt(instance.getY2());
    }

    @Override
    public Bridge deserialize(DataInputStream stream) throws IOException {
        int x1 = stream.readInt();
        int y1 = stream.readInt();
        int x2 = stream.readInt();
        int y2 = stream.readInt();

        return new Bridge(x1, y1, x2, y2);
    }
}
