package rnd.puzzleapp.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import rnd.puzzleapp.puzzle.Bridge;
import rnd.puzzleapp.puzzle.Island;
import rnd.puzzleapp.puzzle.Puzzle;

import static rnd.puzzleapp.utils.Functional.repeatN;

public class PuzzleSerializer implements Serializer<Puzzle> {
    public static final PuzzleSerializer INSTANCE = new PuzzleSerializer();

    @Override
    public void serialize(DataOutputStream stream, Puzzle instance) throws IOException {
        stream.writeInt(instance.getIslands().size());
        for(Island island : instance.getIslands()) {
            IslandSerializer.INSTANCE.serialize(stream, island);
        }

        stream.writeInt(instance.getBridges().size());
        for(Bridge bridge : instance.getBridges()) {
            BridgeSerializer.INSTANCE.serialize(stream, bridge);
        }
    }

    @Override
    public Puzzle deserialize(DataInputStream stream) throws IOException {
        Puzzle puzzle = new Puzzle();


        int numIslands = stream.readInt();
        for(int i = 0; i < numIslands; ++i) {
            puzzle.getIslands().add(IslandSerializer.INSTANCE.deserialize(stream));
        }

        int numBridges = stream.readInt();
        for(int i = 0; i < numBridges; ++i) {
            puzzle.getBridges().add(BridgeSerializer.INSTANCE.deserialize(stream));
        }

        return puzzle;
    }
}
