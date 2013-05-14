package funativity.age.pacman.entities;

import java.io.IOException;
import java.util.Random;

import android.content.res.AssetManager;
import funativity.age.opengl.MM;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.animation.Sprite;
import funativity.age.opengl.animation.SpriteMap;
import funativity.age.pacman.map.Map;
import funativity.age.textures.Texture;
import funativity.age.textures.TextureLoader;
import funativity.age.util.Logger;

import static funativity.age.pacman.map.MapLoader.*;

public class Ghost extends Player
{
	private static final Random r = new Random();
	private static final AGEColor BLUE = new AGEColor(0, 0, 1);
	private static final AGEColor WHITE = new AGEColor();
	private static final AGEColor RED = new AGEColor(1, 0, 0);
	private static final AGEColor ORANGE = Ghosts.CLYDE.color;

	// ghost eyes
	private static Sprite left;
	private static Sprite right;
	private static Sprite up;
	private static Sprite down;
	private static Sprite frightened;

	private static Texture ghostTexture;

	// force turn around (mainly for ghosts)
	private boolean reverse = false;

	// what ghost this is
	private final Ghosts type;
	private boolean renderGhost = true;

	// target tile for this ghost
	private int targetX, targetY;

	// pen variables
	private boolean penned = true;
	private final int pacGlobalLimit;
	private int pacPersonalLimit;
	private int pacPersonalCount;

	// ELROOOOOOOOOOOOOOOOOY!!!
	private static boolean suspendElroy = false;

	public Ghost(Ghosts type)
	{
		this.type = type;
		setRoundingBuffer(ROUNDING_MINBUFFER);
		setFreeReversal(false);

		SpriteMap m = new SpriteMap(ghostTexture, 2, 2, 16, 16, 2);
		m.setFrameRate(12);
		type.setColor(m);
		this.setDrawable(m);

		switch (type)
		{
			default:
			case BLINKY:
				pacGlobalLimit = 0;
				break;
			case PINKY:
				pacGlobalLimit = 7;
				break;
			case INKY:
				pacGlobalLimit = 17;
				break;
			case CLYDE:
				pacGlobalLimit = 32;
				break;
		}
	}

	public void setTargetTile(int x, int y)
	{
		targetX = x;
		targetY = y;
	}

	@Override
	protected float getSpeedRatio(Map map)
	{
		int tile = map.getTileAt(this);

		if ((tile & GP) != 0)
			return 0.35f;
		if (!isAlive())
			return 1.4f;

		return type.getSpeedRatio(map.getPacsRemaining(), map.getLevelNumber(),
				(tile & T) != 0, isFrightened());
	}

	@Override
	protected float getFrameMultiplier()
	{
		return 20;
	}

