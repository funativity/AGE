package age.poc;

import java.util.HashSet;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import age.dataModels.Boat;
import age.dataModels.Coordinate;
import age.dataModels.GameData;
import age.database.BattleShipDB;
import age.enums.BoatType;
import age.enums.Direction;
import age.enums.GameStatus;
import age.enums.SubState;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.Drawable;
import funativity.age.opengl.Entity;
import funativity.age.opengl.MM;
import funativity.age.opengl.Mesh;
import funativity.age.opengl.animation.SpriteMap;
import funativity.age.opengl.primitive.Rectangle;
import funativity.age.state.Scene;
import funativity.age.textures.Texture;
import funativity.age.textures.TextureLoader;
import funativity.age.util.Geometry3f;
import funativity.age.util.Logger;

/**
 * This class holds all the logic for the battleship screen and updating all data
 * @author vancer
 *
 */
public class GridScene extends Scene
{
	private static float FIRE_FRAME_RATE = 5;
	private static float MAX_ZOOM = 1.3f;
	private static float MIN_ZOOM = 0.4f;
	public static final float GRID_SIZE = 10;
	public static final float HALF_GRID_SIZE = GRID_SIZE / 2;
	public static final int TILE_COUNT = 10;
	public static final float TILE_SIZE = GRID_SIZE / TILE_COUNT;

	private BattleShipDB dataBase;

	private Entity gridEntity;
	private Entity waterEntity;

	private SpriteMap hitMesh;
	private Rectangle missMesh;

	// Game state.
	public GameData gameData;
	public SubState subState;
	private boolean myGrid;
	private boolean nextMyGrid;
	private MainActivity activity;

	// Firing phase.
	private Entity reticle;
	public Coordinate target = new Coordinate(0, 0);

	// Screen size in pixels.
	public int screenWidth, screenHeight;

	// Scene projection modifiers.
	private float projScale = 1f, projMoveScale = 1;
	private float projWidth, projHeight;
	private float projX, projY;

	private float camX, camY;
	private float camMoveX, camMoveY;

	public GridScene(Context context)
	{
		super(context);
		this.activity = (MainActivity) context;

	}

	@Override
	public void init()
	{
		// move camera so the grid is in the center of the screen
		resetCameraPosition();

		// create database handle
		dataBase = new BattleShipDB();

		// clear boat meshes so they can reload
		for (BoatType type : BoatType.values())
		{
			if (type.mesh != null)
				type.mesh.delete();
			type.mesh = null;
		}

		// setup reticle
		try
		{
			Rectangle m = new Rectangle(TILE_SIZE, TILE_SIZE);
			m.setTexture(TextureLoader.getTexture("images/Target.png",
					getAssets()));
			reticle = new Entity(m);
		}
		catch (Exception e)
		{
			Logger.e("Failed to load reticle texture", e);
		}

		Texture hitTexture = null;
		Texture missTexture = null;

		try
		{
			// create hit and miss textures
			hitTexture = TextureLoader.getTexture("images/Fire.png",
					getAssets());
			missTexture = TextureLoader.getTexture("images/Miss.png",
					getAssets());
		}
		catch (Exception e)
		{
			Logger.e("Failed to load hit/miss textures", e);
		}

		hitMesh = new SpriteMap(hitTexture, TILE_SIZE, TILE_SIZE,
				TILE_SIZE / 2f, TILE_SIZE / 2f, 64, 64, 16);
		missMesh = new Rectangle(TILE_SIZE, TILE_SIZE, TILE_SIZE / 2f,
				TILE_SIZE / 2f);
		missMesh.setTexture(missTexture);
		missMesh.updateTexCoordData(0, 1, 1, 0);

		refresh();
	}

