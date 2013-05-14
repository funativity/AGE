package funativity.age.pacman.entities;

import java.io.IOException;

import android.content.res.AssetManager;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.Entity;
import funativity.age.opengl.animation.SpriteMap;
import funativity.age.textures.Texture;
import funativity.age.textures.TextureLoader;
import funativity.age.util.Logger;

public class Joystick extends Entity
{
	private static Texture simple;
	private final SpriteMap stick;

	public Joystick(AGEColor color)
	{
		super();

		stick = new SpriteMap(simple, 5, 5, 64, 64, 5);
		stick.setColor(color);
		stick.setFrameRate(-1);
		setDrawable(stick);
	}

	public void setDirNone()
	{
		stick.setFrameIndex(0);
	}

	public void setDirUp()
	{
		stick.setFrameIndex(1);
	}

	public void setDirLeft()
	{
		stick.setFrameIndex(2);
	}

	public void setDirDown()
	{
		stick.setFrameIndex(3);
	}

	public void setDirRight()
	{
		stick.setFrameIndex(4);
	}

	public static void setup(AssetManager assets)
	{
		try
		{
			simple = TextureLoader.getTexture("sprites/joystick.png", assets);
		}
		catch (IOException e)
		{
			Logger.e("Failed to load ghost eye textures", e);
		}
	}

}
