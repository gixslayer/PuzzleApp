package rnd.puzzleapp;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import rnd.puzzleapp.graphics.PuzzleRenderer;
import rnd.puzzleapp.puzzle.Puzzle;
import rnd.puzzleapp.puzzle.PuzzleStatus;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class PuzzleView extends View {
    private final ScaleGestureDetector scaleGestureDetector;
    private final GestureDetector gestureDetector;
    private PuzzleRenderer puzzleRenderer;
    private PuzzleController puzzleController;
    private float scaleFactor;
    private int activePointerId;
    private float lastTouchX;
    private float lastTouchY;
    private float posX;
    private float posY;
    private float padX;
    private float padY;
    private float minX;
    private float minY;
    private float minScaleFactor;

    public PuzzleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        this.scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.gestureDetector = new GestureDetector(context, new GestureListener());
        this.scaleFactor = 1.0f;
        this.posX = 0.0f;
        this.posY = 0.0f;
        this.padX = 0.0f;
        this.padY = 0.0f;
    }

    public void setPuzzle(Puzzle puzzle) {
        this.puzzleRenderer = new PuzzleRenderer(puzzle);
        this.puzzleController = new PuzzleController(puzzle);

        puzzleController.setOnPuzzleChangedListener(this::onPuzzleChanged);
        puzzleController.setOnSelectionChangedListener(puzzleRenderer::setSelectedIsland);
        puzzleController.setOnSelectionChangedListener((island, mode) -> invalidate());
    }

    public PuzzleController getPuzzleController() {
        return puzzleController;
    }

    private void onPuzzleChanged(Puzzle puzzle) {
        invalidate();

        if(puzzle.getStatus() == PuzzleStatus.Solved) {
            Toast.makeText(getContext(), "Puzzle solved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //Log.d("SIZE_CHANGED", String.format("w=%d h=%d oldw=%d oldh=%d gw=%d gh=%d", w, h, oldw, oldh, getWidth(), getHeight()));

        float minXScaleFactor = getWidth() / puzzleRenderer.getWidth();
        float minYScaleFactor = getHeight() / puzzleRenderer.getHeight();
        minScaleFactor = Math.min(minXScaleFactor, minYScaleFactor);
        minX = getWidth() - puzzleRenderer.getWidth() * scaleFactor;
        minY = getHeight() - puzzleRenderer.getHeight() * scaleFactor;
        padX = Math.max((getWidth() - puzzleRenderer.getWidth() * scaleFactor) / 2.0f, 0.0f);
        padY = Math.max((getHeight() - puzzleRenderer.getHeight() * scaleFactor) / 2.0f, 0.0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(posX + padX, posY + padY);
        canvas.scale(scaleFactor, scaleFactor);

        puzzleRenderer.draw(canvas);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                activePointerId = event.getActionIndex();
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                int pointerIndex = event.getActionIndex();
                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);
                float dx = x - lastTouchX;
                float dy = y - lastTouchY;

                //Log.d("POS", String.format("X=%f Y=%f dX=%f dY=%f", posX, posY, dx, dy));

                // Clamp x and y position to ensure the current viewport stays within the puzzle.
                posX = Math.min(Math.max(posX + dx, minX), 0);
                posY = Math.min(Math.max(posY + dy, minY), 0);

                invalidate();

                lastTouchX = x;
                lastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {
                activePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                activePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                int pointerIndex = event.getActionIndex();
                int pointerId = event.getPointerId(pointerIndex);

                if(pointerId == activePointerId) {
                    int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    lastTouchX = event.getX(newPointerIndex);
                    lastTouchY = event.getY(newPointerIndex);
                    activePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //Log.d("SCALE", "" + detector.getScaleFactor());

            // Clamp the scale factor so that the user cannot zoom out further if the entire puzzle
            // is already visible.
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(scaleFactor, minScaleFactor);

            // TODO: keep focus point in-place.
            float focusX = posX + detector.getFocusX() - padX;
            float focusY = posY + detector.getFocusY() - padY;
            float newFocusX = focusX * scaleFactor;
            float newFocusY = focusY * scaleFactor;
            float dX = newFocusX - focusX;
            float dY = newFocusY - focusX;

            // Compute drag bounds for the new scale factor.
            minX = getWidth() - puzzleRenderer.getWidth() * scaleFactor;
            minY = getHeight() - puzzleRenderer.getHeight() * scaleFactor;

            padX = Math.max((getWidth() - puzzleRenderer.getWidth() * scaleFactor) / 2.0f, 0.0f);
            padY = Math.max((getHeight() - puzzleRenderer.getHeight() * scaleFactor) / 2.0f, 0.0f);

            //posX = Math.min(Math.max(posX + dX, minX), 0);
            //posY = Math.min(Math.max(posY + dY, minY), 0);

            invalidate();

            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            // Translate event location to puzzle coordinates.
            int x = (int)((e.getX() - posX - padX) / scaleFactor / PuzzleRenderer.CELL_SIZE);
            int y = (int)((e.getY() - posY - padY) / scaleFactor / PuzzleRenderer.CELL_SIZE);

            puzzleController.onLongPress(x, y);

            //Log.d("LONG", String.format("sx=%f sy=%f x=%d y=%d", e.getX(), e.getY(), x, y));
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Translate event location to puzzle coordinates.
            int x = (int)((e.getX() - posX - padX) / scaleFactor / PuzzleRenderer.CELL_SIZE);
            int y = (int)((e.getY() - posY - padY) / scaleFactor / PuzzleRenderer.CELL_SIZE);

            puzzleController.onSingleTap(x, y);

            //Log.d("TAP", String.format("sx=%f sy=%f x=%d y=%d", e.getX(), e.getY(), x, y));

            return super.onSingleTapConfirmed(e);
        }
    }
}
