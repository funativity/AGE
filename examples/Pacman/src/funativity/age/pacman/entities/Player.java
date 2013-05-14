package funativity.age.pacman.entities;

import funativity.age.collision.CollisionListener;
import funativity.age.collision.CollisionShape;
import funativity.age.opengl.Entity;
import funativity.age.opengl.animation.Sprite;
import funativity.age.pacman.GameScene;
import funativity.age.pacman.map.Map;
import funativity.age.pacman.map.MapLoader;
import funativity.age.state.Scene;
import funativity.age.util.Geometry3f;

public abstract class Player extends Entity implements CollisionListener
{
	protected static final float MAXSPEED = 6.5f;

	// rounding buffer
	public static final float ROUNDING_MINBUFFER = 0.15f;
	public static final float ROUNDING_MAXBUFFER = 0.3f;

	private float roundingBuffer = ROUNDING_MAXBUFFER;

	// movement
	private int reqx, reqy;
	private int facingX, facingY;

	private boolean blocked = false;

	private boolean freeReversal;

	// frightened
	private float frightenedTimeLeft = 0;

	/**
	 * either x or y has to be -1 or 1 and the other has to be 0. No other
	 * options will be accepted. If unaccepted parameters are passed in, nothing
	 * will happen.
	 * 
	 * @param x
	 * @param y
	 */
	public void requestDirection(int x, int y)
	{
		// either x or y has to be -1 or 1 and the other has to be 0. No other
		// options will be accepted
		if (x * x + y * y != 1)
			return;

		reqx = x;
		reqy = y;
	}

	protected abstract float getFrameMultiplier();

	public void frightened(float time)
	{
		frightenedTimeLeft = time;
	}

	protected void clearFrightenedTime()
	{
		frightenedTimeLeft = 0;
	}

	public boolean isFrightened()
	{
		return frightenedTimeLeft > 0;
	}

	public float getFrightenedTime()
	{
		return frightenedTimeLeft;
	}

	/**
	 * Called when this Player is in the center area of the current tile. This
	 * may be called multiple times per tile, depending on the size of the
	 * updates
	 * 
	 * @param map
	 */
	protected void inCenter(Map map)
	{

	}

	/**
	 * Called when this Player enters a new tile. This is only called once when
	 * entering a new tile, and not again until a new tile is entered.
	 * 
	 * @param map
	 */
	protected void newTile(Map map)
	{

	}

	public int getFacingX()
	{
		return facingX;
	}

	public int getFacingY()
	{
		return facingY;
	}

	public int getReqX()
	{
		return reqx;
	}

	public int getReqY()
	{
		return reqy;
	}

	public void setDirection(int x, int y)
	{
		setVelocity(new Geometry3f(x, y));
		facingX = x;
		facingY = y;
	}

	protected abstract float getSpeedRatio(Map map);

	public void setFreeReversal(boolean freeReversal)
	{
		this.freeReversal = freeReversal;
	}

	public void setRoundingBuffer(float buffer)
	{
		if (buffer < ROUNDING_MINBUFFER)
			buffer = ROUNDING_MINBUFFER;
		else if (buffer > ROUNDING_MAXBUFFER)
			buffer = ROUNDING_MAXBUFFER;

		roundingBuffer = buffer;
	}

	public abstract boolean isTileWalkable(int currentTile, int checkingTile);

	protected abstract int[] getStartingDirection(Map map);

	public void reset(Map map, int spawnX, int spawnY)
	{
		setPosition(new Geometry3f(spawnX + 0.5f, spawnY));

		final int[] dir = getStartingDirection(map);
		final int dx = dir[0];
		final int dy = dir[1];

		requestDirection(dx, dy);
		setDirection(dx, dy);

		clearFrightenedTime();

		blocked = false;
	}

	@Override
	public void update(Scene scene, float delta)
	{
		if (scene instanceof GameScene)
		{
			Map map = ((GameScene) scene).getMap();
			if (map.pauseLiving() && isAlive())
				delta = 0;

			update(map, delta);

			if (isFrightened())
			{
				frightenedTimeLeft -= delta;
			}
		}
	}

