package funativity.age.state;

import android.content.Context;
import android.opengl.GLSurfaceView;
import funativity.age.opengl.Entity;

/**
 * This class represents our implementation of the GLSurfaceView. This class
 * generally has very little implementation and as thus we are providing a
 * general implementation of it to aid in the abstraction of Android and OpenGL.
 * 
 * @author wittem
 * 
 */
public class AGEGLSurfaceView extends GLSurfaceView
{
	/** The Context of the GameState or the GameState itself */
	protected Context context;

	/** The Scene associated with this View. */
	protected Scene renderer;

	/**
	 * Constructs a basic implementation of the GLSurfaceView.
	 * 
	 * @param context
	 *            The Context that the View exists within.
	 */
	public AGEGLSurfaceView(Context context)
	{
		super(context);

		this.context = context;
		this.setEGLContextClientVersion(2);

	}

	/**
	 * Initializes the Scene within the View. This must be called before
	 * setContentView in the Activity.
	 * 
	 * @param scene
	 *            The Scene to begin rendering
	 */
	public void start(Scene scene)
	{
		this.renderer = scene;
		setRenderer(renderer);
		setRenderMode(RENDERMODE_CONTINUOUSLY);
	}

	/**
	 * Adds an entity to the scene from the Android UI thread. Note: this should
	 * only be called from the UI thread and is not guaranteed to run
	 * immediately. Android states that it will run before the next
	 * GLSurfaceView.Renderer onDrawFrame() call.
	 * 
	 * @param e
	 *            Entity to be added to the scene
	 */
	public void queueAddEntity(final Entity e)
	{
		queueEvent(new Thread()
		{
			public void run()
			{
				renderer.addEntity(e);
			}
		});
	}

	/**
	 * Removes an entity from the scene from the Android UI thread. Note: this
	 * should only be called from the UI thread and is not guaranteed to run
	 * immediately. Android states that it will run before the next
	 * GLSurfaceView.Renderer onDrawFrame() call.
	 * 
	 * @param e
	 *            Entity to be removed from the scene
	 */
	public void queueRemoveEntity(final Entity e)
	{
		queueEvent(new Thread()
		{
			public void run()
			{
				renderer.removeEntity(e);
			}
		});
	}

	/**
	 * Removes all entities from the scene from the Android UI thread. Note:
	 * this should only be called from the UI thread and is not guaranteed to
	 * run immediately. Android states that it will run before the next
	 * GLSurfaceView.Renderer onDrawFrame() call.
	 */
	public void queueRemoveAllEntities()
	{
		queueEvent(new Thread()
		{
			public void run()
			{
				renderer.removeAllEntities();
			}
		});
	}
}
