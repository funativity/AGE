package funativity.age.state;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import funativity.age.state.layout.AGELayout;
import funativity.age.util.AGEMediaPlayer;
import funativity.age.util.AudioManager;

/**
 * The GameState represents the Android Activity. It can be thought of as a
 * separate state within the game and should be used for such a purpose.
 * 
 * @author wittem
 * 
 */
public abstract class GameState extends Activity implements OnTouchListener,
		OnGestureListener, OnDoubleTapListener, OnScaleGestureListener
{

	/************************ PRIVATE CLASS VARIABLES ******************************/

	/**
	 * The GLSurfaceView that the GameState contains if one exists. This is
	 * created by the Layout.
	 */
	protected AGEGLSurfaceView glView = null;

	/**
	 * Variable for keeping track of whether the game is running. Should quit
	 * upon turning false
	 */
	private boolean isRunning;

	/**
	 * This represents the Android View Layout. We have provided a few versions
	 * for your convenience. They can be used to combine an OpenGL View with any
	 * of the Android Views.
	 */
	private AGELayout layout;

	/**
	 * Used to detect gestures
	 */
	protected GestureDetector gestureScanner;
	protected ScaleGestureDetector scaleScanner;

	/************************ PUBLIC CLASS VARIABLES ******************************/

	/**************************** PUBLIC METHODS **********************************/

	@Override
	/**
	 * Called when the Activity is created.  Any state initialization
	 * should happen here.
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		// Initialize any necessary data
		super.onCreate(savedInstanceState);

		isRunning = false;

		// Pass on init to the subclass provided by the developer
		init();

		if (layout == null)
		{
			throw new RuntimeException("Need a layout");
		}
		// By here, the setContentView needs to have been called
		startEngine();
		setContentView(layout.getTopLevelView());

		gestureScanner = new GestureDetector(this, this);
		scaleScanner = new ScaleGestureDetector(this, this);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		if (glView != null && glView.renderer != null)
		{
			glView.renderer.sceneReset();
		}
	}

	@Override
	public void onPause()
	{
		for (AGEMediaPlayer player : AudioManager.getAudioManager()
				.getAllMediaPlayers())
		{
			player.stopPlaying();
		}
		super.onPause();
	}

	/**
	 * Any initialization of the GameState can happen here
	 */
	public abstract void init();

	/**
	 * Set the Layout of the GameState. This is a necessary step for any
	 * GameState.
	 * 
	 * @param layout
	 *            the AGELayout that will define the contents of the state
	 */
	public void setLayout(AGELayout layout)
	{
		if (layout.getGLSurfaceView() != null)
		{
			glView = layout.getGLSurfaceView();
			glView.setOnTouchListener(this);
		}

		this.layout = layout;
	}

	/**
	 * Returns the ViewGroup to be used when adding Android UI elements.
	 * 
	 * @return ViewGroup that should be used when adding Android UI elements
	 */
	public ViewGroup getViewGroup()
	{
		return layout.getViewGroup();
	}

	/**
	 * When a GameState has a GLSurfaceView, it has some extra set up that is
	 * required before rendering the app. This method defines a place for the
	 * View to do that.
	 */
	private void startEngine()
	{
		if (!isRunning && glView != null && layout.getScene() != null)
		{
			isRunning = true;
			glView.start(layout.getScene());
		}
	}

	/**
	 * This method is called whenever a GameState Activity defines in the
	 * Manifest.xml that it wants to change it's configuration.
	 * "android:configChanges="
	 * <p>
	 * This is currently used to prevent the rotation of the screen and thus the
	 * restart of the Activity.
	 */
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		scaleScanner.onTouchEvent(event);
		return gestureScanner.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e)
	{
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY)
	{
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e)
	{

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy)
	{
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e)
	{

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e)
	{
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e)
	{
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e)
	{
		return false;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector)
	{
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector)
	{
		return true; // allows onScale() to fire by default
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector)
	{
	}
}
