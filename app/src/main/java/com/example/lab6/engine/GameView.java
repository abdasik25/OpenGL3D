package com.example.lab6.engine;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * The type Game view.
 */
public class GameView extends GLSurfaceView {

    private GameRenderer renderer;

    /**
     * The constant left.
     */
    public static float left;
    /**
     * The constant right.
     */
    public static float right;
    /**
     * The constant top.
     */
    public static float top;
    /**
     * The constant bottom.
     */
    public static float bottom;

    /**
     * Instantiates a new Game view.
     *
     * @param context the context
     */
    public GameView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Instantiates a new Game view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);

        renderer = new GameRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        // Render the view only when there is a change in the drawing data
    }
}
