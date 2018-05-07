package rnd.puzzleapp.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import rnd.puzzleapp.puzzle.Puzzle;

/**
 * Serializes {@link Puzzle} instances to/from data streams.
 */
public class PuzzleSerializer implements Serializer<Puzzle> {
    public static final PuzzleSerializer INSTANCE = new PuzzleSerializer();

    private PuzzleSerializer() {
        // NOTE: Private constructor for the Singleton pattern.
    }

    @Override
    public void serialize(DataOutputStream stream, Puzzle instance) throws IOException {
        IslandSerializer.INSTANCE.serializeCollection(stream, instance.getIslands());
        BridgeSerializer.INSTANCE.serializeCollection(stream, instance.getBridges());
    }

    @Override
    public Puzzle deserialize(DataInputStream stream) throws IOException {
        Puzzle puzzle = new Puzzle();

        IslandSerializer.INSTANCE.deserializeCollection(stream).forEach(puzzle::addIsland);
        BridgeSerializer.INSTANCE.deserializeCollection(stream).forEach(puzzle::addBridge);

        return puzzle;
    }
}
