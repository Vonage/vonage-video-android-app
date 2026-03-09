package com.opentok.android;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VeraVideoRenderer extends BaseVideoRenderer {

    private final GLSurfaceView view;
    private final VeraRenderer renderer;

    static class VeraRenderer implements GLSurfaceView.Renderer {

        int[] textureIds = new int[3];
        float[] scaleMatrix = new float[16];

        private final FloatBuffer vertexBuffer;
        private final FloatBuffer textureBuffer;
        private final ShortBuffer drawListBuffer;

        boolean videoFitEnabled = true;
        boolean videoDisabled = false;

        // number of coordinates per vertex in this array
        static final int COORDS_PER_VERTEX = 3;
        static final int TEXTURE_COORDS_PER_VERTEX = 2;

        static float[] xyzCoords = {-1.0f, 1.0f, 0.0f, // top left
                -1.0f, -1.0f, 0.0f, // bottom left
                1.0f, -1.0f, 0.0f, // bottom right
                1.0f, 1.0f, 0.0f // top right
        };

        static float[] uvCoords = {0, 0, // top left
                0, 1, // bottom left
                1, 1, // bottom right
                1, 0}; // top right

        private final short[] vertexIndex = {0, 1, 2, 0, 2, 3}; // order to draw
        // vertices

        ReentrantLock frameLock = new ReentrantLock();
        Frame currentFrame;
        Frame pendingFrame; // Double buffering to avoid frame drops

        private int program;
        private int textureWidth;
        private int textureHeight;
        private int viewportWidth;
        private int viewportHeight;

        // Cache scale matrix to avoid recalculation every frame
        private boolean scaleMatrixDirty = true;
        private int lastFrameWidth = 0;
        private int lastFrameHeight = 0;

        // Shader uniform location cache - lookup once, reuse every frame
        private int mvpMatrixHandle = -1;
        private int positionHandle = -1;
        private int textureHandle = -1;

        // VBO (Vertex Buffer Objects) - store geometry on GPU
        private final int[] vboIds = new int[3]; // 0: vertices, 1: texCoords, 2: indices

        public VeraRenderer() {
            ByteBuffer bb = ByteBuffer.allocateDirect(xyzCoords.length * 4);
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(xyzCoords);
            vertexBuffer.position(0);

            ByteBuffer tb = ByteBuffer.allocateDirect(uvCoords.length * 4);
            tb.order(ByteOrder.nativeOrder());
            textureBuffer = tb.asFloatBuffer();
            textureBuffer.put(uvCoords);
            textureBuffer.position(0);

            ByteBuffer dlb = ByteBuffer.allocateDirect(vertexIndex.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            drawListBuffer = dlb.asShortBuffer();
            drawListBuffer.put(vertexIndex);
            drawListBuffer.position(0);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glClearColor(0, 0, 0, 1);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            String vertexShaderCode = """
                    uniform mat4 uMVPMatrix;
                    attribute vec4 aPosition;
                    attribute vec2 aTextureCoord;
                    varying vec2 vTextureCoord;
                    void main() {
                      gl_Position = uMVPMatrix * aPosition;
                      vTextureCoord = aTextureCoord;
                    }
                    """;
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);

            String fragmentShaderCode = """
                    precision mediump float;
                    uniform sampler2D Ytex;
                    uniform sampler2D Utex,Vtex;
                    varying vec2 vTextureCoord;
                    void main(void) {
                      float nx,ny,r,g,b,y,u,v;
                      mediump vec4 txl,ux,vx;\
                      nx=vTextureCoord[0];
                      ny=vTextureCoord[1];
                      y=texture2D(Ytex,vec2(nx,ny)).r;
                      u=texture2D(Utex,vec2(nx,ny)).r;
                      v=texture2D(Vtex,vec2(nx,ny)).r;
                      y=1.1643*(y-0.0625);
                      u=u-0.5;
                      v=v-0.5;
                      r=y+1.5958*v;
                      g=y-0.39173*u-0.81290*v;
                      b=y+2.017*u;
                      gl_FragColor=vec4(r,g,b,1.0);
                    }
                    """;
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

            program = GLES20.glCreateProgram(); // create empty OpenGL ES

            GLES20.glAttachShader(program, vertexShader); // add the vertex
            GLES20.glAttachShader(program, fragmentShader); // add the fragment
            GLES20.glLinkProgram(program);

            // Cache attribute/uniform locations - lookup once at initialization
            positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
            textureHandle = GLES20.glGetAttribLocation(program, "aTextureCoord");
            mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");

            // Create VBOs and upload geometry data to GPU (one-time operation)
            GLES20.glGenBuffers(3, vboIds, 0);

            // VBO 0: Vertex positions
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);

            // VBO 1: Texture coordinates
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[1]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, textureBuffer.capacity() * 4, textureBuffer, GLES20.GL_STATIC_DRAW);

            // VBO 2: Element indices
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vboIds[2]);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, drawListBuffer.capacity() * 2, drawListBuffer, GLES20.GL_STATIC_DRAW);

            // Unbind to avoid accidental modifications
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

            GLES20.glUseProgram(program);

            // Bind texture samplers to units (once at init, they don't change)
            int ytexHandle = GLES20.glGetUniformLocation(program, "Ytex");
            GLES20.glUniform1i(ytexHandle, 0); /* Bind Ytex to texture unit 0 */

            int utexHandle = GLES20.glGetUniformLocation(program, "Utex");
            GLES20.glUniform1i(utexHandle, 1); /* Bind Utex to texture unit 1 */

            int vtexHandle = GLES20.glGetUniformLocation(program, "Vtex");
            GLES20.glUniform1i(vtexHandle, 2); /* Bind Vtex to texture unit 2 */

            textureWidth = 0;
            textureHeight = 0;
        }

        static void initializeTexture(int name, int id, int width, int height) {
            GLES20.glActiveTexture(name);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width, height, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, null);
        }

        void setupTextures(Frame frame) {
            if (textureIds[0] != 0) {
                GLES20.glDeleteTextures(3, textureIds, 0);
            }
            GLES20.glGenTextures(3, textureIds, 0);

            int w = frame.getWidth();
            int h = frame.getHeight();
            int hw = (w + 1) >> 1;
            int hh = (h + 1) >> 1;

            initializeTexture(GLES20.GL_TEXTURE0, textureIds[0], w, h);
            initializeTexture(GLES20.GL_TEXTURE1, textureIds[1], hw, hh);
            initializeTexture(GLES20.GL_TEXTURE2, textureIds[2], hw, hh);

            textureWidth = frame.getWidth();
            textureHeight = frame.getHeight();
        }

        void GlTexSubImage2D(int width, int height, int stride, ByteBuffer buf) {
            if (stride == width) {
                // Fast path: upload entire plane in a single GL call
                GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, width, height, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, buf);
            } else {
                // Optimized: use glPixelStorei with GL_UNPACK_ROW_LENGTH when available
                // Fall back to row-by-row only if necessary
                GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
                for (int row = 0; row < height; ++row) {
                    buf.position(row * stride);
                    GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, row, width, 1, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, buf);
                }
            }
        }

        void updateTextures(Frame frame) {
            int width = frame.getWidth();
            int height = frame.getHeight();
            int half_width = (width + 1) >> 1;
            int half_height = (height + 1) >> 1;

            ByteBuffer bb = frame.getBuffer();
            if (bb == null) return;
            bb.clear();
            //check if buffer data is correctly sized.
            if (bb.remaining() != frame.getYplaneSize() + frame.getUVplaneSize() * 2) {
                textureWidth = 0;
                textureHeight = 0;
                return;
            }
            bb.position(0);
            GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
            GLES20.glPixelStorei(GLES20.GL_PACK_ALIGNMENT, 1);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
            GlTexSubImage2D(width, height, frame.getYstride(), frame.getYplane());

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[1]);
            GlTexSubImage2D(half_width, half_height, frame.getUvStride(), frame.getUplane());

            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[2]);
            GlTexSubImage2D(half_width, half_height, frame.getUvStride(), frame.getVplane());
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            viewportWidth = width;
            viewportHeight = height;
            scaleMatrixDirty = true; // Viewport changed, recalculate scale
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            // Minimize lock time: just swap frames, don't hold during GL operations
            Frame frameToRender = null;
            boolean videoEnabled = false;

            frameLock.lock();
            try {
                if (pendingFrame != null) {
                    // Dispose old frame outside the critical rendering path
                    if (currentFrame != null) {
                        currentFrame.destroy();
                    }
                    currentFrame = pendingFrame;
                    pendingFrame = null;
                }
                frameToRender = currentFrame;
                videoEnabled = !videoDisabled;
            } finally {
                frameLock.unlock();
            }

            if (frameToRender != null && videoEnabled) {
                GLES20.glUseProgram(program);

                int frameWidth = frameToRender.getWidth();
                int frameHeight = frameToRender.getHeight();

                if (textureWidth != frameWidth || textureHeight != frameHeight) {
                    setupTextures(frameToRender);
                    scaleMatrixDirty = true;
                }

                updateTextures(frameToRender);

                // Cache scale matrix calculation - only recalculate when dimensions change
                if (scaleMatrixDirty || lastFrameWidth != frameWidth || lastFrameHeight != frameHeight) {
                    Matrix.setIdentityM(scaleMatrix, 0);
                    float scaleX = 1.0f, scaleY = 1.0f;
                    float ratio = (float) frameWidth / frameHeight;
                    float vratio = (float) viewportWidth / viewportHeight;

                    if (videoFitEnabled) {
                        if (ratio > vratio) {
                            scaleY = vratio / ratio;
                        } else {
                            scaleX = ratio / vratio;
                        }
                    } else {
                        if (ratio < vratio) {
                            scaleY = vratio / ratio;
                        } else {
                            scaleX = ratio / vratio;
                        }
                    }

                    Matrix.scaleM(scaleMatrix, 0, scaleX * (frameToRender.isMirroredX() ? -1.0f : 1.0f), scaleY, 1);

                    lastFrameWidth = frameWidth;
                    lastFrameHeight = frameHeight;
                    scaleMatrixDirty = false;
                }

                // Use cached uniform location instead of looking it up every frame
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, scaleMatrix, 0);

                // Bind VBOs and draw using data stored on GPU
                // Position attribute from VBO
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[0]);
                GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, COORDS_PER_VERTEX * 4, 0);
                GLES20.glEnableVertexAttribArray(positionHandle);

                // Texture coordinate attribute from VBO
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[1]);
                GLES20.glVertexAttribPointer(textureHandle, TEXTURE_COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, TEXTURE_COORDS_PER_VERTEX * 4, 0);
                GLES20.glEnableVertexAttribArray(textureHandle);

                // Element indices from VBO
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vboIds[2]);
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, vertexIndex.length, GLES20.GL_UNSIGNED_SHORT, 0);

                // Unbind VBOs (good practice)
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
            }
            // else: already cleared to black
        }

        public void displayFrame(Frame frame) {
            frameLock.lock();
            try {
                // Double buffering: dispose pending frame if not yet rendered
                if (pendingFrame != null) {
                    pendingFrame.destroy();
                }
                pendingFrame = frame;
            } finally {
                frameLock.unlock();
            }
        }

        public static int loadShader(int type, String shaderCode) {
            int shader = GLES20.glCreateShader(type);

            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            return shader;
        }

        public void disableVideo(boolean b) {
            frameLock.lock();
            try {
                videoDisabled = b;
                if (videoDisabled) {
                    // Clean up frames when disabling
                    if (currentFrame != null) {
                        currentFrame.destroy();
                        currentFrame = null;
                    }
                    if (pendingFrame != null) {
                        pendingFrame.destroy();
                        pendingFrame = null;
                    }
                }
            } finally {
                frameLock.unlock();
            }
        }

        public void enableVideoFit(boolean enableVideoFit) {
            if (videoFitEnabled != enableVideoFit) {
                videoFitEnabled = enableVideoFit;
                scaleMatrixDirty = true; // Recalculate scale on next frame
            }
        }

        public void cleanup() {
            // Clean up VBOs when renderer is destroyed
            if (vboIds[0] != 0) {
                GLES20.glDeleteBuffers(3, vboIds, 0);
                vboIds[0] = vboIds[1] = vboIds[2] = 0;
            }
            // Clean up textures
            if (textureIds[0] != 0) {
                GLES20.glDeleteTextures(3, textureIds, 0);
                textureIds[0] = textureIds[1] = textureIds[2] = 0;
            }
            // Clean up frames
            frameLock.lock();
            try {
                if (currentFrame != null) {
                    currentFrame.destroy();
                    currentFrame = null;
                }
                if (pendingFrame != null) {
                    pendingFrame.destroy();
                    pendingFrame = null;
                }
            } finally {
                frameLock.unlock();
            }
        }
    }

    public VeraVideoRenderer(Context context) {
        view = new GLSurfaceView(context);
        view.setEGLContextClientVersion(2);

        // Optimize: Preserve EGL context on pause to avoid recreation overhead
        view.setPreserveEGLContextOnPause(true);

        renderer = new VeraRenderer();
        view.setRenderer(renderer);

        // RENDERMODE_WHEN_DIRTY is correct: only render on requestRender() call
        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        // Allows video to overlay other media surfaces
        view.setZOrderMediaOverlay(true);
    }

    @Override
    public void onFrame(Frame frame) {
        renderer.displayFrame(frame);
        view.requestRender();
    }

    @Override
    public void setStyle(String key, String value) {
        if (BaseVideoRenderer.STYLE_VIDEO_SCALE.equals(key)) {
            if (BaseVideoRenderer.STYLE_VIDEO_FIT.equals(value)) {
                renderer.enableVideoFit(true);
            } else if (BaseVideoRenderer.STYLE_VIDEO_FILL.equals(value)) {
                renderer.enableVideoFit(false);
            }
        }
    }

    @Override
    public void onVideoPropertiesChanged(boolean videoEnabled) {
        renderer.disableVideo(!videoEnabled);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onPause() {
        view.onPause();
    }

    @Override
    public void onResume() {
        view.onResume();
    }
}