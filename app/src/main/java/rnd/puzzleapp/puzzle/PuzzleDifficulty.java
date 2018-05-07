package rnd.puzzleapp.puzzle;

/**
 * The difficulty of a {@link Puzzle}.
 */
public enum PuzzleDifficulty {
    // NOTE: Should be declared in increasing order of difficulty for correct sorting.
    VeryEasy("Very easy", 4, 8),
    Easy("Easy", 8, 12),
    Normal("Normal", 12, 16),
    Hard("Hard", 16, 32),
    VeryHard("Very hard", 32, 64);

    private final String name;
    private final int minNodes;
    private final int maxNodes;

    PuzzleDifficulty(String name, int minNodes, int maxNodes) {
        this.name = name;
        this.minNodes = minNodes;
        this.maxNodes = maxNodes;
    }

    /**
     * Returns the name of this difficulty.
     * @return the name, which is a valid directory name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the minimum amount of islands in this difficulty.
     * @return the minimum amount of islands (inclusive)
     */
    public int getMinNodes() {
        return minNodes;
    }

    /**
     * Returns the maximum amount of islands in this difficulty.
     * @return the maximum amount of islands (inclusive)
     */
    public int getMaxNodes() {
        return maxNodes;
    }
}
