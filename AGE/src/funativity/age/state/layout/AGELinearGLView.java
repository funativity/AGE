package funativity.age.state.layout;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import funativity.age.state.AGEGLSurfaceView;

/**
 * This class represents a Layout for Android that provides access to a
 * GLSurfaceView for OpenGL as well as a LinerLayout for adding Android UI
 * elements.
 * 
 * @author wittem
 * 
 */
public class AGELinearGLView extends AGELayout
{

	/**
	 * The FrameLayout to be used when merging the LinearLayout and
	 * GLSurfaceView
	 */
	private FrameLayout fl;

	/** The LinearLayout for this Layout */
	private LinearLayout ll;

	/**
	 * Creates and combines an instance of LinearLayout with an
	 * AGEGLSurfaceView.
	 * 
	 * @param context
	 *            The Context that this Layout exists within.
	 */
	public AGELinearGLView(Context context)
	{
		this(context, new AGEGLSurfaceView(context));
	}

	/**
	 * Creates and combines an instance of LinearLayout with an
	 * AGEGLSurfaceView.
	 * 
	 * @param context
	 *            The Context that this Layout exists within.
	 * @param glView
	 *            The GLSurfaceView associated with this View, if any
	 */
	public AGELinearGLView(Context context, AGEGLSurfaceView glView)
	{
		super(context);
		this.glView = glView;

		ll = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		ll.setLayoutParams(params);

		fl = new FrameLayout(context);
		FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		fl.setLayoutParams(params2);

		fl.addView(glView);
		fl.addView(ll);

		ll.setOrientation(LinearLayout.VERTICAL);

		topLevelView = fl;
	}

	@Override
	public ViewGroup getViewGroup()
	{
		return ll;
	}

}
