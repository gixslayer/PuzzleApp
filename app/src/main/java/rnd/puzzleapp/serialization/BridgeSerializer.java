package rnd.puzzleapp.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import rnd.puzzleapp.puzzle.Bridge;
import rnd.puzzleapp.puzzle.Island;
import rnd.puzzleapp.puzzle.Puzzle;

public class BridgeSerializer implements Serializer<Bridge> {
    public static final BridgeSerializer INSTANCE = new BridgeSerializer(null);

    private final Puzzle puzzle;

    public BridgeSerializer(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    @Override
    public void serialize(DataOutputStream stream, Bridge instance) throws IOException {
        stream.writeInt(instance.getFirstEndpoint().getX());
        stream.writeInt(instance.getFirstEndpoint().getY());
        stream.writeInt(instance.getSecondEndpoint().getX());
        stream.writeInt(instance.getSecondEndpoint().getY());
    }

    @Override
    public Bridge deserialize(DataInputStream stream) throws IOException {
        int x1 = stream.readInt();
        int y1 = stream.readInt();
        int x2 = stream.readInt();
        int y2 = stream.readInt();

        try {
            Island firstEndPoint = puzzle.getIsland(x1, y1)
                    .orElseThrow(() -> new IOException("Invalid island location"));
            Island secondEndPoint = puzzle.getIsland(x2, y2)
                    .orElseThrow(() -> new IOException("Invalid island location"));

            return new Bridge(firstEndPoint, secondEndPoint);
        } catch (Throwable e) {
            throw new IOException(e);
        }
    }
}
