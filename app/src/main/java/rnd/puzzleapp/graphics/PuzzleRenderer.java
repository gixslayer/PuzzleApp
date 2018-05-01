package rnd.puzzleapp.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.function.BiConsumer;

import rnd.puzzleapp.PuzzleController;
import rnd.puzzleapp.puzzle.Bridge;
import rnd.puzzleapp.puzzle.Island;
import rnd.puzzleapp.puzzle.Orientation;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.Span;

public class PuzzleRenderer {
    public static final float CELL_SIZE = 128;
    public static final float ISLAND_RADIUS = CELL_SIZE / 4;
    public static final float BRIDGE_WIDTH = CELL_SIZE / 16;
    public static final float BRIDGE_OFFSET = (CELL_SIZE - BRIDGE_WIDTH) / 2;
    public static final float TEXT_SIZE = CELL_SIZE / 2;
    public static final Paint BACKGROUND_PAINT = new Paint();
    public static final Paint TEXT_PAINT = new Paint();
    public static final Paint BRIDGE_PAINT = new Paint();
    public static final Paint ISLAND_SELECTED_PLACE_PAINT = new Paint();
    public static final Paint ISLAND_SELECTED_DELETE_PAINT = new Paint();
    public static final Paint ISLAND_ABOVE_DEGREE_PAINT = new Paint();
    public static final Paint ISLAND_BELOW_DEGREE_PAINT = new Paint();
    public static final Paint ISLAND_ON_DEGREE_PAINT = new Paint();

    static {
        BACKGROUND_PAINT.setColor(Color.WHITE);
        BRIDGE_PAINT.setColor(Color.BLACK);
        TEXT_PAINT.setColor(Color.BLACK);
        TEXT_PAINT.setTextSize(TEXT_SIZE);
        ISLAND_SELECTED_PLACE_PAINT.setColor(Color.BLUE);
        ISLAND_SELECTED_DELETE_PAINT.setColor(Color.RED);
        ISLAND_ABOVE_DEGREE_PAINT.setColor(Color.rgb(255, 100, 0));
        ISLAND_BELOW_DEGREE_PAINT.setColor(Color.rgb(150, 150, 150));
        ISLAND_ON_DEGREE_PAINT.setColor(Color.GREEN);
    }

    private final Puzzle puzzle;
    private final float width;
    private final float height;
    private Canvas canvas;
    private Island selectedIsland;
    private PuzzleController.SelectionMode selectedMode;

    public PuzzleRenderer(Puzzle puzzle) {
        this.puzzle = puzzle;
        this.width = puzzle.getWidth() * CELL_SIZE;
        this.height = puzzle.getHeight() * CELL_SIZE;
        this.canvas = null;
        this.selectedIsland = null;
        this.selectedMode = PuzzleController.SelectionMode.place;
    }

    public void setSelectedIsland(Island island, PuzzleController.SelectionMode mode) {
        this.selectedIsland = island;
        this.selectedMode = mode;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void draw(Canvas canvas) {
        this.canvas = canvas;

        canvas.drawRect(0, 0, width, height, BACKGROUND_PAINT);

        puzzle.getBridges().stream().distinct().forEach(this::drawBridge);
        puzzle.getIslands().forEach(this::drawIsland);
    }

    private Paint getIslandPaint(Island island) {
        if(island == selectedIsland) {
            if(selectedMode == PuzzleController.SelectionMode.place) {
                return ISLAND_SELECTED_PLACE_PAINT;
            } else if(selectedMode == PuzzleController.SelectionMode.delete) {
                return ISLAND_SELECTED_DELETE_PAINT;
            }
        } else {
            long bridges = puzzle.getBridgeCount(island);
            int requiredBridges = island.getRequiredBridges();

            if(bridges < requiredBridges) {
                return ISLAND_BELOW_DEGREE_PAINT;
            } else if(bridges > requiredBridges) {
                return ISLAND_ABOVE_DEGREE_PAINT;
            } else if(bridges == requiredBridges) {
                return ISLAND_ON_DEGREE_PAINT;
            }
        }

        throw new IllegalStateException("Unable to determine island paint");
    }

    private void drawIsland(Island island) {
        Rect textBounds = new Rect();
        String text = Integer.toString(island.getRequiredBridges());
        TEXT_PAINT.getTextBounds(text, 0, text.length(), textBounds);
        float x = island.getX() * CELL_SIZE + CELL_SIZE / 2;
        float y = island.getY() * CELL_SIZE + CELL_SIZE / 2;
        float textX = x - textBounds.exactCenterX();
        float textY = y - textBounds.exactCenterY();

        canvas.drawCircle(x, y, ISLAND_RADIUS, getIslandPaint(island));
        canvas.drawText(text, textX, textY, TEXT_PAINT);
    }

    private void drawBridge(Bridge bridge) {
        Orientation orientation = bridge.getOrientation();
        long multiplicity = puzzle.getBridgeCount(bridge);
        BiConsumer<Bridge, Float> drawBridgeFunction = getDrawBridgeFunction(orientation);

        if(multiplicity == 1) {
            drawBridgeFunction.accept(bridge, BRIDGE_OFFSET);
        } else if(multiplicity == 2) {
            drawBridgeFunction.accept(bridge, BRIDGE_OFFSET-BRIDGE_WIDTH);
            drawBridgeFunction.accept(bridge, BRIDGE_OFFSET+BRIDGE_WIDTH);
        }
    }

    private BiConsumer<Bridge, Float> getDrawBridgeFunction(Orientation orientation) {
        switch (orientation) {
            case Horizontal:
                return this::drawHorizontalBridge;
            case Vertical:
                return this::drawVerticalBridge;
            default:
                throw new IllegalArgumentException("Bad orientation");
        }
    }

    private void drawHorizontalBridge(Bridge bridge, float yOffset) {
        Span horizontalSpan = bridge.getHorizontalSpan();
        float y = bridge.getY1();
        float left = horizontalSpan.getStart() * CELL_SIZE + CELL_SIZE / 2;
        float top = y * CELL_SIZE + yOffset;
        float right = horizontalSpan.getEnd() * CELL_SIZE + CELL_SIZE / 2;
        float bottom = top + BRIDGE_WIDTH;

        canvas.drawRect(left, top, right, bottom, BRIDGE_PAINT);
    }

    private void drawVerticalBridge(Bridge bridge, float xOffset) {
        Span verticalSpan = bridge.getVerticalSpan();
        float x = bridge.getX1();
        float left = x * CELL_SIZE + xOffset;
        float top = verticalSpan.getStart() * CELL_SIZE + CELL_SIZE / 2;
        float right = left + BRIDGE_WIDTH;
        float bottom = verticalSpan.getEnd() * CELL_SIZE + CELL_SIZE / 2;

        canvas.drawRect(left, top, right, bottom, BRIDGE_PAINT);
    }
}