	@Override
	protected void newTile(Map map)
	{
		Player player = map.getPacman();
		int tile = map.getTileAt(this);
		int tiley = Map.getTileY(this);
		int tilex = Map.getTileX(this);

		// supposed to be in pen
		if (!isAlive())
		{
			if ((tile & GP) == 0)
			{
				if (tilex == map.getGhostDoorX()
						&& tiley > map.getGhostDoorY() - 3
						&& tiley <= map.getGhostDoorY())
				{
					setTargetTile(map.getGhostDoorX(), map.getGhostDoorY() - 3);
				}
				else if (tiley > map.getGhostDoorY() - 3)
				{
					setTargetTile(map.getGhostDoorX(), map.getGhostDoorY());
				}
				else
				{
					setTargetTile(map.getGhostDoorX(), map.getGhostDoorY() + 3);
				}
			}
			else
			{
				int[] spawn = type.getSpawn(map);
				setTargetTile(spawn[0], spawn[1]);
			}
		}
		// frightened movement
		else if (isFrightened())
		{
			if (r.nextBoolean())
			{
				int dx = r.nextInt(1) * 2 - 1;
				setTargetTile(tilex + dx, tiley);
			}
			else
			{
				int dy = r.nextInt(1) * 2 - 1;
				setTargetTile(tilex, tiley + dy);
			}
		}
		// chase mode
		else if (map.getMode() == Map.Mode.CHASE)
		{
			int dx, dy, bx, by, midx, midy;

			// chase
			switch (type)
			{
				case BLINKY:
					this.setTargetTile(Map.getTileX(player),
							Map.getTileY(player));
					break;
				case PINKY:
					this.setTargetTile(
							Map.getTileX(player) + 4 * player.getFacingX(),
							Map.getTileY(player) + 4 * player.getFacingY());
					break;
				case INKY:
					// get where pacman is looking
					midx = Map.getTileX(player) + 2 * player.getFacingX();
					midy = Map.getTileY(player) + 2 * player.getFacingY();
					// get blinky's location
					bx = Map.getTileX(map.getBlinky());
					by = Map.getTileY(map.getBlinky());
					// get change
					dx = midx - bx;
					dy = midy - by;
					// set target
					this.setTargetTile(midx + dx, midy + dy);
					break;
				case CLYDE:
					// get distance from pacman
					dx = Map.getTileX(this) - Map.getTileX(player);
					dy = Map.getTileY(this) - Map.getTileY(player);
					double dist = Math.sqrt(dx * dx + dy * dy);
					if (dist < 8)
						map.setScatterTile(this);
					else
						this.setTargetTile(Map.getTileX(player),
								Map.getTileY(player));
					break;
			}
		}
		// scatter (and blinky cruise elroy chase pacman during scatter mode)
		else
		{
			if (type.getCruiseElroyLevel(map.getPacsRemaining(),
					map.getLevelNumber()) > 0)
				this.setTargetTile(Map.getTileX(player), Map.getTileY(player));
			else
				map.setScatterTile(this);
		}

		// update direction to move
		Options[] options = new Options[4];
		byte count = 0;

		// if up is not restricted and is an open tile, and Ghost is not
		// currently moving down, add up to options
		//@formatter:off
		if ((tile & RU) == 0 && 
				isTileWalkable( map.getTileAt(this), map.getTileAt(tilex + Options.U.getDX(), tiley + Options.U.getDY())) && 
				getFacingY() != -1)
		{
					//@formatter:on
			options[0] = Options.U;
			count++;
		}

		// if left is not restricted and is an open tile, and Ghost is not
		// currently moving right, add left to options
		//@formatter:off
		if ((tile & RL) == 0 && 
				isTileWalkable( map.getTileAt(this), map.getTileAt(tilex + Options.L.getDX(), tiley + Options.L.getDY())) && 
				getFacingX() != 1)
		{
					//@formatter:on
			options[1] = Options.L;
			count++;
		}

		// if down is not restricted and is an open tile, and Ghost is not
		// currently moving up, add down to options
		//@formatter:off
		if ((tile & RD) == 0 && 
				isTileWalkable( map.getTileAt(this), map.getTileAt(tilex + Options.D.getDX(), tiley + Options.D.getDY())) && 
				getFacingY() != 1)
		{
					//@formatter:on
			options[2] = Options.D;
			count++;
		}

		// if right is not restricted and is an open tile, and Ghost is not
		// currently moving left, add right to options
		//@formatter:off
		if ((tile & RR) == 0 && 
				isTileWalkable( map.getTileAt(this), map.getTileAt(tilex + Options.R.getDX(), tiley + Options.R.getDY())) && 
				getFacingX() != -1)
		{
					//@formatter:on
			options[3] = Options.R;
			count++;
		}

		// if we have no options...there is a problem
		if (count == 0)
		{
			// Logger.i(type.name() + " has no moving options at [" + tilex +
			// ", " + tiley + "]");
			return;
		}

		// if we only have 1 option, no need to decide if it is the best
		if (count == 1)
		{
			for (int i = 0; i < options.length; i++)
			{
				if (options[i] != null)
					options[i].requestDirection(this);
			}
		}
		else
		{
			// now we know our options, lets choose best one
			int bestIndex = -1;
			int bestValue = -1;
			for (int i = 0; i < options.length; i++)
			{
				if (options[i] != null)
				{
					int dx = (tilex + options[i].dx) - targetX;
					int dy = (tiley + options[i].dy) - targetY;
					int value = dx * dx + dy * dy;
					if (value < bestValue || bestValue == -1)
					{
						bestValue = value;
						bestIndex = i;
					}
				}
			}

			if (bestIndex >= 0)
				options[bestIndex].requestDirection(this);
		}
	}

