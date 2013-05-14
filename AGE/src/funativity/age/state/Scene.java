package funativity.age.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import funativity.age.collision.CollisionManager;
import funativity.age.error.TextureTooLargeException;
import funativity.age.opengl.Entity;
import funativity.age.opengl.shaders.Technique;
import funativity.age.textures.TextureLoader;

/**
 * Holds all of the objects that are on the screen. This class will update and
 * render all objects that it has
 * 
 * @author riedla
 * 
 */
public abstract class Scene implements GLSurfaceView.Renderer
{
	/**
	 * List to hold all of the Entities
	 */
	private ArrayList<Entity> entities = new ArrayList<Entity>();

	/**
	 * List to hold all of the entities that will be added to the main list on
	 * the start of the next update call
	 */
	private ArrayList<Entity> addlist = new ArrayList<Entity>();

	/**
	 * List to hold all of the entities that will be removed from the main list
	 * on the start of the next update call
	 */
	private ArrayList<Entity> removelist = new ArrayList<Entity>();

	private float smoothedDeltaRealTime_ms = 30f;
	private float movAverageDeltaTime_ms = smoothedDeltaRealTime_ms;
	private long lastRealTimeMeasurement_ms;

	// Current number of elements to keep track of in the moving average
	private static final float MOVING_AVERAGE_PERIOD_DEFAULT = 0;
	private float curMovAveragePeriod = MOVING_AVERAGE_PERIOD_DEFAULT;

	// final number of numbers to count for moving average
	private static final float MOVING_AVERAGE_PERIOD = 40;

	// facter to help smooth out the averages
	private static final float SMOOTHING_FACTOR = 0.1f;

	private Context context;
	private CollisionManager cm;

	public Scene(Context context)
	{
		super();
		this.context = context;
		Technique.setContext(context);
		cm = new CollisionManager(this);
	}

	/**
	 * Add an entity to this scene. <BR>
	 * <BR>
	 * <B>Please note:</B> It wont actually be added until the start of the next
	 * frame to avoid synchronization issues
	 * 
	 * @param entity
	 *            entity to be added to this scene
	 */
	public void addEntity(Entity entity)
	{
		if (!addlist.contains(entity))
			addlist.add(entity);
	}

	/**
	 * Remove an entity from this scene. <BR>
	 * <BR>
	 * <B>Please note:</B> It wont actually be removed until the start of the
	 * next frame to avoid synchronization issues
	 * 
	 * @param entity
	 *            entity to be removed from this scene
	 */
	public void removeEntity(Entity entity)
	{
		if (!removelist.contains(entity) && entities.contains(entity))
			removelist.add(entity);
	}

	/**
	 * Remove ALL Entities from this scene. This wont take affect until the
	 * Beginning of the next update call
	 */
	public void removeAllEntities()
	{
		removelist.addAll(entities);
	}

	/**
	 * Get the Entity at give index
	 * 
	 * @param index
	 *            of entity to retrieve
	 * @return Entity that is at given index
	 */
	public Entity getEntity(int index)
	{
		return entities.get(index);
	}

	/**
	 * Get the list of Entities that make up this scene. It is assumed that this
	 * list wont be affected by the caller of this method (unless you know what
	 * you are doing)
	 * 
	 * @return list of Entities
	 */
	public List<Entity> getEntities()
	{
		return entities;
	}

	/**
	 * Gets the number of Entities in this Scene
	 * 
	 * @return count of Entities in the Scene
	 */
	public int getNumEntities()
	{
		return entities.size();
	}

	/**
	 * Update all entities in this list. This method also updates the entity
	 * list before any updates are called on the entities
	 * 
	 * @param delta
	 *            Time in seconds since the last update
	 */
	public void update(float delta)
	{
		updateLists();

		cm.checkForCollisions(delta);

		for (Entity entity : entities)
		{
			entity.update(this, delta);
		}
	}

	/**
	 * Renders all of the entities that are part of this scene.
	 */
	public void render()
	{
		for (Entity entity : entities)
		{
			entity.render();
		}
	}

	/**
	 * Update the Entity list. This should only be called outside of the update
	 * loop. This method gets called automatically by this method and in normal
	 * cases never needs to explicitly be called.
	 */
	public void updateLists()
	{
		entities.removeAll(removelist);
		entities.addAll(addlist);

		// Remove this shape's manager if it's this manager.
		for (Entity entity : removelist)
		{
			if (entity.getScene() == this)
			{
				entity.setScene(null);
			}
		}

		// Set each entity's scene to this scene.
		for (Entity entity : addlist)
		{
			entity.setScene(this);
		}

		removelist.clear();
		addlist.clear();
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		// Moving average calc
		// Code found at:
		// http://stackoverflow.com/questions/10648325/android-smooth-game-loop
		long currTimePick_ms = SystemClock.uptimeMillis();
		float realTimeElapsed_ms;
		if (lastRealTimeMeasurement_ms > 0)
		{
			realTimeElapsed_ms = (currTimePick_ms - lastRealTimeMeasurement_ms);
		}
		else
		{
			realTimeElapsed_ms = smoothedDeltaRealTime_ms;
		}

		// update the number of elements to count for in the moving average
		if (curMovAveragePeriod < MOVING_AVERAGE_PERIOD)
			curMovAveragePeriod++;

		movAverageDeltaTime_ms = (realTimeElapsed_ms + movAverageDeltaTime_ms
				* (curMovAveragePeriod - 1))
				/ curMovAveragePeriod;

		// Calc a better approximation for smooth stepTime
		smoothedDeltaRealTime_ms = smoothedDeltaRealTime_ms
				+ (movAverageDeltaTime_ms - smoothedDeltaRealTime_ms)
				* SMOOTHING_FACTOR;

		lastRealTimeMeasurement_ms = currTimePick_ms;

		// update the game, then draw
		update(smoothedDeltaRealTime_ms / 1000f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		render();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		// get the developer to reload their resources
		loadResources();

		// reload used techniques
		Technique.reloadTechniques();

		try
		{
			TextureLoader.reloadAllTextures(context.getAssets());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (TextureTooLargeException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// Initialize everything OpenGL needs to be proper here:
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		init();
		loadResources();
	}

	/**
	 * This is called in onResume() of the Scene's parent Activity. This resets
	 * any variables used in timing when the Activity is resumed.
	 */
	public void sceneReset()
	{
		lastRealTimeMeasurement_ms = 0;
		curMovAveragePeriod = MOVING_AVERAGE_PERIOD_DEFAULT;
	}

	/**
	 * Anything that should be ran initially on scene load should be done
	 * here.
	 */
	public abstract void init();

	/**
	 * Any loading of resources should be done here.
	 */
	public abstract void loadResources();

	/**
	 * Gets the AssetManager being used by this Scene
	 * @return AssetManager
	 */
	public AssetManager getAssets()
	{
		return context.getAssets();
	}

	/**
	 * Gets the CollisionManager being used by this Scene
	 * @return Collision Manager
	 */
	public CollisionManager getCollisionManager()
	{
		return cm;
	}
}