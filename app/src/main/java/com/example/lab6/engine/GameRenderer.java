package com.example.lab6.engine;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.lab6.geometry.Ship;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * The type Game renderer.
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];


    private float ratioY;
    private float ratioX;

    private Ship ship;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glFrontFace(GLES20.GL_FRONT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        ship = new Ship();

    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(viewMatrix, 0, 0, 2, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);


        ship.draw(vPMatrix);
    }


    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        ratioY = 1.0f;
        ratioX = 1.0f;
        float left = -1;
        float right = 1;
        float bottom = -1;
        float top = 1;
        float near = 1.0f;
        float far = 8.0f;
        if (width > height) {
            ratioX = (float) width / height;
            left *= ratioX;
            right *= ratioX;
        } else {
            ratioY = (float) height / width;
            bottom *= ratioY;
            top *= ratioY;
        }

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
        GameView.left = left;
        GameView.right = right;
        GameView.top = top;
        GameView.bottom = bottom;
    }

    /**
     * Load shader int.
     *
     * @param type       the type
     * @param shaderCode the shader code
     * @return the int
     */
    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
