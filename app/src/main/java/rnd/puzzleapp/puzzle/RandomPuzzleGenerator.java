package rnd.puzzleapp.puzzle;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomPuzzleGenerator implements PuzzleGenerator {
    private static final float SUBDIVISION_BIAS = .75f;
    private static final float EDGE_ADDITION_BIAS = .25f;
    private static final float NODE_ADDITION_BIAS = 2;
    private static final float BIAS_SUM = SUBDIVISION_BIAS + EDGE_ADDITION_BIAS + NODE_ADDITION_BIAS;
    private static final int MIN_NODE_OFFSET = 1;
    private static final int MAX_NODE_OFFSET = 8;

    private final Random random;
    private final int targetNodeCount;
    private final Puzzle puzzle;

    public RandomPuzzleGenerator(long seed, int minNodeCount, int maxNodeCount) {
        this.random = new Random(seed);
        this.targetNodeCount = randomInt(minNodeCount, maxNodeCount);
        this.puzzle = new Puzzle();
    }

    private Bridge selectRandomEdge() {
        return selectRandomListElement(puzzle.getBridges());
    }

    private Island selectRandomNode() {
        return selectRandomListElement(puzzle.getIslands());
    }

    private <T> T selectRandomListElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    private int randomInt(int min, int max) {
        // NOTE: Both min/max are inclusive.
        return random.nextInt(max - min + 1) + min;
    }

    private Island randomOffset(Island node) {
        // TODO: Some bias to generate favourable shapes?
        int offset = randomInt(MIN_NODE_OFFSET, MAX_NODE_OFFSET);
        offset = random.nextBoolean() ? -offset : offset;
        boolean horizontalOffset = random.nextBoolean();
        int x = horizontalOffset ? node.getX() + offset : node.getX();
        int y = horizontalOffset ? node.getY() : node.getY() + offset;

        return new Island(x, y, 0);
    }

    private void subdivision() {
        Bridge edge = selectRandomEdge();
        Orientation orientation = edge.getOrientation();
        Span horizontalSpan = edge.getHorizontalSpan();
        Span verticalSpan = edge.getVerticalSpan();
        long edgeMultiplicity = puzzle.getBridgeCount(edge);

        if(horizontalSpan.size() >= 2 || verticalSpan.size() >= 2) {
            int x = orientation == Orientation.Horizontal ?
                    randomInt(horizontalSpan.getStart() + 1, horizontalSpan.getEnd() - 1) :
                    horizontalSpan.getStart();
            int y = orientation == Orientation.Horizontal ?
                    verticalSpan.getStart() :
                    randomInt(verticalSpan.getStart() + 1, verticalSpan.getEnd() - 1);

            // Remove existing edges.
            puzzle.getBridges().removeIf(edge::equals);

            // Create and add subdivided node and edges.
            Island newNode = new Island(x, y, 0);
            Bridge newEdge1 = Bridge.create(edge.getFirstEndpoint(), newNode);
            Bridge newEdge2 = Bridge.create(edge.getSecondEndpoint(), newNode);

            puzzle.getIslands().add(newNode);
            for(int i = 0; i < edgeMultiplicity; ++i) {
                puzzle.getBridges().add(newEdge1);
                puzzle.getBridges().add(newEdge2);
            }
        }
    }

    private void edgeAddition() {
        Bridge edge = selectRandomEdge();

        if(puzzle.getBridgeCount(edge) < Puzzle.MAX_BRIDGE_COUNT) {
            puzzle.getBridges().add(edge);
        }
    }

    private void nodeAddition() {
        Island node = selectRandomNode();
        Island newNode = randomOffset(node);
        Bridge newEdge = Bridge.create(node, newNode);

        if(!puzzle.getIslands().contains(newNode)
                && puzzle.getBridges().stream().noneMatch(newNode::crosses)
                && puzzle.getBridges().stream().noneMatch(newEdge::intersects)
                && puzzle.getIslands().stream().noneMatch(newEdge::crosses)) {
            puzzle.getIslands().add(newNode);
            puzzle.getBridges().add(newEdge);
        }
    }

    private void nextRound() {
        float value = random.nextFloat() * BIAS_SUM;

        if(value < SUBDIVISION_BIAS) {
            subdivision();
        } else if(value < SUBDIVISION_BIAS + EDGE_ADDITION_BIAS) {
            edgeAddition();
        } else {
            nodeAddition();
        }
    }

    private Island normalizeNode(Island node, int minX, int minY) {
        return new Island(node.getX() - minX, node.getY() - minY, node.getRequiredBridges());
    }

    private Island findNormalizedNode(Island node, List<Island> normalizedNodes, int minX, int minY) {
        return normalizedNodes.stream()
                .filter(i -> i.getX() + minX == node.getX() && i.getY() + minY == node.getY())
                .findFirst()
                .orElse(null);
    }

    private Bridge normalizeEdge(Bridge edge, List<Island> nodes, int minX, int minY) {
        Island firstEndpoint = findNormalizedNode(edge.getFirstEndpoint(), nodes, minX, minY);
        Island secondEndpoint = findNormalizedNode(edge.getSecondEndpoint(), nodes, minX, minY);

        return Bridge.create(firstEndpoint, secondEndpoint);
    }

    @Override
    public Puzzle generate(boolean keepBridges) {
        // Add 2 initial connected nodes.
        Island initialNode = new Island(0, 0, 0);
        puzzle.getIslands().add(initialNode);
        nodeAddition();

        // Keep making random permutation rounds until the target node count is hit.
        while (puzzle.getIslands().size() < targetNodeCount) {
            nextRound();
        }

        // Compute node degrees.
        List<Island> nodes = puzzle.getIslands().stream()
                .map(i -> new Island(i.getX(), i.getY(), (int)puzzle.getBridgeCount(i)))
                .collect(Collectors.toList());

        // Normalize node locations to be non-negative.
        int minX = nodes.stream().map(Island::getX).min(Integer::compare).get();
        int minY = nodes.stream().map(Island::getY).min(Integer::compare).get();

        List<Island> normalizedNodes = nodes.stream()
                .map(i -> normalizeNode(i, minX, minY))
                .collect(Collectors.toList());
        List<Bridge> normalizedEdges = puzzle.getBridges().stream()
                .map(b -> normalizeEdge(b, normalizedNodes, minX, minY))
                .collect(Collectors.toList());

        // Remove existing nodes and edges.
        puzzle.getIslands().clear();
        puzzle.getBridges().clear();

        // Add the final normalized & sorted nodes to form the puzzle.
        Collections.sort(normalizedNodes);
        puzzle.getIslands().addAll(normalizedNodes);

        // Add the final normalized & sorted edges to form a solution to the puzzle.
        if(keepBridges) {
            Collections.sort(normalizedEdges);
            puzzle.getBridges().addAll(normalizedEdges);
        }

        return puzzle;
    }
}
