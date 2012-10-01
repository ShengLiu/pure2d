/**
 * 
 */
package com.funzio.pure2D.gl.gl10;

import javax.microedition.khronos.opengles.GL10;

import com.funzio.pure2D.Camera;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

/**
 * @author long
 * @category This class is used to manage and also limit the number of JNI calls, for optimization purpose.
 */
public class GLState {
    public GL10 mGL;

    // texture
    private Texture mTexture = null;
    private boolean mTextureEnabled = false;
    private TextureCoordBuffer mTextureCoordBuffer;
    private boolean mTextureCoordArrayEnabled = false;

    // array toggles
    private boolean mVertexArrayEnabled = false;

    // colors
    private boolean mColorArrayEnabled = false;
    private GLColor mColor = new GLColor(1f, 1f, 1f, 1f);
    private BlendFunc mBlendFunc = new BlendFunc();

    // viewport and camera
    private int[] mViewport = new int[4];
    private int mMaxTextureSize = 0;
    private float mLineWidth = 0;

    private int mAxisSystem = Scene.AXIS_BOTTOM_LEFT;
    public Camera mCamera;

    public GLState(final GL10 gl) {
        reset(gl);
    }

    public void reset(final GL10 gl) {
        mGL = gl;

        mTexture = null;
        mTextureEnabled = false;
        mTextureCoordBuffer = null;
        mTextureCoordArrayEnabled = false;

        mVertexArrayEnabled = false;

        mColorArrayEnabled = false;
        mColor = new GLColor(1f, 1f, 1f, 1f);

        // find the max texture size
        int[] textureSize = new int[1];
        gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, textureSize, 0);
        mMaxTextureSize = textureSize[0];
    }

    public int getAxisSystem() {
        return mAxisSystem;
    }

    public void setAxisSystem(final int axisSystem) {
        mAxisSystem = axisSystem;
    }

    public int getMaxTextureSize() {
        return mMaxTextureSize;
    }

    public void setViewport(final int x, final int y, final int width, final int height) {
        mGL.glViewport(x, y, width, height);
        mViewport[0] = x;
        mViewport[1] = y;
        mViewport[2] = width;
        mViewport[3] = height;
    }

    public int[] getViewport() {
        int[] clone = mViewport.clone();
        clone[0] = mViewport[0];
        clone[1] = mViewport[1];
        clone[2] = mViewport[2];
        clone[3] = mViewport[3];
        return clone;
    }

    public void setLineWidth(final float width) {
        // diff check
        if (mLineWidth == width) {
            return;
        }

        mLineWidth = width;
        mGL.glLineWidth(mLineWidth);
    }

    public float getLineWidth() {
        return mLineWidth;
    }

    public boolean bindTexture(final Texture texture) {
        // diff check
        if (mTexture == texture) {
            return false;
        }

        // make sure it's enabled
        setTextureEnabled(true);

        // bind to gl
        mTexture = texture;
        mGL.glBindTexture(GL10.GL_TEXTURE_2D, mTexture.mTextureID);
        return true;
    }

    public boolean unbindTexture() {
        // diff check
        if (mTexture == null) {
            return false;
        }

        // unbind to gl
        mTexture = null;
        // mGL.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);

        // make sure it's disabled
        setTextureEnabled(false);
        return true;
    }

    /**
     * @return the vertexArrayEnabled
     */
    public boolean isVertexArrayEnabled() {
        return mVertexArrayEnabled;
    }

    /**
     * @param vertexArrayEnabled the vertexArrayEnabled to set
     */
    public boolean setVertexArrayEnabled(final boolean vertexArrayEnabled) {
        // diff check
        if (mVertexArrayEnabled == vertexArrayEnabled) {
            return false;
        }

        // apply
        mVertexArrayEnabled = vertexArrayEnabled;
        if (mVertexArrayEnabled) {
            mGL.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        } else {
            mGL.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }

        return true;
    }

    /**
     * @return the textureEnabled
     */
    public boolean isTextureEnabled() {
        return mTextureEnabled;
    }

    /**
     * @param textureEnabled the textureEnabled to set
     */
    public boolean setTextureEnabled(final boolean textureEnabled) {
        // diff check
        if (mTextureEnabled == textureEnabled) {
            return false;
        }

        mTextureEnabled = textureEnabled;

        if (mTextureEnabled) {
            // Enable Texture
            mGL.glEnable(GL10.GL_TEXTURE_2D);
        } else {
            mGL.glDisable(GL10.GL_TEXTURE_2D);
        }

        return true;
    }

    public boolean setTextureCoordBuffer(final TextureCoordBuffer buffer) {
        // diff check
        if (mTextureCoordBuffer == buffer) {
            return false;
        }

        // values check
        if (buffer != null) { // && !TextureCoordBuffer.compare(mTextureCoordBuffer, buffer)
            // apply
            mGL.glTexCoordPointer(2, GL10.GL_FLOAT, 0, buffer.mBuffer);
        }

        // now keep
        mTextureCoordBuffer = buffer;
        return true;
    }

    public TextureCoordBuffer getTextureCoordBuffer() {
        return mTextureCoordBuffer;
    }

    /**
     * @return the textureCoordArrayEnabled
     */
    public boolean isTextureCoordArrayEnabled() {
        return mTextureCoordArrayEnabled;
    }

    /**
     * @param textureCoordArrayEnabled the textureCoordArrayEnabled to set
     */
    public boolean setTextureCoordArrayEnabled(final boolean textureCoordArrayEnabled) {
        // diff check
        if (mTextureCoordArrayEnabled == textureCoordArrayEnabled) {
            return false;
        }

        // apply
        mTextureCoordArrayEnabled = textureCoordArrayEnabled;
        if (mTextureCoordArrayEnabled) {
            mGL.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        } else {
            mGL.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }

        return true;
    }

    /**
     * @return the colorArrayEnabled
     */
    public boolean isColorArrayEnabled() {
        return mColorArrayEnabled;
    }

    /**
     * @param colorArrayEnabled the colorArrayEnabled to set
     */
    public boolean setColorArrayEnabled(final boolean colorArrayEnabled) {
        // diff check
        if (mColorArrayEnabled == colorArrayEnabled) {
            return false;
        }

        // apply
        mColorArrayEnabled = colorArrayEnabled;
        if (mColorArrayEnabled) {
            mGL.glEnableClientState(GL10.GL_COLOR_ARRAY);
        } else {
            mGL.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }

        return true;
    }

    /**
     * @return the color
     */
    public GLColor getColor() {
        return mColor;
    }

    /**
     * @param color the color to set
     */
    public boolean setColor(final GLColor color) {
        // diff check
        if (mColor.equals(color)) {
            return false;
        }

        if (color == null) {
            mColor.setValues(1f, 1f, 1f, 1f);
        } else {
            mColor.setValues(color.r, color.g, color.b, color.a);
        }

        // apply
        mGL.glColor4f(mColor.r, mColor.g, mColor.b, mColor.a);

        return true;
    }

    /**
     * @param color the color to set
     */
    public boolean setColor(final float r, final float g, final float b, final float a) {
        // diff check
        if (mColor.equals(r, g, b, a)) {
            return false;
        }

        // apply
        mColor.setValues(r, g, b, a);
        mGL.glColor4f(mColor.r, mColor.g, mColor.b, mColor.a);

        return true;
    }

    /**
     * @return the blendFunc
     */
    public BlendFunc getBlendFunc() {
        return mBlendFunc;
    }

    /**
     * @param blendFunc the blendFunc to set
     */
    public boolean setBlendFunc(final BlendFunc blendFunc) {
        // null check
        if (blendFunc == null) {
            if (mBlendFunc.isSet()) {
                mBlendFunc.reset();
                mGL.glBlendFunc(mBlendFunc.src, mBlendFunc.dst);
                return true;
            } else {
                return false;
            }
            // diff check
        } else if (mBlendFunc.equals(blendFunc)) {
            return false;
        }

        // apply
        mBlendFunc.src = blendFunc.src;
        mBlendFunc.dst = blendFunc.dst;
        mGL.glBlendFunc(mBlendFunc.src, mBlendFunc.dst);

        return true;
    }

    /**
     * @return the camera
     */
    public Camera getCamera() {
        return mCamera;
    }

    /**
     * @param camera the camera to set
     */
    public void setCamera(final Camera camera) {
        mCamera = camera;
    }
}