	public void addToPacCounter()
	{
		pacPersonalCount++;
	}

	public void setPersonalPacCountLimit(int limit)
	{
		pacPersonalLimit = limit;
	}

	@Override
	public void frightened(float time)
	{
		if (isAlive())
		{
			reverse();
			super.frightened(time);
		}
	}

	@Override
	protected int[] getStartingDirection(Map map)
	{
		int[] rtn = new int[2];

		switch (type)
		{
			case BLINKY:
				rtn[0] = -1;
				break;
			case PINKY:
				rtn[1] = -1;
				break;
			case INKY:
				rtn[1] = 1;
				break;
			case CLYDE:
				rtn[1] = 1;
				break;
		}

		return rtn;
	}

	public void release()
	{
		penned = false;

		if (type == Ghosts.CLYDE)
			setCruiseElroy(false);
	}

	@Override
	public void inCenter(Map map)
	{
		final int tile = map.getTileAt(this);

		// if we need to reverse, and we are not in the ghost pen
		if (reverse && (tile & (GP | GG)) == 0)
		{
			int reqTile = map.getTileAt(Map.getTileX(this) - getFacingX(),
					Map.getTileY(this) - getFacingY());

			// if turning around makes us run into a wall, wait till next tile
			if (isTileWalkable(tile, reqTile))
			{
				// GD is just above the GG. if you turn around while moving up,
				// you
				// will go back into the pen
				if (((tile & GD) == 0)
						|| ((tile & GD) != 0 && getFacingX() != 0 && getFacingY() == 0))
				{
					requestDirection(-getFacingX(), -getFacingY());
					reverse = false;
					setDirection(-getFacingX(), -getFacingY());
				}
			}
		}
	}

	public void reverse()
	{
		if (isAlive())
			reverse = true;
	}

	public static void setCruiseElroy(boolean suspendElroy)
	{
		Ghost.suspendElroy = suspendElroy;
	}

	public Ghosts getType()
	{
		return type;
	}

	public boolean isPenned()
	{
		return penned;
	}

	public void clearPersonalPacCounter()
	{
		pacPersonalCount = 0;
	}

	@Override
	public void reset(Map map, int spawnX, int spawnY)
	{
		super.reset(map, spawnX, spawnY);
		renderGhost = true;
		reverse = false;

		if (type == Ghosts.BLINKY)
			penned = false;
		else
			penned = true;
	}

	@Override
	public void collide(Map map, Player other)
	{
		if (other instanceof Pacman)
		{
			if (((Pacman) other).isDeadly() && isVulnerable())
			{
				penned = true;
				renderGhost = false;
				clearFrightenedTime();
				map.ghostKilled(getX(), getY());
			}
		}
	}

	@Override
	protected boolean isDeadly()
	{
		return !isFrightened() && isAlive();
	}

	@Override
	protected boolean isVulnerable()
	{
		return isFrightened() && isAlive();
	}

	@Override
	public boolean isAlive()
	{
		return renderGhost;
	}

	@Override
	public boolean isTileWalkable(int currentTile, int checkingTile)
	{
		// if we are sitting on a wall, something must have gone wrong...so just
		// let us off
		if ((currentTile & B) != 0)
			return true;

		// if the checking tile is a wall, it is not walkable
		if ((checkingTile & B) != 0)
			return false;

		// prevent ghosts from going into the pen, but allow them to come out
		if ((checkingTile & GG) != 0 && (currentTile & GP) == 0 && !penned)
			return false;

		return true;
	}

	@Override
	public void render()
	{
		MM.pushMatrix();
		MM.translate(getX(), getY(), getZ());

		if (isFrightened())
		{
			AGEColor color = BLUE;
			frightened.setColor(ORANGE);

			if (getFrightenedTime() < 2)
			{
				float delay = getFrightenedTime() * 2.5f;
				if (delay - (int) delay > 0.5f)
				{
					color = WHITE;
					frightened.setColor(RED);
				}
			}

			((Sprite) getDrawable()).setColor(color);
			getDrawable().draw();

			frightened.draw();
		}
		else
		{
			if (isAlive())
			{
				type.setColor((Sprite) getDrawable());
				getDrawable().draw();
			}

			if (getFacingX() < 0)
				left.draw();
			else if (getFacingX() > 0)
				right.draw();
			else if (getFacingY() < 0)
				down.draw();
			else if (getFacingY() > 0)
				up.draw();
		}

		MM.popMatrix();
	}