	/**
	 * Refresh the game data
	 */
	public void refresh()
	{
		// Query DB for data
		gameData = dataBase.getGameData(activity.gameID, activity.userID);

		// Check if ships have been placed (enter ship placement sub-state)
		if (gameData.userBoats.size() == 0)
		{
			subState = SubState.PlaceShips;

			// add all of the boats to the list in default locations
			gameData.userBoats.add(new Boat(BoatType.AIRCRAFT_CARRIER,
					new Coordinate(0, 0), Direction.VERTICAL));
			gameData.userBoats.add(new Boat(BoatType.BATTLESHIP,
					new Coordinate(1, 0), Direction.VERTICAL));
			gameData.userBoats.add(new Boat(BoatType.SUBMARINE, new Coordinate(
					2, 0), Direction.VERTICAL));
			gameData.userBoats.add(new Boat(BoatType.DESTROYER, new Coordinate(
					3, 0), Direction.VERTICAL));
			gameData.userBoats.add(new Boat(BoatType.PATROL_BOAT,
					new Coordinate(4, 0), Direction.VERTICAL));
		}
		else
		{
			updateGameStatus();
		}

		// look at user grid if they need to place ships, or it is NOT their
		// turn
		setGrid(subState == SubState.PlaceShips || !gameData.isCurrentUsersTurn);
	}

	/**
	 * Reset the camera zoom on the grid back to initial state
	 */
	public void resetCameraZoom()
	{
		projScale = 1;
	}

	/**
	 * Get the projected scale
	 * @return Returns the projected scale
	 */
	public float getProjScale()
	{
		return projScale;
	}

	/**
	 * Zoom on the grid
	 * @param scale The scale to zoom in/out to
	 */
	public void zoom(float scale)
	{
		projMoveScale *= scale;
	}

	/**
	 * Move the camera back to the center of the screen
	 */
	public void resetCameraPosition()
	{
		moveCameraBy(HALF_GRID_SIZE - camX, HALF_GRID_SIZE - camY);
	}

	/**
	 * Move the camera by an offset. this change will take place during the next
	 * frame.
	 * 
	 * @param x
	 * @param y
	 */
	public void moveCameraBy(float x, float y)
	{
		camMoveX += x;
		camMoveY += y;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		// Store the screen size.
		screenWidth = width;
		screenHeight = height;

		// Make a square ratio for a portrait orientation.
		float heightRatio = height / (float) width;
		projWidth = GRID_SIZE;
		projHeight = GRID_SIZE * heightRatio;

		// Set the initial perspective.
		updateProjection();

		// Why not.
		super.onSurfaceChanged(gl, width, height);
	}

	/**
	 * Call this once all of the boats are in the desired location and user
	 * wants to save them
	 */
	public void saveBoatLocations()
	{
		dataBase.setBoats(gameData.userBoats, activity.gameID, activity.userID);
	}

	/**
	 * Send the target as a shot to the database. does not do any checking to
	 * ensure the shot is on the grid. also pushes the list of boats, so any
	 * changes to the boats needs to be done before this is called.
	 */
	public void submitShot()
	{
		// make a new instance so we can save it in the list
		Coordinate c = new Coordinate(target);
		getGridShots().add(c);

		if (c.hit)
		{
			dataBase.addHitShot(getGridShots(), activity.gameID,
					activity.userID, getGridBoats());
		}
		else
		{
			dataBase.addMissedShot(getGridShots(), activity.gameID,
					activity.userID);
		}
	}

