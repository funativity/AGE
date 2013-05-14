package funativity.age.state.layout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import funativity.age.state.AGEGLSurfaceView;
import funativity.age.state.Scene;

/**
 * This class represents a top level View to be associated with an Activity.
 * 
 * @author wittem
 * 
 */
public abstract class AGELayout extends View
{
	/** The GLSurfaceView associated with this View, if any */
	protected AGEGLSurfaceView glView = null;

	/** The ViewGroup that should be used to add Android UI elements */
	protected ViewGroup viewGroup = null;

	/**
	 * The very top level View to be used when setting the Activities content
	 * view
	 */
	protected View topLevelView = null;

	/** The Scene to be used with the GLSurfaceView, if any */
	protected Scene scene = null;

	/**
	 * Generic Constructor for the Layout
	 * 
	 * @param context
	 *            The Context that the View is contained within
	 */
	public AGELayout(Context context)
	{
		super(context);
	}

	/**
	 * Returns the GLSurfaceView used by this Layout
	 * 
	 * @return the AGEGLSurfaceView that is being used in this Layout
	 */
	public AGEGLSurfaceView getGLSurfaceView()
	{
		return glView;
	}

	/**
	 * Returns the top level View defined in this Layout
	 * 
	 * @return The View that can be considered that highest level in the View
	 *         hierarchy.
	 */
	public View getTopLevelView()
	{
		return topLevelView;
	}

	/**
	 * The ViewGroup to be used when adding custom Android UI elements to the
	 * Layout.
	 * 
	 * @return Returns the ViewGroup to be used when adding Android UI elements.
	 */
	public abstract ViewGroup getViewGroup();

	/**
	 * Sets the Scene to be used with the AGEGLSurfaceView
	 * 
	 * @param scene
	 *            The instance of Scene to be used with the GLSurfaceView.
	 */
	public void setScene(Scene scene)
	{
		this.scene = scene;
	}

	/**
	 * Returns the Scene being used by the AGEGLSurfaceView
	 * 
	 * @return The Scene being used by the AGEGLSurfaceView
	 */
	public Scene getScene()
	{
		return scene;
	}

}
