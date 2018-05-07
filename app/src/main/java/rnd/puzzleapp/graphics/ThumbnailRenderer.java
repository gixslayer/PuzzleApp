package rnd.puzzleapp.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import rnd.puzzleapp.puzzle.Puzzle;

/**
 * Represents a class that renders a thumbnail for a {@link Puzzle}.
 */
public class ThumbnailRenderer {
    public static final int THUMBNAIL_SIZE = 400;
    public static final int THUMBNAIL_LARGE_SIZE = 800;

    private final PuzzleRenderer puzzleRenderer;

    public ThumbnailRenderer(Puzzle puzzle) {
        this.puzzleRenderer = new PuzzleRenderer(puzzle);
    }

    private Bitmap drawPaddedBitmap() {
        float width = puzzleRenderer.getWidth();
        float height = puzzleRenderer.getHeight();
        float size = Math.max(width, height);
        float padX = (size - width) / 2.0f;
        float padY = (size - height) / 2.0f;
        Bitmap bitmap = Bitmap.createBitmap((int)size, (int)size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawRect(0, 0, size, padY, PuzzleRenderer.BACKGROUND_PAINT);
        canvas.drawRect(0, size-padY, size, size, PuzzleRenderer.BACKGROUND_PAINT);
        canvas.drawRect(0, 0, padX, size, PuzzleRenderer.BACKGROUND_PAINT);
        canvas.drawRect(size-padX, 0, size, size, PuzzleRenderer.BACKGROUND_PAINT);
        canvas.translate(padX, padY);
        puzzleRenderer.draw(canvas);

        return bitmap;
    }

    /**
     * Renders a normal sized thumbnail for the puzzle.
     * @return the rendered thumbnail
     */
    public Bitmap draw() {
        return draw(THUMBNAIL_SIZE);
    }

    /**
     * Renders a large thumbnail for the puzzle.
     * @return the rendered thumbnail
     */
    public Bitmap drawLarge() {
        return draw(THUMBNAIL_LARGE_SIZE);
    }

    /**
     * Renders a thumbnail for the puzzle.
     * @param size the size in pixels of the thumbnail, the thumbnail is padded and centered if needed
     *             to create a square thumbnail
     * @return the rendered thumbnail
     */
    public Bitmap draw(int size) {
        Bitmap bitmap = drawPaddedBitmap();

        return Bitmap.createScaledBitmap(bitmap, size, size, true);
    }
}