	/**
	 * Obtain the coordinate of where the user touched the screen
	 * @param event The touch event, is fired when user touches the screen
	 * @return The coordinate on the grid where the user touched
	 */
	public Coordinate getTouchCoordinate(MotionEvent event)
	{
		// Portrait orientation-related values.
		float defaultSize = screenWidth;
		float startLeft = 0;
		float startTop = (screenHeight - screenWidth) / 2;

		// Calculate the new size of the grid in pixels.
		float size = defaultSize / projScale;
		float halfSize = size / 2;

		// get the offset of the camera
		float camXOffset = (camX - HALF_GRID_SIZE) * (size / GRID_SIZE);
		float camYOffset = (camY - HALF_GRID_SIZE) * (size / GRID_SIZE);

		// Get the touch location in pixels.
		float touchX = event.getX() + camXOffset;
		float touchY = event.getY() + camYOffset;

		// Calculate the extra non-grid space in pixels.
		float extraX = -projX * size / GRID_SIZE;
		float extraY = projY * size / GRID_SIZE;
		float extraScale = (defaultSize - size) / 2;

		// Add up all the extra space.
		float extraLeft = startLeft + extraScale + extraX;
		float extraTop = startTop + extraScale + extraY;

		// Calculate the X boundaries in pixels.
		float midX = screenWidth / 2 + extraX;
		float minX = midX - halfSize;
		float maxX = midX + halfSize;

		// Calculate the Y boundaries in pixels.
		float midY = screenHeight / 2 + extraY;
		float minY = midY - halfSize;
		float maxY = midY + halfSize;

		// Detect if the touch is within the boundaries.
		boolean withinX = touchX > minX && touchX < maxX;
		boolean withinY = touchY > minY && touchY < maxY;
		if (withinX && withinY)
		{
			// Convert from pixels to OpenGL values, inverting Y.
			float x = GRID_SIZE * (touchX - halfSize - extraLeft) / size;
			float y = GRID_SIZE * -(touchY - halfSize - extraTop) / size;

			// Convert from origin system to upper left (0, 0) system.
			int tileX = (int) Math.floor(x + HALF_GRID_SIZE);
			int tileY = (int) -Math.ceil(y - HALF_GRID_SIZE);
			return new Coordinate(tileX, tileY);
		}

		// Touch occurred outside the grid.
		return null;
	}

	/**
	 * Set the grid to specified grid
	 * @param myGrid The grid to set this class's grid to
	 */
	public void setGrid(boolean myGrid)
	{
		nextMyGrid = myGrid;
	}

	/**
	 * Sets the displayed grid to either the players grid, or the enemies grid.
	 * Only should be done on OpenGL thread
	 * 
	 * @param myGrid
	 */
	private void setGridLocal(boolean myGrid)
	{
		this.myGrid = myGrid;

		// remove everything off of the screen
		removeAllEntities();
		updateLists();

		// get the list of boats that are on this grid
		List<Boat> boats = getGridBoats();

		try
		{
			// create the water only if it has not been made yet.
			if (waterEntity == null)
			{
				Rectangle waterMesh = new Rectangle(GRID_SIZE * 2.5f,
						GRID_SIZE * 2.5f);
				waterMesh.setTexture(TextureLoader.getTexture(
						"images/Water.png", getAssets()));
				waterMesh.updateTexCoordData(0, TILE_COUNT / 2, 0,
						TILE_COUNT / 2);
				waterEntity = new Entity(waterMesh);
				waterEntity.setPosition(new Geometry3f(HALF_GRID_SIZE,
						HALF_GRID_SIZE));
			}

			// create grid only if it has not been made yet.
			if (gridEntity == null)
			{
				Rectangle gridMesh = new Rectangle(GRID_SIZE, GRID_SIZE);
				gridMesh.updateTexCoordData(0, TILE_COUNT, 0, TILE_COUNT);
				gridMesh.setColor(new AGEColor(1.0f, 0.8f, 0.0f));
				gridMesh.setTexture(TextureLoader.getTexture("images/tile.png",
						getAssets()));
				gridEntity = new Entity(gridMesh);
				gridEntity.setPosition(new Geometry3f(HALF_GRID_SIZE,
						HALF_GRID_SIZE));
			}

		}
		catch (Exception e)
		{
			Logger.e("Failed to create resources for grid", e);
		}

		// add water and grid to the screen.
		addEntity(waterEntity);
		addEntity(gridEntity);

		// add the boats to the grid
		for (Boat boat : boats)
		{
			// only show boat if it is user's boat, or if it is dead.
			if (myGrid || boat.isBoatDead())
			{
				// create mesh size of boat, offset so head is on one
				// side of mesh
				Mesh boatMesh = boat.CreateSprite(getAssets(), TILE_SIZE);

				Entity head = new Entity(boatMesh);

				// make a reference to the entity
				boat.setEntity(head);

				// move the boat to the right location
				boat.moveHeadTo(boat.getHead());

				// rotate boat if it needs to be
				boat.updateDirection();

				// add to screen
				addEntity(head);
			}
		}

		int hitsOnGrid = 0;
		// add shots
		for (Coordinate s : getGridShots())
		{
			Drawable shotMesh = null;
			if (s.hit)
			{
				shotMesh = hitMesh;

				// TODO fix hack
				// hack to fix frame rate changes in fire
				hitsOnGrid++;
				hitMesh.setFrameRate(FIRE_FRAME_RATE / hitsOnGrid);
			}
			else
			{
				shotMesh = missMesh;
			}

			Entity shot = new Entity(shotMesh);
			shot.setPosition(new Geometry3f(s.x, s.y));
			addEntity(shot);
		}

		if (subState == SubState.Fire && !this.myGrid)
		{
			addEntity(reticle);
			setTarget(target);
		}

		updateUI();
	}

