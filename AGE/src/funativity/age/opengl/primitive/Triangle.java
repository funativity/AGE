package funativity.age.opengl.primitive;

import funativity.age.opengl.DrawMode;
import funativity.age.util.Geometry3f;

/**
 * OpenGL mesh representing a triangle.
 */
public class Triangle extends Primitive
{
	/**
	 * Creates a centered, xy-plane triangle using a width and height of 1.
	 */
	public Triangle()
	{
		this(1, 1);
	}

	/**
	 * Creates a centered, xy-plane triangle of given width and height.
	 * 
	 * @param width
	 *            size on x-axis
	 * @param height
	 *            size on y-axis
	 */
	public Triangle(float width, float height)
	{
		super(new Geometry3f(width, height));
		build();
	}

	@Override
	public float[] initVertices()
	{
		// half width and height
		float hw = getWidth() / 2f;
		float hh = getHeight() / 2f;

		return new float[] {
				// bottom left
				-hw, -hh, 0, 0, 1,

				// bottom right
				hw, -hh, 0, 1, 1,

				// top
				0, hh, 0, 0.5f, 0 };
	}

	@Override
	public short[] initDrawOrder()
	{
		return new short[] { 0, 1, 2 };
	}

	@Override
	public DrawMode initDrawMode()
	{
		return DrawMode.GL_TRIANGLES;
	}
}