package funativity.age.canvas;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/**
 * Determines and uses the size of a screen for scaling across multiple screen
 * sizes.
 * 
 */
public class Screen
{
	// #region Fields
	/**
	 * Screen width in pixels.
	 */
	private static int screenWidth;

	/**
	 * Screen height in pixels.
	 */
	private static int screenHeight;

	// #endregion

	// #region Getters and setters
	/**
	 * Gets the last set or calculated screen width. This may not be the correct
	 * screen width if it has not been set or calculated with the current
	 * screen.
	 * 
	 * @return screen width in pixels
	 * @throws IllegalStateException
	 *             if screen size has not been set
	 */
	public static int getScreenWidth()
	{
		// Check the variables.
		if (screenWidth == 0)
		{
			throw new IllegalStateException("Screen size has not been set.");
		}

		return screenWidth;
	}

	/**
	 * Sets the screen width without the need for a context.
	 * 
	 * @param screenWidth
	 *            screen width in pixels
	 * @throws IllegalStateException
	 *             if screen width is not greater than 0
	 */
	public static void setScreenWidth(int screenWidth)
	{
		// Check the variables.
		if (screenWidth <= 0)
		{
			throw new IllegalArgumentException(
					"Screen width must be greater than 0.");
		}

		Screen.screenWidth = screenWidth;
	}

	/**
	 * Gets the last set or calculated screen height. This may not be the
	 * correct screen height if it has not been set or calculated with the
	 * current screen.
	 * 
	 * @return screen height in pixels
	 * @throws IllegalStateException
	 *             if screen size has not been set
	 */
	public static int getScreenHeight()
	{
		// Check the variables.
		if (screenHeight == 0)
		{
			throw new IllegalStateException("Screen size has not been set.");
		}

		return screenHeight;
	}

	/**
	 * Sets the screen height without the need for a context.
	 * 
	 * @param screenHeight
	 *            screen height in pixels
	 * 
	 * @throws IllegalArgumentException
	 *             if screen height is not greater than 0
	 */
	public static void setScreenHeight(int screenHeight)
	{
		// Check the variables.
		if (screenHeight <= 0)
		{
			throw new IllegalArgumentException(
					"Screen height must be greater than 0.");
		}

		Screen.screenHeight = screenHeight;
	}

	// #endregion

	// #region Calculation
	/**
	 * Calculates and sets the screen size using the provided context. This
	 * needs to be called before the screen size is used, and called again
	 * whenever the screen size is changed in order to stay 'correct'.
	 * 
	 * @param context
	 *            to calculate size on
	 * @throws IllegalArgumentException
	 *             if context is null
	 */
	// For Eclipse warning.
	@SuppressWarnings("deprecation")
	// For Android's compiler.
	@TargetApi(13)
	public static void calculateScreenSize(Context context)
	{
		// Check the variables.
		if (context == null)
		{
			throw new IllegalArgumentException("Context must not be null.");
		}

		// Get the relevant area.
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		// Determine the screen size.
		if (Build.VERSION.SDK_INT >= 13)
		{
			Point size = new Point();
			display.getSize(size);
			setScreenWidth(size.x);
			setScreenHeight(size.y);
		}
		else
		{
			setScreenWidth(display.getWidth());
			setScreenHeight(display.getHeight());
		}
	}

	// #endregion
}