	/**
	 * Update and refresh all the elements on the screen
	 */
	private void updateUI()
	{
		Handler refresh = new Handler(Looper.getMainLooper());
		refresh.post(new Runnable()
		{
			public void run()
			{
				activity.updateUI();
			}
		});
	}
	
	/**
	 * This will update the game status based on the game data
	 */
	public void updateGameStatus()
	{
		// Check if game over (enter game over sub-state)
		// If game is over, this method ends early
		switch (getGameStatus())
		{
			case WIN:
				break;
			case LOSE:
				subState = SubState.GameOver;
				break;
			case IN_PROGRESS:
			default:
				// only change state if not placing ships
				if (subState != SubState.PlaceShips)
				{
					// Check turn (enter corresponding sub-state)
					if (gameData.isCurrentUsersTurn)
						subState = SubState.Fire;
					else
						subState = SubState.View;
					break;
				}
		}
	}

	/**
	 * Determine the current state of the game. Returns LOSE if the user has
	 * lost, WIN if the user has won, otherwise IN_PROGRESS will be returned.
	 * 
	 * @return the current state of the game
	 */
	public GameStatus getGameStatus()
	{
		GameStatus rtn = GameStatus.IN_PROGRESS;
		boolean alive = false;

		// make alive to true if any of user boats are alive
		for (Boat b : gameData.userBoats)
		{
			if (!b.isBoatDead())
				alive = true;
		}

		// if none of user's boats are alive, user lost
		if (!alive)
			rtn = GameStatus.LOSE;

		// do same check for opponent only if they already placed their boats
		if (gameData.enemyBoats.size() > 0)
		{
			alive = false;
			for (Boat b : gameData.enemyBoats)
			{
				if (!b.isBoatDead())
					alive = true;
			}
			if (!alive)
				rtn = GameStatus.WIN;
		}

		return rtn;
	}

	/**
	 * Get the active set of boats on the grid, either the user's or enemy's.
	 * 
	 * @return the active set of boats on the grid
	 */
	public List<Boat> getGridBoats()
	{
		return myGrid ? gameData.userBoats : gameData.enemyBoats;
	}

	/**
	 * Get the active set of shots on the grid, either the user's or enemy's.
	 * 
	 * @return the active set of shots on the grid
	 */
	public List<Coordinate> getGridShots()
	{
		return myGrid ? gameData.enemyShots : gameData.userShots;
	}

	/**
	 * Looks through the active set of boats (either the user's boats or enemy's
	 * boats) on the grid to see if a part of a boat exists on a coordinate.
	 * 
	 * @param coord
	 *            coordinate on grid
	 * @return first boat found at the coordinate, null otherwise
	 */
	public Boat getBoatAt(Coordinate coord)
	{
		for (Boat boat : getGridBoats())
		{
			if (boat.isAt(coord))
			{
				return boat;
			}
		}

		return null;
	}

