package funativity.age.pacman.entities;

import java.io.IOException;

import android.content.res.AssetManager;
import funativity.age.opengl.Entity;
import funativity.age.opengl.animation.SpriteMap;
import funativity.age.state.Scene;
import funativity.age.textures.Texture;
import funativity.age.textures.TextureLoader;
import funativity.age.util.Geometry3f;
import funativity.age.util.Logger;

public class Score extends Entity
{
	public static final int INDEX_100 = 0;
	public static final int INDEX_300 = 1;
	public static final int INDEX_500 = 2;
	public static final int INDEX_700 = 3;
	public static final int INDEX_1000 = 4;
	public static final int INDEX_2000 = 5;
	public static final int INDEX_3000 = 6;
	public static final int INDEX_5000 = 7;
	public static final int INDEX_200 = 8;
	public static final int INDEX_400 = 9;
	public static final int INDEX_800 = 10;
	public static final int INDEX_1600 = 11;

	private static Texture SCORE_TEXTURE;
	private static final float SCORE_DISPLAY_DURATION = 2;

	private float duration;

	public Score(float x, float y, int index)
	{
		final float width = 2.5f;
		final float height = width * 8f / 21f;

		SpriteMap sprite = new SpriteMap(SCORE_TEXTURE, width, height, 21, 8,
				12);
		sprite.setFrameRate(0);
		sprite.setFrameIndex(index);
		sprite.update(0);
		setDrawable(sprite);

		setPosition(new Geometry3f(x, y));

		duration = SCORE_DISPLAY_DURATION;
	}

	@Override
	public void update(Scene scene, float delta)
	{
		if ((duration -= delta) <= 0)
			scene.removeEntity(this);
	}

	public static void setup(AssetManager assets)
	{
		try
		{
			SCORE_TEXTURE = TextureLoader.getTexture("sprites/scores.png",
					assets);
		}
		catch (IOException e)
		{
			Logger.e("Failed to load scores texture", e);
		}
	}
}
