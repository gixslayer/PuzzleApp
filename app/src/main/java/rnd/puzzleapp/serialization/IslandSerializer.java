package rnd.puzzleapp.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import rnd.puzzleapp.puzzle.Island;

public class IslandSerializer implements Serializer<Island> {
    public static final IslandSerializer INSTANCE = new IslandSerializer();

    private IslandSerializer() {

    }

    @Override
    public void serialize(DataOutputStream stream, Island instance) throws IOException {
        stream.writeInt(instance.getX());
        stream.writeInt(instance.getY());
        stream.writeInt(instance.getRequiredBridges());
    }

    @Override
    public Island deserialize(DataInputStream stream) throws IOException {
        int x = stream.readInt();
        int y = stream.readInt();
        int requiredBridges = stream.readInt();

        return new Island(x, y, requiredBridges);
    }
}
