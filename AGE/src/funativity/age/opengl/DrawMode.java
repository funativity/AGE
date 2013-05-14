package funativity.age.opengl;

import android.opengl.GLES20;

/**
 * OpenGL constants referencing the draw modes.
 *
 */
public enum DrawMode
{
	GL_TRIANGLES(GLES20.GL_TRIANGLES), GL_TRIANGLE_FAN(GLES20.GL_TRIANGLE_FAN), GL_TRIANGLE_STRIP(
			GLES20.GL_TRIANGLE_STRIP), GL_POINTS(GLES20.GL_POINTS), GL_LINES(
			GLES20.GL_LINES), GL_LINE_STRIP(GLES20.GL_LINE_STRIP);
	private int mode;

	private DrawMode(int mode)
	{
		setMode(mode);
	}

	/**
	 * Gets the current DrawMode
	 * @return returns the current DrawMode
	 */
	public int getMode()
	{
		return mode;
	}

	/**
	 * Sets the current DrawMode
	 */
	public void setMode(int mode)
	{
		this.mode = mode;
	}
}