	public void update(Map map, float delta)
	{
		int tile = map.getTileAt(this);

		if (penned && (tile & GP) != 0)
		{
			// if using the personal pac counter, check if we should be release
			if (isAlive() && !map.isUsingGlobalCounter()
					&& pacPersonalCount >= pacPersonalLimit)
			{
				release();
			}
			// if using the global pac counter, check if we should be released
			else if (isAlive() && map.isUsingGlobalCounter()
					&& map.getNextReleaseableGhost() == this
					&& map.getGlobalPacCounter() >= pacGlobalLimit)
			{
				release();

				if (type == Ghosts.CLYDE)
					map.disableGlobalCounter();
			}
			// in pen movements
			else
			{
				int[] target = type.getSpawn(map);

				if (getY() <= target[1])
					renderGhost = true;

				float speed = getSpeedRatio(map) * MAXSPEED;
				final float alignDistance = (target[0] + 0.5f) - getX();

				if (alignDistance != 0)
				{
					final float alignDirection = alignDistance
							/ Math.abs(alignDistance);

					// find how long it would take to get there
					float adjustTime = Math.abs(alignDistance) / speed;

					// decide how much time will actually be used to get to
					// center
					adjustTime = adjustTime < delta ? adjustTime : delta;

					// update position
					setX(getX() + adjustTime * alignDirection * speed);
				}

				int tiley = Map.getTileY(this);
				if (tiley > target[1])
					setDirection(0, -1);
				else if (tiley < target[1])
					setDirection(0, 1);

				setY(getY() + getDY() * speed * delta);

				((Sprite) getDrawable()).setFrameRate(getFrameMultiplier()
						* getSpeedRatio(map));
				getDrawable().update(delta);

				return;
			}
		}
		// leaving pen movement
		else if (!penned && (tile & GP) != 0 && delta > 0)
		{
			// first need to be aligned in the center
			final float center = map.getGhostDoorX() - 0.5f;
			final float distance = center - getX();
			final float speedRatio = getSpeedRatio(map);
			final float speed = speedRatio * MAXSPEED;

			// move to the center of the pen
			if (distance != 0)
			{
				final float timeToCenter = Math.abs(distance) / speed;
				final float adjustTime = timeToCenter < delta ? timeToCenter
						: delta;

				setDirection((int) (distance / Math.abs(distance)), 0);

				setX(getX() + getDX() * speed * adjustTime);
			}
			// move up out of the pen
			else
			{

				final float yCenter = map.getGhostDoorY();
				final float yDistance = yCenter - getY();

				final float timeToCenter = yDistance / speed;
				final float adjustTime = timeToCenter < delta ? timeToCenter
						: delta;

				setDirection(0, 1);
				setY(getY() + getDY() * speed * adjustTime);
			}

			((Sprite) getDrawable()).setFrameRate(getFrameMultiplier()
					* speedRatio);
			getDrawable().update(delta);
			return;
		}

		// normal Player update
		super.update(map, delta);
	}

	public static void setup(AssetManager assets)
	{
		try
		{
			Texture t = TextureLoader.getTexture("sprites/ghostUp.png", assets);
			up = new SpriteMap(t, 2, 2, 16, 16, 1);

			t = TextureLoader.getTexture("sprites/ghostDown.png", assets);
			down = new SpriteMap(t, 2, 2, 16, 16, 1);

			t = TextureLoader.getTexture("sprites/ghostLeft.png", assets);
			left = new SpriteMap(t, 2, 2, 16, 16, 1);

			t = TextureLoader.getTexture("sprites/ghostRight.png", assets);
			right = new SpriteMap(t, 2, 2, 16, 16, 1);

			t = TextureLoader.getTexture("sprites/ghostFrightened.png", assets);
			frightened = new SpriteMap(t, 2, 2, 16, 16, 1);
			frightened.setColor(ORANGE);

			ghostTexture = TextureLoader.getTexture(
					"sprites/ghostBackground.png", assets);
		}
		catch (IOException e)
		{
			Logger.e("Failed to load ghost eye textures", e);
		}
	}

