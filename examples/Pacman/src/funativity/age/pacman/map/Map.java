package funativity.age.pacman.map;

import funativity.age.opengl.Entity;
import funativity.age.opengl.AGEColor;
import funativity.age.pacman.GameScene;
import funativity.age.pacman.entities.Fruit;
import funativity.age.pacman.entities.Ghost;
import funativity.age.pacman.entities.Pacman;
import funativity.age.pacman.entities.Player;
import funativity.age.pacman.entities.Score;

import static funativity.age.pacman.map.MapLoader.*;

public class Map
{
	// THINGS THAT STILL NEED TO BE DONE!!!

	// HUD
	// - Show level counter (fruits)
	// - Show current player (in multiplayer)
	// Animation
	// - Intro
	// - Align pacman to grid (if possible)
	// Settings
	// - Sprite resolution
	// - Level colors
	// - Pacman colors
	// Sound
	// Features
	// - Multiplayer
	// - Save high scores

	public enum Mode
	{
		SCATTER, CHASE;
	}

	// #region Private Fields

	private final GameScene scene;

	private final int width;
	private final int height;
	private final int[][] tiles;
	private final AGEColor mapColor;
	private final AGEColor altColor;
	private boolean setAltColor = false;

	// pacs
	private final int pacsTotal;
	private int pacsRemaining;
	private int globalPacCounter;
	private float spacFlickerTime = 0; // super pac flicker time

	// points
	private static final int POINTS_PACS = 10;
	private static final int POINTS_SUPERPACS = 50 - POINTS_PACS;
	private static final int[] POINTS_GHOSTKILL = { 200, 400, 800, 1600 };
	private static final int[] POINTSINDEX_GHOSTKILL = { Score.INDEX_200,
			Score.INDEX_400, Score.INDEX_800, Score.INDEX_1600 };
	private int ghostKillIndex = 0;

	// release vars
	private float autoReleaseTimer = 0;
	private boolean globalCounter = false;

	// mode vars
	private Mode mode = Mode.CHASE;
	private int modeCount = 0;
	private float modeTimeLeft = 7;
	private static final float STAY_ON_MODE = -10;

	private float frightenedTimeLeft = 0;

	private final int levelNumber;

	// spawns
	private int[] spawnPlayer = new int[2];
	private int[] spawnBlinky = new int[2];
	private int[] spawnPinky = new int[2];
	private int[] spawnInky = new int[2];
	private int[] spawnClyde = new int[2];
	private int[] spawnFruit = new int[2];
	private int[] ghostDoor = new int[2];

	// scatter locations
	private int[] scatterBlinky = new int[2];
	private int[] scatterPinky = new int[2];
	private int[] scatterInky = new int[2];
	private int[] scatterClyde = new int[2];

	// timing
	public static final float DELAY_STARTGAME = 3;
	public static final float DELAY_DEAD = 3;
	public static final float DELAY_WIN = 3;
	public static final float DELAY_LIVING = 0.6f;

	private float pauseTime = 0;
	private float pauseLivingTime = 0;

	// #endregion