	/**
	 * Returns whose grid is being displayed. either true for user's grid, false
	 * for enemies
	 * 
	 * @return
	 */
	public boolean isMyGrid()
	{
		return myGrid;
	}

	/**
	 * Goes through the list of active shots on the grid and checks if the
	 * target coordinate has already been shot at.
	 * 
	 * @param target
	 *            coordinate to check
	 * @return false if the coordinate has already been shot at, true otherwise
	 */
	public boolean isTargetable(Coordinate target)
	{
		for (Coordinate shot : getGridShots())
		{
			if (target.x == shot.x && target.y == shot.y)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Sets the targeted coordinate's location and updates the texture to show
	 * or not show. This should only be called by the activity through a queued
	 * event due to threading issues.
	 * 
	 * @param target
	 *            coordinate to place visible reticle, use null to hide the
	 *            reticle
	 */
	public void setTarget(Coordinate target)
	{
		if (target != null)
		{
			this.target = target;

			// Move and show the reticle.
			reticle.setPosition(new Geometry3f(target.x + TILE_SIZE / 2,
					target.y + TILE_SIZE / 2));
		}
	}

	/**
	 * Determines if the placing of all the boats is valid. It is valid only if
	 * each boat is fully in the grid and does not overlap any other boat.
	 * 
	 * @return true if the placing is valid, false otherwise
	 */
	public boolean isValidPlacing()
	{
		// Track all boat coordinates in a set to see if each is used only once.
		HashSet<Coordinate> allCoords = new HashSet<Coordinate>();
		for (Boat boat : getGridBoats())
		{
			List<Coordinate> boatCoords = boat.getCoordinates();
			for (Coordinate boatCoord : boatCoords)
			{
				// Check if the coordinate is in the grid.
				boolean withinX = boatCoord.x >= 0
						&& boatCoord.x <= TILE_COUNT - 1;
				boolean withinY = boatCoord.y >= 0
						&& boatCoord.y <= TILE_COUNT - 1;
				if (!(withinX && withinY))
				{
					return false;
				}

				// If it fails to add, it already is occupied.
				if (!allCoords.add(boatCoord))
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Move the camera to designated location. Providing (0,0) will move tile
	 * (0,0) to the center of the screen. Limits the bounds of the camera's
	 * position so that the grid is always in view.
	 * 
	 * This should ONLY be called on the OpenGL thread. Specifically during the
	 * update method.
	 * 
	 * @param x
	 * @param y
	 */
	private void moveCameraTo(float x, float y)
	{
		float scaleOffset = TILE_SIZE * projScale * 2;
		float min = 0 + scaleOffset;
		float max = GRID_SIZE - scaleOffset;

		// check bounds of both x and y
		camX = x < min ? min : x > max ? max : x;
		camY = y < min ? min : y > max ? max : y;

		// offset the screen so the upper left corner is tile (0,0)
		MM.lookAt(camX, camY, -1, camX, camY, 0, 0, -1, 0);
		// set up so that we count up going down
	}

	/**
	 * Only do this on the OpenGL thread.
	 */
	private void updateProjection()
	{
		// Center the projection on the X and Y coordinate.
		float x = projScale * projWidth / 2f;
		float y = projScale * projHeight / 2f;
		MM.ortho(projX - x, projX + x, projY + y, projY - y);
	}

	@Override
	public void update(float delta)
	{
		updateGameStatus();

		this.updateLists();
		setGridLocal(nextMyGrid);

		super.update(delta);

		// update the camera's position
		moveCameraTo(camX + camMoveX, camY + camMoveY);
		camMoveX = 0;
		camMoveY = 0;

		// update the camera's zoom
		projScale *= projMoveScale;
		projScale = projScale < MIN_ZOOM ? MIN_ZOOM
				: projScale > MAX_ZOOM ? MAX_ZOOM : projScale;
		updateProjection();
		projMoveScale = 1;
	}

	@Override
	public void loadResources()
	{
		//Do nothing at the moment
	}
}