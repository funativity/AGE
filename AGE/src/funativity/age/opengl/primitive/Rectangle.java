package funativity.age.opengl.primitive;

import funativity.age.opengl.DrawMode;
import funativity.age.opengl.shaders.SimpleTechnique;
import funativity.age.util.Geometry3f;

/**
 * OpenGL mesh representing a rectangle.
 */
public class Rectangle extends Primitive
{
	private final float offX, offY;

	/**
	 * Creates a centered, xy-plane rectangle using a width and height of 1.
	 */
	public Rectangle()
	{
		this(1, 1);
	}

	/**
	 * Creates a centered, xy-plane rectangle of given width and height.
	 * 
	 * @param width
	 *            size on x-axis
	 * @param height
	 *            size on y-axis
	 */
	public Rectangle(float width, float height)
	{
		this(width, height, 0, 0);
	}

	/**
	 * Creates a xy-plane rectangle of given width and height offset by offX and
	 * offY
	 * 
	 * @param width
	 *            size on x-axis
	 * @param height
	 *            size on y-axis
	 * @param offX
	 *            offset on the x-axis
	 * @param offY
	 *            offset on the y-axis
	 */
	public Rectangle(float width, float height, float offX, float offY)
	{
		super(new Geometry3f(width, height));

		this.offX = offX;
		this.offY = offY;

		build();
	}

	/**
	 * Adjust the texture coords in the rectangle to be in a specific spot on
	 * the texture
	 * 
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 */
	public void updateTexCoordData(float left, float right, float top,
			float bottom)
	{
		float[] texData = new float[] { left, bottom, left, top, right, bottom,
				right, top };

		super.bufferSubData(texData, SimpleTechnique.ELEMENT_COUNT_TEXTURE,
				SimpleTechnique.BYTE_COUNT_TEXTURE,
				SimpleTechnique.BYTE_OFFSET_TEXTURE, SimpleTechnique.STRIDE);
	}

	@Override
	public float[] initVertices()
	{
		// half height and half width
		float hw = getWidth() / 2f;
		float hh = getHeight() / 2f;

		return new float[] {
				// bottom left
				-hw + offX, -hh + offY, 0, 0, 1,

				// top left
				-hw + offX, hh + offY, 0, 0, 0,

				// bottom right
				hw + offX, -hh + offY, 0, 1, 1,

				// top right
				hw + offX, hh + offY, 0, 1, 0 };
	}

	@Override
	public short[] initDrawOrder()
	{
		return new short[] { 0, 1, 2, 2, 1, 3 };
	}

	@Override
	public DrawMode initDrawMode()
	{
		return DrawMode.GL_TRIANGLES;
	}
}