	public enum Ghosts
	{
		//@formatter:off
		BLINKY(new AGEColor ( 1, 0, 0, 1 )), 
		PINKY(new AGEColor ( 1, 0.722f, 0.871f, 1 )),
		INKY(new AGEColor ( 0, 1, 0.871f, 1 )),
		CLYDE(new AGEColor ( 1, 0.722f, 0.282f, 1 ));
		//@formatter:on

		private static final int normalCount = 5;
		private static final int elroyCount = 19;

		private static final float[] SPEED_NORMAL = { 0.75f, 0.85f, 0.85f,
				0.85f, 0.95f };
		private static final float[] SPEED_TUNNEL = { 0.4f, 0.45f, 0.45f,
				0.45f, 0.5f };
		private static final float[] SPEED_FRIGHT = { 0.5f, 0.55f, 0.55f,
				0.55f, 0.6f };
		private static final float[] SPEED_ELROY1 = { 0.8f, 0.9f, 0.9f, 0.9f,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		private static final float[] SPEED_ELROY2 = { 0.85f, 0.95f, 0.95f,
				0.95f, 1.05f, 1.05f, 1.05f, 1.05f, 1.05f, 1.05f, 1.05f, 1.05f,
				1.05f, 1.05f, 1.05f, 1.05f, 1.05f, 1.05f, 1.05f };

		private static final int[] DOTS_ELROY1 = { 20, 30, 40, 40, 40, 50, 50,
				50, 60, 60, 60, 80, 80, 80, 100, 100, 100, 100, 120 };
		private static final int[] DOTS_ELROY2 = { 10, 15, 20, 20, 20, 25, 25,
				25, 30, 30, 30, 40, 40, 40, 50, 50, 50, 50, 60 };

		private final AGEColor color;

		Ghosts(AGEColor color)
		{
			this.color = color;
		}

		public int[] getSpawn(Map map)
		{
			switch (this)
			{
				case BLINKY:
					return map.getSpawnPinky();
				default:
				case PINKY:
					return map.getSpawnPinky();
				case INKY:
					return map.getSpawnInky();
				case CLYDE:
					return map.getSpawnClyde();
			}
		}

		public void setColor(Sprite m)
		{
			m.setColor(color);
		}

		public int getCruiseElroyLevel(int pacsRemaining, int levelNumber)
		{
			if (this == BLINKY && !suspendElroy)
			{
				int elroyIndex = levelNumber < elroyCount ? levelNumber
						: elroyCount - 1;

				if (pacsRemaining <= DOTS_ELROY2[elroyIndex])
					return 2;
				if (pacsRemaining <= DOTS_ELROY1[elroyIndex])
					return 1;
			}

			return 0;
		}

		public float getSpeedRatio(int pacsRemaining, int levelNumber,
				boolean inTunnel, boolean frightened)
		{
			int index = levelNumber < normalCount ? levelNumber
					: normalCount - 1;

			if (inTunnel)
				return SPEED_TUNNEL[index];
			if (frightened)
				return SPEED_FRIGHT[index];

			// if this is a cruise elroy, adjust the speed. the getCruiseElroy
			// method checks to make sure it is blinky only
			int elroyIndex = levelNumber < elroyCount ? levelNumber
					: elroyCount - 1;
			switch (getCruiseElroyLevel(pacsRemaining, levelNumber))
			{
				case 1:
					return SPEED_ELROY1[elroyIndex];
				case 2:
					return SPEED_ELROY2[elroyIndex];
			}

			return SPEED_NORMAL[index];
		}

	}

	private enum Options
	{
		L(-1, 0), R(1, 0), U(0, 1), D(0, -1);

		private int dx, dy;

		private Options(int dx, int dy)
		{
			this.dx = dx;
			this.dy = dy;
		}

		public int getDX()
		{
			return dx;
		}

		public int getDY()
		{
			return dy;
		}

		public void requestDirection(Ghost ghost)
		{
			ghost.requestDirection(dx, dy);
		}
	}

}
