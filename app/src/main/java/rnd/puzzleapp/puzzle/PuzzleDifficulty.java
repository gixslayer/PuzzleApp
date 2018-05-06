package rnd.puzzleapp.puzzle;

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

    public String getName() {
        return name;
    }

    public int getMinNodes() {
        return minNodes;
    }

    public int getMaxNodes() {
        return maxNodes;
    }
}
