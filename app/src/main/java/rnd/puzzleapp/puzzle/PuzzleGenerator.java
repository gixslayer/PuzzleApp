package rnd.puzzleapp.puzzle;

public interface PuzzleGenerator {
    default Puzzle generate() {
        return generate(true);
    }

    Puzzle generate(boolean keepBridges);
}
