package com.example.lab6.geometry;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.example.lab6.engine.GameRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * The type Ship.
 */
public class Ship {

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 a_Color;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix*vPosition;" +
                    "v_Color = a_Color;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "  gl_FragColor = v_Color;" +
                    "}";

    private int mProgram;


    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private int vPMatrixHandle;
    private int positionHandle;
    private int colorHandle;

    private float[] modelMatrix = new float[16];
    private float[] mVPMatrix = new float[16];

    /**
     * The Coords per vertex.
     */
    static final int COORDS_PER_VERTEX = 6;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private float[] coords = new float[]
            {
                    -3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,//0
                    -1.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,//1
                    -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 1.0f,//2
                    1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 0.0f,//3
                    1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f,//4
                    3.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, //5
                    -1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,//6
                    -1.0f, -1.0f, 1.0f, 1.0f, 0.0f, 0.0f,//7
                    1.0f, -1.0f, 1.0f, 1.0f, 0.0f, 0.0f,//8
                    1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,//9

                    //kub

                    -0.2f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,//10
                    0.2f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,//11
                    -0.2f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,//12
                    0.2f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, //13
                    0.5f, 0.8f, 0.0f, 0.0f, 0.0f, 1.0f,//14
                    0.4f, 0.7f, 0.0f, 0.0f, 0.0f, 1.0f,//15
                    0.5f, 0.6f, 0.0f, 0.0f, 0.0f, 1.0f,//16
                    -0.2f, 0.6f, 0.0f, 0.0f, 0.0f, 1.0f,//17
                    0.2f, 0.8f, 0.0f, 0.0f, 0.0f, 1.0f, //18
                    0.4f, 0.8f, 0.0f, 1.0f, 0.0f, 0.0f,//19
                    0.4f, 0.6f, 0.0f, 1.0f, 0.0f, 0.0f,//20

                    -0.2f, 0.0f, 0.3f, 1.0f, 0.0f, 0.0f,//21
                    0.2f, 0.0f, 0.3f, 1.0f, 0.0f, 0.0f, //22
                    -0.2f, 1.0f, 0.3f, 1.0f, 0.0f, 0.0f,//23
                    0.2f, 1.0f, 0.3f, 1.0f, 0.0f, 0.0f,//24
            };

    private short drawOrder[] = {
            0, 1, 2,
            2, 1, 3,
            1, 4, 3,
            4, 5, 3,

            7, 6, 0,
            8, 6, 7,
            8, 9, 6,
            5, 9, 8,

            0, 2, 7,//������ �������� � ������
            8, 3, 5,

            7, 2, 3,//pod lodkoy
            8, 7, 3,

            0, 6, 1,//verhnie ugli
            9, 5, 4,

            1, 6, 4,//seredina vverha
            6, 9, 4,

            //flag
            10, 12, 11,//machta
            11, 12, 13,

            18, 19, 17,
            17, 19, 20,

            19, 14, 15,
            15, 16, 20,

            //zadnyaya
            11, 12, 10,
            11, 13, 12,

            18, 15, 19,
            20, 18, 17,
            15, 14, 19,
            20, 16, 15,


            //Ob'em
            22, 23, 21,
            22, 24, 23,

            11, 13, 22,
            13, 24, 22,

            12, 10, 21,
            23, 12, 21,

            12, 23, 13,
            23, 24, 13,
    }; // order to draw vertices

    /**
     * Instantiates a new Ship.
     */
    public Ship() {
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                coords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coords);
        vertexBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);


        int vertexShader = GameRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GameRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        Matrix.setIdentityM(modelMatrix, 0);
    }

    /**
     * Draw.
     *
     * @param vPMatrix the v p matrix
     */
    public void draw(float[] vPMatrix) {
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(modelMatrix, 0, angle, 0, 1, 0);
        Matrix.translateM(modelMatrix, 0, 2, 0, 0);

        Matrix.multiplyMM(mVPMatrix, 0, vPMatrix, 0, modelMatrix, 0);


        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        vertexBuffer.position(0);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, 3,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(colorHandle, 3, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(colorHandle);

        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mVPMatrix, 0);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
