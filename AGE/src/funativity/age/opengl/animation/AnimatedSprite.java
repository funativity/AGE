package funativity.age.opengl.animation;

import java.util.ArrayList;

import funativity.age.textures.Texture;

/**
 * Sprite class that is meant to handle multiple frames for a sprite. Intended
 * for using multiple different textures to make up the sprite.
 * 
 * @author riedla
 * 
 */
public class AnimatedSprite extends Sprite
{
	// list of textures that represent this sprite. The order they will be
	// displayed is the order they are in this list.
	private ArrayList<Texture> textures = new ArrayList<Texture>();

	/**
	 * Creation of the AnimatedSprite. Undefined results if width or height are
	 * <= 0.
	 * 
	 * @param spriteWidth
	 *            width of this sprite
	 * @param spriteHeight
	 *            height of this sprite
	 */
	public AnimatedSprite(float spriteWidth, float spriteHeight)
	{
		super(spriteWidth, spriteHeight);
	}

	/**
	 * Adds new frame at end of list. Add null if you want a blank frame.
	 * 
	 * @param texture
	 *            Texture with which to crean a frame with
	 */
	public void AddFrame(Texture texture)
	{
		textures.add(texture);
		setFrameCount(textures.size());
	}

	@Override
	public void update(float delta)
	{
		super.update(delta);

		if (getFrameCount() > 0)
			this.setTexture(textures.get(getFrameIndex()));
	}

}
