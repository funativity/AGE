package funativity.age.state.layout;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * This class represents a Layout for Android that provides access to a basic
 * implementation of RelativeLayout.
 * 
 * @author wittem
 * 
 */
public class AGERelativeLayout extends AGELayout
{

	/** The RelativeLayout for this Layout */
	private RelativeLayout rl;

	/** The ScrollView for this Layout */
	private ScrollView lltop;

	/**
	 * Creates a RelativeLayout and wraps it with a ScrollView.
	 * 
	 * @param context
	 *            The Context that this Layout exists within.
	 */
	public AGERelativeLayout(Context context)
	{
		super(context);

		lltop = new ScrollView(context);

		rl = new RelativeLayout(context);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		rl.setLayoutParams(params);
		lltop.addView(rl);

		rl.setGravity(RelativeLayout.CENTER_HORIZONTAL);

		topLevelView = lltop;
	}

	@Override
	public ViewGroup getViewGroup()
	{
		return rl;
	}

}
