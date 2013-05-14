package funativity.age.opengl;

import java.util.Stack;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * Matrix Manager. This class wraps the openGL matrices together
 * 
 * 
 * @author riedla
 * 
 */
public class MM
{

	/**
	 * This enum represents the three matrices that are used in OpenGL
	 * calculations.
	 * 
	 * @author riedla
	 * 
	 */
	public static enum Matrices
	{
		MODEL, VIEW, PROJECTION;

		private float[] matrix = new float[16];

		Matrices()
		{
			loadIdentity(this);
		}

		public float[] getMatrix()
		{
			return matrix;
		}

		public void setMatrix(float[] matrix)
		{
			this.matrix = matrix;
		}
	}

	/**
	 * Matrix stack. Used to save/load matrices quickly
	 */
	private static Stack<float[]> matrixStack = new Stack<float[]>();

	/**
	 * Get the Model Matrix
	 * 
	 * @return
	 */
	public static float[] getMMatrix()
	{
		return Matrices.MODEL.getMatrix();
	}

	/**
	 * Get the View Matrix
	 * 
	 * @return
	 */
	public static float[] getVMatrix()
	{
		return Matrices.VIEW.getMatrix();
	}

	/**
	 * Get the Projection Matrix
	 * 
	 * @return
	 */
	public static float[] getPMatrix()
	{
		return Matrices.PROJECTION.getMatrix();
	}

	/**
	 * Get the ModelView Matrix
	 * 
	 * @return
	 */
	public static float[] getMVMatrix()
	{
		float[] rtn = new float[16];
		Matrix.multiplyMM(rtn, 0, getVMatrix(), 0, getMMatrix(), 0);
		return rtn;
	}

	/**
	 * Get the ModelViewProjection Matrix
	 * 
	 * @return
	 */
	public static float[] getMVPMatrix()
	{
		float[] rtn = new float[16];
		Matrix.multiplyMM(rtn, 0, getPMatrix(), 0, getMVMatrix(), 0);
		return rtn;
	}

	/**
	 * Loads the identity matrix into the model matrix
	 */
	public static void loadIdentity()
	{
		loadIdentity(Matrices.MODEL);
	}

	/**
	 * Loads the identity matrix into the provided matrix. Acceptable values
	 * are; MATRIX_MODEL, MATRIX_VIEW, MATRIX_PROJECTION (0, 1, 2)
	 * 
	 * @param matrix
	 */
	public static void loadIdentity(Matrices matrix)
	{
		Matrix.setIdentityM(matrix.getMatrix(), 0);
	}

	/**
	 * Set up the view matrix to simulate a camera at (eyeX, eyeY, eyeZ) looking
	 * at (targetX, targetY, targetZ) and simulates the "up" direction by (upX,
	 * upY, upZ). Assumes up is normalized.
	 * 
	 * @param eyeX
	 *            Position of camera
	 * @param eyeY
	 * @param eyeZ
	 * @param targetX
	 *            Point camera is looking at
	 * @param targetY
	 * @param targetZ
	 * @param upX
	 *            Up direction for camera
	 * @param upY
	 * @param upZ
	 */
	public static void lookAt(float eyeX, float eyeY, float eyeZ,
			float targetX, float targetY, float targetZ, float upX, float upY,
			float upZ)
	{
		Matrix.setLookAtM(Matrices.VIEW.getMatrix(), 0, eyeX, eyeY, eyeZ,
				targetX, targetY, targetZ, upX, upY, upZ);
	}

	/**
	 * Set up the view matrix to simulate a camera at (eyeX, eyeY, eyeZ) looking
	 * at (targetX, targetY, targetZ) and simulates the "up" direction by (upX,
	 * upY, upZ). Assumes up is normalized.
	 * 
	 * @param eye
	 *            Position of camera
	 * @param up
	 *            Up direction for camera
	 * @param target
	 *            Point camera is looking at
	 */
	public static void lookAt(float[] eye, float[] up, float[] look)
	{
		lookAt(eye[0], eye[1], eye[2], look[0], look[1], look[2], up[0], up[1],
				up[2]);
	}

	/**
	 * Setup the projection matrix using the default Android Frustum method
	 * call. This method accomplishes a similar task as the perspective method.
	 * 
	 * @param width
	 *            Width of the screen
	 * @param height
	 *            Height of the screen
	 * @param znear
	 *            Smallest distance from the screen that can be rendered (should
	 *            be > 0)
	 * @param zfar
	 *            Farthest distance from the screen that can be rendered
	 */
	public static void projection(int width, int height, float znear, float zfar)
	{
		GLES20.glViewport(0, 0, width, height);

		final float ratio = (float) width / height;

		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;

		Matrix.frustumM(Matrices.PROJECTION.getMatrix(), 0, left, right,
				bottom, top, znear, zfar);
	}