	public Map(GameScene scene, int[][] tiles, int levelNumber)
	{
		this.scene = scene;

		this.width = tiles.length;
		this.height = tiles[0].length;
		this.tiles = tiles;

		this.mapColor = MapLoader.getMapColor(levelNumber);
		this.altColor = MapLoader.getAltMapColor(levelNumber);

		this.levelNumber = levelNumber;

		MapParts.loadMap(tiles, mapColor, MapLoader.getPacColor(levelNumber));

		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				if ((tiles[x][y] & PS) != 0)
					assignPoint(spawnPlayer, x, y);
				if ((tiles[x][y] & GSB) != 0)
					assignPoint(spawnBlinky, x, y);
				if ((tiles[x][y] & GSP) != 0)
					assignPoint(spawnPinky, x, y);
				if ((tiles[x][y] & GSI) != 0)
					assignPoint(spawnInky, x, y);
				if ((tiles[x][y] & GSC) != 0)
					assignPoint(spawnClyde, x, y);
				if ((tiles[x][y] & FS) != 0)
					assignPoint(spawnFruit, x, y);
				if ((tiles[x][y] & GD) != 0)
					assignPoint(ghostDoor, x, y);
				if ((tiles[x][y] & B) == 0 && (tiles[x][y] & (PAC | SPC)) != 0)
					pacsRemaining++;
			}
		}

		pacsTotal = pacsRemaining;

		Ghost.setCruiseElroy(false);
		reset();

		setGhostsPersonalPacCountLimit();
		globalCounter = false;

		getBlinky().clearPersonalPacCounter();
		getPinky().clearPersonalPacCounter();
		getInky().clearPersonalPacCounter();
		getClyde().clearPersonalPacCounter();
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int[] getSpawnBlinky()
	{
		return spawnBlinky;
	}

	public int[] getSpawnPinky()
	{
		return spawnPinky;
	}

	public int[] getSpawnInky()
	{
		return spawnInky;
	}

	public int[] getSpawnClyde()
	{
		return spawnClyde;
	}

	public int[] getSpawnFruit()
	{
		return spawnFruit;
	}

	public int getGhostDoorX()
	{
		return ghostDoor[0];
	}

	public int getGhostDoorY()
	{
		return ghostDoor[1];
	}

	public int getTileAt(int x, int y)
	{
		if (x < 0 || y < 0 || x >= width || y >= height)
		{
			return 0;
		}

		return tiles[x][y];
	}

	public static final int getTileX(Player player)
	{
		return (int) (player.getX() + 0.5f);
	}

	public static final int getTileY(Player player)
	{
		return (int) (player.getY() + 0.5f);
	}

	public int getTileAt(Player player)
	{
		return getTileAt(getTileX(player), getTileY(player));
	}

	public int getPacsRemaining()
	{
		return pacsRemaining;
	}

	public int getGlobalPacCounter()
	{
		return globalPacCounter;
	}

	public Mode getMode()
	{
		return mode;
	}

	public boolean pauseLiving()
	{
		return pauseLivingTime > 0;
	}

	public void spawnFruit()
	{
		final float duration = (float) (Math.random() + 9);
		final Fruit.Fruits fruitIndex = Fruit.Fruits
				.getLevelFruitIndex(levelNumber);

		scene.addFruit(spawnFruit[0], spawnFruit[1], duration, fruitIndex);
	}

	public void eatFruit(Fruit.Fruits fruitType)
	{
		scene.addToScore(fruitType.getPoints());
		scene.showScore(spawnFruit[0], spawnFruit[1], fruitType.getScoreIndex());
	}

	public boolean tryToEatAt(Player player)
	{
		int x = getTileX(player);
		int y = getTileY(player);

		switch (getTileAt(x, y) & ALL_PACS)
		{
			case SPC:
				frightened();
				scene.addToScore(POINTS_SUPERPACS);
			case PAC:
				autoReleaseTimer = 0;
				pacsRemaining--;
				scene.addToScore(POINTS_PACS);

				Ghost next = getNextReleaseableGhost();
				if (next != null)
				{
					if (globalCounter)
					{
						globalPacCounter++;
					}
					else
					{
						next.addToPacCounter();
					}
				}

				if (pacsRemaining <= 0)
					pauseFor(DELAY_WIN);

				final int pacsEaten = pacsTotal - pacsRemaining;
				if (pacsEaten == 70 || pacsEaten == 170)
					spawnFruit();

				// remove pac
				tiles[x][y] = tiles[x][y] & ~ALL_PACS;
				MapParts.removePacAt(y * width + x);

				return true;
		}

		return false;
	}

	public void ghostKilled(float x, float y)
	{
		scene.addToScore(POINTS_GHOSTKILL[ghostKillIndex]);
		scene.showScore(x, y, POINTSINDEX_GHOSTKILL[ghostKillIndex]);
		ghostKillIndex++;
		pauseLivingTime = DELAY_LIVING;
	}

	public void pacmanKilled()
	{
		scene.addToLives(-1);
		Ghost.setCruiseElroy(true);
		globalCounter = true;
		pauseFor(DELAY_DEAD);
	}

	private void frightened()
	{
		frightenedTimeLeft = getFrenzyTime();
		ghostKillIndex = 0;

		for (Entity e : scene.getEntities())
		{
			if (e instanceof Player)
			{
				((Player) e).frightened(frightenedTimeLeft);
			}
		}
	}

	public Ghost getNextReleaseableGhost()
	{
		if (getBlinky().isPenned() && (getTileAt(getBlinky()) & GP) != 0
				&& getBlinky().isAlive())
			return getBlinky();
		if (getPinky().isPenned() && (getTileAt(getPinky()) & GP) != 0
				&& getPinky().isAlive())
			return getPinky();
		if (getInky().isPenned() && (getTileAt(getInky()) & GP) != 0
				&& getInky().isAlive())
			return getInky();
		if (getClyde().isPenned() && (getTileAt(getClyde()) & GP) != 0
				&& getClyde().isAlive())
			return getClyde();

		return null;
	}

	private void assignPoint(int[] point, int x, int y)
	{
		point[0] = x;
		point[1] = y;
	}

	public boolean isUsingGlobalCounter()
	{
		return globalCounter;
	}

	public void disableGlobalCounter()
	{
		globalCounter = false;
	}

	private void reset()
	{
		// reset entities
		getPacman().reset(this, spawnPlayer[0], spawnPlayer[1]);
		getBlinky().reset(this, spawnBlinky[0], spawnBlinky[1]);
		getPinky().reset(this, spawnPinky[0], spawnPinky[1]);
		getInky().reset(this, spawnInky[0], spawnInky[1]);
		getClyde().reset(this, spawnClyde[0], spawnClyde[1]);

		scene.removeFruit();

		// mode reset
		modeCount = 0;
		setNextMode(false);

		// game play timers
		frightenedTimeLeft = 0;
		autoReleaseTimer = 0;

		// pac counter (use global counter if this is not the start of a level)
		globalPacCounter = 0;

		// timing
		pauseFor(DELAY_STARTGAME);
	}

	/**
	 * return true if everything else has a normal update. false if everything
	 * else uses 0 as the delta (returning false will force everything to
	 * "pause" for the frame)
	 * 
	 * This update is intended only for updating global level timers
	 * 
	 * @param delta
	 * @return
	 */
	public boolean update(float delta)
	{
		spacFlickerTime -= delta;
		if (spacFlickerTime < 0)
			spacFlickerTime = 1;

		if (pauseTime > 0)
		{
			if ((pauseTime -= delta) <= 0)
			{
				if (scene.addToLives(0) <= 0)
					return true;

				if (pacsRemaining <= 0)
					scene.loadNextMap();
				else if (!getPacman().isAlive())
					reset();
			}

			if (!getPacman().isAlive())
				return true;
			return false;
		}

		if (pauseLiving())
		{
			pauseLivingTime -= delta;

			// dont update release or frightened timer during this freeze
		}
		else
		{
			// release ghosts from pen on a timer. If pacman takes too long to
			// eat
			// pacs, new ghosts will be realsed
			if ((autoReleaseTimer += delta) >= 4)
			{
				autoReleaseTimer = 0;
				Ghost next = getNextReleaseableGhost();
				if (next != null)
				{
					next.release();
				}
			}

			// time to change modes
			if (frightenedTimeLeft > 0)
			{
				frightenedTimeLeft -= delta;
			}
			else
			{
				if (modeTimeLeft > STAY_ON_MODE && (modeTimeLeft -= delta) <= 0)
				{
					setNextMode(true);
				}
			}
		}

		return true;
	}

	/**
	 * render this map. Return true if the rest of the normal render method
	 * should be called
	 * 
	 * @return
	 */
	public boolean render()
	{
		MapParts.setMapColor(setAltColor ? altColor : mapColor);
		MapParts.renderMap();

		if (pauseTime > 0 && pacsRemaining <= 0)
		{
			scene.getPacman().render();
			float delay = pauseTime * 2.5f;
			if (delay - (int) delay > 0.5f)
				setAltColor = true;
			else
				setAltColor = false;
		}
		else
		{
			MapParts.renderExtras();
			return true;
		}

		return false;
	}

	public void pauseFor(float time)
	{
		pauseTime = time;
	}

	public float getPauseTime()
	{
		return pauseTime;
	}

	public void setScatterTile(Ghost ghost)
	{
		switch (ghost.getType())
		{
			case BLINKY:
				ghost.setTargetTile(scatterBlinky[0], scatterBlinky[1]);
				break;
			case PINKY:
				ghost.setTargetTile(scatterPinky[0], scatterPinky[1]);
				break;
			case INKY:
				ghost.setTargetTile(scatterInky[0], scatterInky[1]);
				break;
			case CLYDE:
				ghost.setTargetTile(scatterClyde[0], scatterClyde[1]);
				break;
		}
	}

	public void setScatterBlinky(int x, int y)
	{
		scatterBlinky = new int[] { x, y };
	}

	public void setScatterPinky(int x, int y)
	{
		scatterPinky = new int[] { x, y };
	}

	public void setScatterInky(int x, int y)
	{
		scatterInky = new int[] { x, y };
	}

	public void setScatterClyde(int x, int y)
	{
		scatterClyde = new int[] { x, y };
	}

	public int getLevelNumber()
	{
		return levelNumber;
	}

	public Pacman getPacman()
	{
		return scene.getPacman();
	}

	public Ghost getBlinky()
	{
		return scene.getBlinky();
	}

	public Ghost getPinky()
	{
		return scene.getPinky();
	}

	public Ghost getInky()
	{
		return scene.getInky();
	}

	public Ghost getClyde()
	{
		return scene.getClyde();
	}

	private float getFrenzyTime()
	{
		switch (levelNumber + 1)
		{
			case 1:
				return 6;
			case 2:
			case 6:
			case 10:
				return 5;
			case 3:
				return 4;
			case 4:
			case 14:
				return 3;
			case 5:
			case 7:
			case 8:
			case 11:
				return 2;
			case 9:
			case 12:
			case 13:
			case 15:
			case 16:
			case 18:
				return 1;
			default:
				return 0;
		}
	}

	private void setGhostsPersonalPacCountLimit()
	{
		switch (levelNumber + 1)
		{
			case 1:
				getBlinky().setPersonalPacCountLimit(0);
				getPinky().setPersonalPacCountLimit(0);
				getInky().setPersonalPacCountLimit(30);
				getClyde().setPersonalPacCountLimit(60);
				break;
			case 2:
				getBlinky().setPersonalPacCountLimit(0);
				getPinky().setPersonalPacCountLimit(0);
				getInky().setPersonalPacCountLimit(0);
				getClyde().setPersonalPacCountLimit(50);
				break;
			default:
				getBlinky().setPersonalPacCountLimit(0);
				getPinky().setPersonalPacCountLimit(0);
				getInky().setPersonalPacCountLimit(0);
				getClyde().setPersonalPacCountLimit(0);
		}
	}

	private void setNextMode(boolean informGhosts)
	{
		modeCount++;
		if (modeCount % 2 == 0)
			mode = Mode.CHASE;
		else
			mode = Mode.SCATTER;

		if (levelNumber == 0)
		{
			switch (modeCount)
			{
				case 1:
				case 3:
					modeTimeLeft = 7;
					break;
				case 5:
				case 7:
					modeTimeLeft = 5;
					break;
				case 2:
				case 4:
				case 6:
					modeTimeLeft = 20;
					break;
				default:
					modeTimeLeft = STAY_ON_MODE;
			}
		}
		else if (levelNumber < 4)
		{
			switch (modeCount)
			{
				case 1:
				case 3:
					modeTimeLeft = 7;
					break;
				case 5:
					modeTimeLeft = 5;
					break;
				case 7:
					modeTimeLeft = 1f / 60f;
					break;
				case 2:
				case 4:
					modeTimeLeft = 20;
					break;
				case 6:
					modeTimeLeft = 1033;
					break;
				default:
					modeTimeLeft = STAY_ON_MODE;
			}
		}
		else
		{
			switch (modeCount)
			{
				case 5:
				case 1:
				case 3:
					modeTimeLeft = 5;
					break;
				case 7:
					modeTimeLeft = 1f / 60f;
					break;
				case 2:
				case 4:
					modeTimeLeft = 20;
					break;
				case 6:
					modeTimeLeft = 1037;
					break;
				default:
					modeTimeLeft = STAY_ON_MODE;
			}
		}

		if (informGhosts)
		{
			getBlinky().reverse();
			getPinky().reverse();
			getInky().reverse();
			getClyde().reverse();
		}
	}
}
