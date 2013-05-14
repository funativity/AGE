package funativity.age.opengl.primitive;

import funativity.age.opengl.DrawMode;
import funativity.age.util.Geometry3f;

/**
 * OpenGL mesh representing a box.
 */
public class Box extends Primitive
{
	/**
	 * Creates a centered cube using a width, height, and depth of 1.
	 */
	public Box()
	{
		this(1, 1, 1);
	}

	/**
	 * Creates a centered cube of given size.
	 * 
	 * @param size
	 *            size on x-axis, y-axis, and z-axis
	 */
	public Box(float size)
	{
		this(size, size, size);
	}

	/**
	 * Creates a centered box of given size.
	 * 
	 * @param width
	 *            size on x-axis
	 * @param height
	 *            size on y-axis
	 * @param depth
	 *            size on z-axis
	 */
	public Box(float width, float height, float depth)
	{
		super(new Geometry3f(width, height, depth));
		build();
	}

	@Override
	public float[] initVertices()
	{
		float hw = getWidth() / 2f;
		float hh = getHeight() / 2f;
		float hd = getDepth() / 2f;

		//@formatter:off
		return new float[] {
				// front
				-hw, hh, hd, 0, 0,
				-hw, -hh, hd, 0, 1, 
				hw, hh, hd, 1, 0,
				hw, -hh, hd, 1, 1,

				// back
				-hw, hh, -hd, 0, 0, 
				hw, hh, -hd, 1, 0, 
				-hw, -hh, -hd, 0, 1, 
				hw, -hh, -hd, 1, 1,

				// left
				-hw, hh, hd, 0, 0, 
				-hw, -hh, hd, 0, 1, 
				-hw, -hh, -hd, 1, 1,
				-hw, hh, -hd, 1, 0,

				// right
				hw, hh, hd, 0, 0, 
				hw, -hh, -hd, 1, 1, 
				hw, -hh, hd, 0, 1, 
				hw, hh, -hd, 1, 0,

				// top
				hw, hh, hd, 1, 1, 
				-hw, hh, hd, 0, 1, 
				hw, hh, -hd, 1, 0, 
				-hw, hh, -hd, 0, 0,

				// bottom
				hw, -hh, hd, 1, 1, 
				-hw, -hh, hd, 0, 1, 
				-hw, -hh, -hd, 0, 0, 
				hw, -hh, -hd, 1, 0
			};
		//@formatter:on
	}

	@Override
	public short[] initDrawOrder()
	{
		return new short[] {
				// front
				0, 1, 2, 2, 1, 3,

				// back
				4, 5, 6, 6, 5, 7,

				// left
				9, 8, 10, 10, 8, 11,

				// right
				13, 12, 14, 12, 13, 15,

				// top
				17, 16, 18, 17, 18, 19,

				// bottom
				20, 21, 22, 20, 22, 23 };
	}

	@Override
	public DrawMode initDrawMode()
	{
		return DrawMode.GL_TRIANGLES;
	}
}