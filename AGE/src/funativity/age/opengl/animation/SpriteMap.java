package funativity.age.opengl.animation;

import funativity.age.textures.Texture;

/**
 * Sprite class that is intended to have a single texture that has all of the
 * animated frames on it.
 * 
 * @author riedla
 * 
 */
public class SpriteMap extends Sprite
{
	/**
	 * Size of each frame. This is used to know how far to move to show only the
	 * next frame
	 */
	private final float frameWidth;
	private final float frameHeight;

	// number of frames in each row of the texture
	private final int framesPerRow;

	// last used index. Used to prevent updates when none are needed
	private int lastIndex = -1;

	/**
	 * Single frame sprite map.
	 * 
	 * @param texture
	 * @exception NullPointException
	 *                thrown if texture is null.
	 */
	public SpriteMap(Texture texture, float spriteWidth, float spriteHeight)
	{
		this(texture, spriteWidth, spriteHeight, 1, 1, 1);
	}

	/**
	 * Texture is a sprite map. meaning it has multiple sprites on the single
	 * image. Assumes frames are going left to right starting in the top left
	 * corner of the texture.
	 * 
	 * 
	 * 
	 * @param texture
	 *            texture to render this spritemap. Can NOT be null
	 * @param spriteWidth
	 *            width of this sprite in world space
	 * @param spriteHeight
	 *            height of this sprite in world space
	 * @param frameWidth
	 *            width of each frame in pixels
	 * @param frameHeight
	 *            height of each frame in pixels
	 * @param frameCount
	 *            number of frames on this texture. Important because the entire
	 *            texture may not be used
	 * @exception NullPointException
	 *                thrown if texture is null.
	 */
	public SpriteMap(Texture texture, float spriteWidth, float spriteHeight,
			int frameWidth, int frameHeight, int frameCount)
	{
		this(texture, spriteWidth, spriteHeight, 0, 0, frameWidth, frameHeight,
				frameCount);
	}

	/**
	 * Texture is a sprite map. meaning it has multiple sprites on the single
	 * image. Assumes frames are going left to right starting in the top left
	 * corner of the texture.
	 * 
	 * 
	 * 
	 * @param texture
	 *            texture to render this spritemap. Can NOT be null
	 * @param spriteWidth
	 *            width of this sprite in world space
	 * @param spriteHeight
	 *            height of this sprite in world space
	 * @param offX
	 *            offset on the x-axis
	 * @param offY
	 *            offset on the y-axis
	 * @param frameWidth
	 *            width of each frame in pixels
	 * @param frameHeight
	 *            height of each frame in pixels
	 * @param frameCount
	 *            number of frames on this texture. Important because the entire
	 *            texture may not be used
	 * @exception NullPointException
	 *                thrown if texture is null.
	 */
	public SpriteMap(Texture texture, float spriteWidth, float spriteHeight,
			float offX, float offY, int frameWidth, int frameHeight,
			int frameCount)
	{
		super(spriteWidth, spriteHeight, offX, offY);

		// This type of sprite NEEDS a texture
		if (texture == null)
		{
			throw new NullPointerException("A sprites texture cannot be null.");
		}

		// store vars
		this.frameWidth = (float) frameWidth / texture.getWidth();
		this.frameHeight = (float) frameHeight / texture.getHeight();
		this.framesPerRow = texture.getWidth() / frameWidth;
		setFrameCount(frameCount);

		super.setTexture(texture);
		update(0);
	}

	@Override
	public void update(float delta)
	{
		super.update(delta);

		// only update the data if we are showing a different frame
		if (getFrameIndex() != lastIndex)
		{
			lastIndex = getFrameIndex();

			// find where on the texture we will be drawing
			float left = frameWidth * (getFrameIndex() % framesPerRow);
			float right = left + frameWidth;
			float top = frameHeight * (int) (getFrameIndex() / framesPerRow);
			float bottom = top + frameHeight;

			// adjust the texture coords for the sprite
			this.updateTexCoordData(left, right, top, bottom);
		}
	}

}
