package funativity.age.opengl.animation;

import funativity.age.opengl.AGEColor;
import funativity.age.opengl.primitive.Rectangle;
import funativity.age.textures.Texture;

/**
 * Represents a Sprite. Intended for 2D game development. This class holds
 * general sprite specific information
 * 
 * 
 * @author riedla
 * 
 */
public abstract class Sprite extends AnimatedMesh
{
	// Actual rectangle that this sprite will render its image onto.
	private Rectangle sprite;

	/**
	 * Abstract sprite that holds information about a generic sprite (such as
	 * what frame is currently being shown)
	 * 
	 * @param width
	 *            width of sprite
	 * @param height
	 *            height of sprite
	 */
	public Sprite(float width, float height)
	{
		this(width, height, 0, 0);
	}

	/**
	 * Abstract sprite that holds information about a generic sprite (such as
	 * what frame is currently being shown)
	 * 
	 * @param width
	 *            width of sprite
	 * @param height
	 *            height of sprite
	 * @param offX
	 *            offset on the x-axis
	 * @param offY
	 *            offset on the y-axis
	 */
	public Sprite(float width, float height, float offX, float offY)
	{
		sprite = new Rectangle(width, height, offX, offY);
	}

	@Override
	public void draw()
	{
		sprite.draw();
	}

	/**
	 * Update the texture coordinates of the underlying rectangle of this sprite
	 * 
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 */
	public void updateTexCoordData(float left, float right, float top,
			float bottom)
	{
		sprite.updateTexCoordData(left, right, top, bottom);
	}

	/**
	 * Set the base texture of this sprite.
	 * 
	 * @param texture
	 */
	public void setTexture(Texture texture)
	{
		sprite.setTexture(texture);
	}

	@Override
	public AGEColor getColor()
	{
		return sprite.getColor();
	}

	@Override
	public void setColor(AGEColor color)
	{
		sprite.setColor(color);
	}

}