	/**
	 * gets the distance to the center of the tile that this object is currently
	 * in. Returned value is positive if the center is in front of this object,
	 * negative otherwise
	 * 
	 * @return
	 */
	private float getDistanceToCurrentCenter()
	{
		// where we are on the current tile (values between -0.5 and 0.5)
		float posTileX = getX() - Map.getTileX(this);
		float posTileY = getY() - Map.getTileY(this);

		// assumes only going in one direction. Going diagonal has undefined
		// results
		// (-1==left 2==down 1==right -2==up)
		switch ((int) getDX() + (int) getDY() * 2)
		{
			default:
			case -1: // left
				return posTileX;
			case 2: // down
				return posTileY;
			case 1: // right
				return -posTileX;
			case -2: // up
				return -posTileY;
		}
	}

	/**
	 * Gets the distance to the center of the tile that this object is moving
	 * towards. Returned value is always >= 0
	 * 
	 * @return
	 */
	private float getDistanceToNextCenter()
	{
		// where we are on the current tile (values between -0.5 and 0.5)
		float posTileX = getX() - Map.getTileX(this);
		float posTileY = getY() - Map.getTileY(this);

		// assumes only going in one direction. Going diagonal has undefined
		// results
		// (-1==left 2==down 1==right -2==up)
		switch ((int) getDX() + (int) getDY() * 2)
		{
			default:
			case -1: // left
				// adjust position in tile to find distance to next tile
				if (posTileX <= 0)
					return 1 + posTileX;
				else
					return posTileX;
			case 2: // down
				// adjust position in tile to find distance to next tile
				if (posTileY <= 0)
					return 1 + posTileY;
				else
					return posTileY;
			case 1: // right
				// adjust position in tile to find distance to next tile
				if (posTileX >= 0)
					return 1 - posTileX;
				else
					return -posTileX;
			case -2: // up
				// adjust position in tile to find distance to next tile
				if (posTileY >= 0)
					return 1 - posTileY;
				else
					return -posTileY;
		}
	}

