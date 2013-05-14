package funativity.age.state.layout;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * This class represents a Layout for Android that provides access to a basic
 * implementation of LinearLayout.
 * 
 * @author wittem
 * 
 */
public class AGELinearLayout extends AGELayout
{

	/** The LinearLayout for this Layout */
	private LinearLayout ll;

	/** The ScrollView for this Layout */
	private ScrollView lltop;

	/**
	 * Creates a LinearLayout and wraps it with a ScrollView.
	 * 
	 * @param context
	 *            The Context that this Layout exists within.
	 */
	public AGELinearLayout(Context context)
	{
		super(context);

		lltop = new ScrollView(context);

		ll = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		ll.setLayoutParams(params);
		lltop.addView(ll);

		ll.setOrientation(LinearLayout.VERTICAL);

		topLevelView = lltop;
	}

	@Override
	public ViewGroup getViewGroup()
	{
		return ll;
	}

}
