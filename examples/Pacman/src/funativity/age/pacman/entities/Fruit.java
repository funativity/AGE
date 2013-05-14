package funativity.age.pacman.entities;

import java.io.IOException;

import android.content.res.AssetManager;
import funativity.age.opengl.animation.SpriteMap;
import funativity.age.pacman.map.Map;
import funativity.age.state.Scene;
import funativity.age.textures.Texture;
import funativity.age.textures.TextureLoader;
import funativity.age.util.Logger;

public class Fruit extends Player
{
	private static SpriteMap fruit;

	private float duration = 0;
	private Fruits type;

	public Fruit()
	{
		setDrawable(fruit);
	}

	public void setFruit(Fruits fruitType)
	{
		this.type = fruitType;
		fruit.setFrameIndex(type.getSpriteIndex());
		fruit.update(1);
	}

	public void setDuration(float duration)
	{
		this.duration = duration;
	}

	@Override
	public void update(Scene scene, float delta)
	{
		fruit.setFrameIndex(type.getSpriteIndex());
		super.update(scene, delta);

		if ((duration -= delta) < 0)
		{
			scene.removeEntity(this);
		}
	}

	@Override
	public void collide(Map map, Player other)
	{
		if (other instanceof Pacman && isAlive())
		{
			duration = 0;
			map.eatFruit(type);
		}
	}

	@Override
	protected float getSpeedRatio(Map map)
	{
		return 0;
	}

	@Override
	public boolean isTileWalkable(int currentTile, int checkingTile)
	{
		return true;
	}

	@Override
	protected int[] getStartingDirection(Map map)
	{
		return new int[] { 0, 0 };
	}

	@Override
	protected boolean isDeadly()
	{
		return false;
	}

	@Override
	protected boolean isVulnerable()
	{
		return true;
	}

	@Override
	protected float getFrameMultiplier()
	{
		return 1;
	}

	@Override
	public boolean isAlive()
	{
		return duration > 0;
	}

	public void clearDuration()
	{
		duration = 0;
	}

	public static void setup(AssetManager assets)
	{
		try
		{
			Texture t = TextureLoader.getTexture("sprites/fruit.png", assets);
			fruit = new SpriteMap(t, 2.3f, 2.3f, 16, 16, 8);
			fruit.setFrameRate(0);
		}
		catch (IOException e)
		{
			Logger.e("Failed to load ghost eye textures", e);
		}
	}

	public enum Fruits
	{
		//@formatter:off
		CHERRY(0, 100, Score.INDEX_100), 
		STRAWBERRY(1, 300, Score.INDEX_300), 
		ORANGE(2, 500, Score.INDEX_500), 
		APPLE(3, 700, Score.INDEX_700), 
		MELON(4, 1000, Score.INDEX_1000), 
		GALAXIAN(5, 2000, Score.INDEX_2000), 
		BELL(6, 3000, Score.INDEX_3000), 
		KEY(7, 5000, Score.INDEX_5000);
		//@formatter:on

		private static final Fruits[] indexOrder = { CHERRY, STRAWBERRY,
				ORANGE, ORANGE, APPLE, APPLE, MELON, MELON, GALAXIAN, GALAXIAN,
				BELL, BELL, KEY };

		private final int spriteIndex;
		private final int points;
		private final int scoreIndex;

		Fruits(int spriteIndex, int points, int scoreIndex)
		{
			this.spriteIndex = spriteIndex;
			this.points = points;
			this.scoreIndex = scoreIndex;
		}

		public int getPoints()
		{
			return points;
		}

		public int getScoreIndex()
		{
			return scoreIndex;
		}

		public int getSpriteIndex()
		{
			return spriteIndex;
		}

		public static Fruits getLevelFruitIndex(int level)
		{
			int index = level >= indexOrder.length ? indexOrder.length - 1
					: level;
			return indexOrder[index];
		}
	}
}