	public void update(Map map, final float delta)
	{
		if (delta <= 0)
			return;

		// simple reversal of direction (only if it is allowed)
		if (freeReversal && reqx == -getDX() && reqy == -getDY())
		{
			setDirection(reqx, reqy);
		}

		// remaining amount of time used to move
		float remainingDelta = delta;

		// speed of this object
		final float speed = getSpeedRatio(map) * MAXSPEED;

		final float extraBufferFactor = 0.999f;

		// in this loop:
		// - First move from a center to the next center area
		// - Then, move in that center area to the dead center
		// - Repeat until all delta available is used up (or unable to move)
		do
		{
			// current tile x/y (used for knowing if we entered a new tile)
			int startingTileX = Map.getTileX(this);
			int startingTileY = Map.getTileY(this);

			// distance to the center of the next tile we are facing
			final float distanceToCenterEdge = getDistanceToNextCenter()
					- roundingBuffer * extraBufferFactor;

			// if we are not inside of a center area moving towards a center
			// area, move to the next area (but no farther)
			if (!blocked && distanceToCenterEdge > 0)
			{
				// decide how much time will be used to move
				final float timeToCenter = distanceToCenterEdge / speed;
				final float adjustTime = timeToCenter < remainingDelta ? timeToCenter
						: remainingDelta;

				// first move to the center
				move(adjustTime, speed);

				// update remaining time to use
				remainingDelta -= adjustTime;
			}

			// current tile value
			int tile = map.getTileAt(this);

			// current tile x/y
			int tilex = Map.getTileX(this);
			int tiley = Map.getTileY(this);

			// get actual distance to center of tile we are in
			final float distanceToCenter = getDistanceToCurrentCenter();

			// check if we are in center area of current tile
			if (Math.abs(distanceToCenter) <= roundingBuffer)
			{
				// inform class that it is in the center of a tile
				inCenter(map);

				// only update the direction if we are allowed to request and
				// the request is meaningful
				if (reqx != getFacingX() && reqy != getFacingY())
				{
					// get requested tile
					int reqTile = map.getTileAt(tilex + reqx, tiley + reqy);

					// check if requested direction is allowed
					if (isTileWalkable(tile, reqTile))
					{
						// update direction
						setDirection(reqx, reqy);
					}
				}

				// get tile we are about to move to
				int newTile = map.getTileAt(tilex + getFacingX(), tiley
						+ getFacingY());

				// Check for wall collisions
				blocked = !this.isTileWalkable(tile, newTile);

				// move to the dead center of the tile we are in (if we can)
				if (distanceToCenter > 0 && !blocked)
				{
					final float timeInCenter = distanceToCenter / speed;
					final float adjustTime = timeInCenter < remainingDelta ? timeInCenter
							: remainingDelta;

					// first move to the edge of center
					move(adjustTime, speed);

					// update remaining time to use
					remainingDelta -= adjustTime;
				}
			}

			// inform sub-classes when entering new tile
			if (startingTileX != Map.getTileX(this)
					|| startingTileY != Map.getTileY(this))
			{
				newTile(map);
			}
		}
		while (remainingDelta > 0 && remainingDelta < 1 && !blocked);
		// as long as there is some delta to use (and its not really large
		// (probably a bug was hit)) then keep repeating

		// =================================
		// Wrap
		// =================================
		int tilex = Map.getTileX(this);
		int tiley = Map.getTileY(this);

		if (tilex <= 0 && getDX() < 0)
		{
			setX(map.getWidth() - 1);
		}
		else if (tilex >= map.getWidth() && getDX() > 0)
		{
			setX(0);
		}

		if (tiley <= 0 && getDY() < 0)
		{
			setY(map.getHeight() - 1);
		}
		else if (tiley >= map.getHeight() && getDY() > 0)
		{
			setY(0);
		}

		// =================================
		// Grid Alignment
		// =================================
		if (getDX() != 0 && speed != 0)
		{
			// find out far we need to move to be adjusted correctly
			final float alignDistance = Map.getTileY(this) - getY();

			if (alignDistance != 0)
			{
				final float alignDirection = alignDistance
						/ Math.abs(alignDistance);

				// find how long it would take to get there
				float adjustTime = Math.abs(alignDistance) / speed;

				// decide how much time will actually be used to get to center
				adjustTime = adjustTime < delta ? adjustTime : delta;

				// update position
				setY(getY() + adjustTime * alignDirection * speed);
			}
		}
		else if (getDY() != 0 && speed != 0
				&& (map.getTileAt(this) & (MapLoader.GD | MapLoader.GG)) == 0)
		{
			// find out far we need to move to be adjusted correctly
			final float alignDistance = Map.getTileX(this) - getX();

			if (alignDistance != 0)
			{
				final float alignDirection = alignDistance
						/ Math.abs(alignDistance);

				// find how long it would take to get there
				float adjustTime = Math.abs(alignDistance) / speed;

				// decide how much time will actually be used to get to center
				adjustTime = adjustTime < delta ? adjustTime : delta;

				// update position
				setX(getX() + adjustTime * alignDirection * speed);
			}
		}
	}

	private void move(final float delta, final float speed)
	{
		// dx/dy is direction, offset is how far to move based on time, and
		// delta is time
		setX(getX() + getDX() * speed * delta);
		setY(getY() + getDY() * speed * delta);

		final float speedRatio = speed / MAXSPEED;

		((Sprite) getDrawable())
				.setFrameRate(getFrameMultiplier() * speedRatio);
		getDrawable().update(delta);
	}

	protected abstract boolean isDeadly();

	protected abstract boolean isVulnerable();

	public abstract boolean isAlive();

	public abstract void collide(Map map, Player other);

	@Override
	public void onCollide(CollisionShape shape1, CollisionShape shape2,
			float delta)
	{
		if (shape2.getEntity() instanceof Player)
		{
			Player p2 = (Player) shape2.getEntity();
			collide(GameScene.getCurrentMap(), p2);
			p2.collide(GameScene.getCurrentMap(), this);

		}
	}

	@Override
	public boolean isCollide(CollisionShape shape1, CollisionShape shape2,
			float delta)
	{
		return shape1.isIntersect(shape2);
	}
}