	/**
	 * Setup the projection matrix using a different form of calculations
	 * compared to the projection method. This method accomplishes a similar
	 * task as the projection method.
	 * 
	 * @param fovY
	 *            Angle in degrees the frustum changes (normal values between 40
	 *            and 70)
	 * @param aspect
	 *            Aspect ratio of the screen (width/height)
	 * @param zNear
	 *            Smallest distance from the screen that can be rendered (should
	 *            be > 0)
	 * @param zFar
	 *            Farthest distance from the screen that can be rendered
	 */
	public static void perspective(float fovY, float aspect, float zNear,
			float zFar)
	{
		// calculate scale
		float frustumScale = calcFrustumScale(fovY);

		// reset projection
		loadIdentity(Matrices.PROJECTION);

		// rebuild projection - explanation of this code can be found at
		// http://arcsynthesis.org/gltut/Positioning/Tut04%20The%20Matrix%20Has%20You.html
		Matrices.PROJECTION.getMatrix()[0] = frustumScale / aspect;
		Matrices.PROJECTION.getMatrix()[5] = frustumScale;
		Matrices.PROJECTION.getMatrix()[10] = (zFar + zNear) / (zNear - zFar);
		Matrices.PROJECTION.getMatrix()[14] = (2 * zFar * zNear)
				/ (zNear - zFar);
		Matrices.PROJECTION.getMatrix()[11] = -1;
	}

	/**
	 * 
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 */
	public static void ortho(float left, float right, float top, float bottom)
	{
		// reset projection
		loadIdentity(Matrices.PROJECTION);

		Matrices.PROJECTION.getMatrix()[0] = 2 / (right - left);
		Matrices.PROJECTION.getMatrix()[5] = 2 / (top - bottom);
		Matrices.PROJECTION.getMatrix()[10] = -1;
		Matrices.PROJECTION.getMatrix()[12] = -(right + left) / (right - left);
		Matrices.PROJECTION.getMatrix()[13] = -(top + bottom) / (top - bottom);
	}

	/**
	 * Rotate the Model matrix 'angle' degrees around the (x, y, z) axis
	 * 
	 * @param angle
	 *            Amount to rotate in degrees
	 * @param x
	 *            Axis to rotate around
	 * @param y
	 * @param z
	 */
	public static void rotate(float angle, float x, float y, float z)
	{
		Matrix.rotateM(Matrices.MODEL.getMatrix(), 0, angle, x, y, z);
	}

	/**
	 * Scale the Model matrix by amounts provided
	 * 
	 * @param x
	 *            Scale in the x-direction
	 * @param y
	 *            Scale in the y-direction
	 * @param z
	 *            Scale in the z-direction
	 */
	public static void scale(float x, float y, float z)
	{
		Matrix.scaleM(Matrices.MODEL.getMatrix(), 0, x, y, z);
	}

	/**
	 * Translate the Model matrix by amounts provided
	 * 
	 * @param x
	 *            X translation
	 * @param y
	 *            Y translation
	 * @param z
	 *            Z translation
	 */
	public static void translate(float x, float y, float z)
	{
		Matrix.translateM(Matrices.MODEL.getMatrix(), 0, x, y, z);
	}

	/**
	 * Used to calculate frustum scale when setting up the projection matrix
	 * 
	 * 
	 * @param fovDeg
	 *            field of view in degrees
	 * 
	 * @return scale
	 */
	private static float calcFrustumScale(float fovDeg)
	{
		float fovRad = (float) Math.toRadians(fovDeg);
		return 1.0f / (float) Math.tan(fovRad / 2.0f);
	}

	/**
	 * Push the current model matrix onto the matrix stack. The current model
	 * matrix will not be affected by this call.
	 */
	public static void pushMatrix()
	{
		pushMatrix(Matrices.MODEL);
	}

	/**
	 * Push specified matrix onto the matrix stack. This can be used to reload a
	 * matrix after doing some manipulations to it. Acceptable values are;
	 * MATRIX_MODEL, MATRIX_VIEW, MATRIX_PROJECTION (0, 1, 2)
	 * 
	 * @param matrix
	 */
	public static void pushMatrix(Matrices matrix)
	{
		float[] cpy = new float[16];
		System.arraycopy(matrix.getMatrix(), 0, cpy, 0, 16);

		matrixStack.push(cpy);
	}

	/**
	 * Pop the last matrix off of the matrix stack and load it into the model
	 * matrix
	 */
	public static void popMatrix()
	{
		popMatrix(Matrices.MODEL);
	}

	/**
	 * Pop the last matrix off of the matrix stack and load it into the
	 * specified matrix. Acceptable values are; MATRIX_MODEL, MATRIX_VIEW,
	 * MATRIX_PROJECTION (0, 1, 2)
	 * 
	 */
	public static void popMatrix(Matrices matrix)
	{
		matrix.setMatrix(matrixStack.pop());
	}
}