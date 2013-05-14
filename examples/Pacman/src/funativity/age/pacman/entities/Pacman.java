package funativity.age.pacman.entities;

import java.io.IOException;

import android.content.res.AssetManager;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.animation.SpriteMap;
import funativity.age.pacman.map.Map;
import funativity.age.textures.Texture;
import funativity.age.textures.TextureLoader;
import funativity.age.util.Geometry3f;
import funativity.age.util.Logger;

import static funativity.age.pacman.map.MapLoader.*;

public class Pacman extends Player
{
	private static Texture pacmanTexture;
	private static Texture deadTexture;

	private final SpriteMap pacman;
	private final SpriteMap dead;

	private boolean alive = true;

	// freeze after eating
	private static final float EAT_DELAY = 0.015f;
	private float eatDelay = 0;

	public Pacman(AGEColor color)
	{
		final float width = 2, height = 2;

		setFreeReversal(true);
		setRoundingBuffer(ROUNDING_MAXBUFFER);

		pacman = new SpriteMap(pacmanTexture, width, height, 16, 16, 5);
		pacman.setFrameRate(12);
		pacman.setColor(color);
		this.setDrawable(pacman);

		dead = new SpriteMap(deadTexture, width, height, 16, 16, 16);
		dead.setFrameRate((dead.getFrameCount() - 1) / Map.DELAY_DEAD);
		dead.setColor(color);
	}

	@Override
	public boolean isAlive()
	{
		return alive;
	}

	@Override
	public void reset(Map map, int spawnX, int spawnY)
	{
		super.reset(map, spawnX, spawnY);
		alive = true;

		dead.reset();
		pacman.reset();
		setDrawable(pacman);
		this.setRZ(0);
	}

	@Override
	public boolean isTileWalkable(int currentTile, int checkingTile)
	{
		// if we are sitting on a wall, something must have gone wrong...so just
		// let us off
		if ((currentTile & B) != 0)
			return true;

		// if the checking tile is a wall, it is not walkable
		if ((checkingTile & B) != 0 || (checkingTile & GG) != 0)
			return false;

		return true;
	}

	@Override
	protected float getSpeedRatio(Map map)
	{
		if (isFrightened())
			return 1.2f;

		return 1;
	}

	@Override
	protected void inCenter(Map map)
	{
		if (map.tryToEatAt(this))
		{
			eatDelay = EAT_DELAY;
		}
	}

	@Override
	public void update(Map map, float delta)
	{
		if (isAlive())
		{
			// make pacman move slow after eating. Rulz of teh game
			if (eatDelay > 0)
			{
				if (delta >= eatDelay)
				{
					delta -= eatDelay;
					eatDelay = 0;
				}
				else
				{
					eatDelay -= delta;
					delta = 0;
				}
			}

			super.update(map, delta);

			if (getFacingX() < 0)
				this.setRZ(180);
			else if (getFacingX() > 0)
				this.setRZ(0);
			else if (getFacingY() > 0)
				this.setRZ(90);
			else if (getFacingY() < 0)
				this.setRZ(270);
		}
		else
		{
			getDrawable().update(delta);

			if (getFacingX() < 0)
				this.setRZ(90);
			else if (getFacingX() > 0)
				this.setRZ(270);
			else if (getFacingY() > 0)
				this.setRZ(0);
			else if (getFacingY() < 0)
				this.setRZ(180);
		}
	}

	@Override
	public void collide(Map map, Player other)
	{
		if (other instanceof Ghost)
		{
			if (((Ghost) other).isDeadly() && isVulnerable())
			{
				alive = false;
				setVelocity(new Geometry3f());
				setDrawable(dead);
				map.pacmanKilled();
			}
		}
	}

	@Override
	protected boolean isDeadly()
	{
		return isFrightened() && isAlive();
	}

	@Override
	protected boolean isVulnerable()
	{
		return isAlive();
	}

	public static void setup(AssetManager assets)
	{
		try
		{
			pacmanTexture = TextureLoader.getTexture("sprites/pacman.png",
					assets);
			deadTexture = TextureLoader.getTexture("sprites/dead.png", assets);
		}
		catch (IOException e)
		{
			Logger.e("Failed to load ghost eye textures", e);
		}
	}

	@Override
	protected int[] getStartingDirection(Map map)
	{
		int dx = map.getLevelNumber() % 3 == 2 ? 1 : -1;
		return new int[] { dx, 0 };
	}

	@Override
	protected float getFrameMultiplier()
	{
		return 20;
	}

}
