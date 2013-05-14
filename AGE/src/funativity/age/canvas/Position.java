package funativity.age.canvas;

/**
 * Represents an area bounded by a rectangle.
 * 
 */
public class Position
{
	// #region Fields
	/**
	 * Left pixel boundary.
	 */
	private int left;

	/**
	 * Top pixel boundary.
	 */
	private int top;

	/**
	 * Right pixel boundary.
	 */
	private int right;

	/**
	 * Bottom pixel boundary.
	 */
	private int bottom;

	// #endregion

	// #region Constructors
	/**
	 * Creates a position bounded as a rectangle.
	 * 
	 * @param left
	 *            left pixel boundary
	 * @param top
	 *            top pixel boundary
	 * @param right
	 *            right pixel boundary
	 * @param bottom
	 *            bottom pixel boundary
	 */
	public Position(int left, int top, int right, int bottom)
	{
		setAll(left, top, right, bottom);
	}

	// #endregion

	// #region Position getters and setters
	/**
	 * Gets the left pixel boundary.
	 * 
	 * @return left pixel boundary
	 */
	public int getLeft()
	{
		return left;
	}

	/**
	 * Gets the top pixel boundary.
	 * 
	 * @return top pixel boundary
	 */
	public int getTop()
	{
		return top;
	}

	/**
	 * Gets the right pixel boundary.
	 * 
	 * @return right pixel boundary
	 */
	public int getRight()
	{
		return right;
	}

	/**
	 * Gets the bottom pixel boundary.
	 * 
	 * @return bottom pixel boundary
	 */
	public int getBottom()
	{
		return bottom;
	}

	/**
	 * Sets the left and right pixel boundaries, essentially adjusting the
	 * width.
	 * 
	 * @param left
	 *            left pixel boundary
	 * @param right
	 *            right pixel boundary
	 * @throws IllegalArgumentException
	 *             if left is not less than right
	 */
	public void setLeftRight(int left, int right)
	{
		// Check the variables.
		if (left >= right)
		{
			throw new IllegalArgumentException("Left must be less than right.");
		}

		this.left = left;
		this.right = right;
	}

	/**
	 * Sets the top and bottom pixel boundaries, essentially adjusting the
	 * height.
	 * 
	 * @param top
	 *            top pixel boundary
	 * @param bottom
	 *            bottom pixel boundary
	 * @throws IllegalArgumentException
	 *             if top is not less than bottom
	 */
	public void setTopBottom(int top, int bottom)
	{
		// Check the variables.
		if (top >= bottom)
		{
			throw new IllegalArgumentException("Top must be less than bottom.");
		}

		this.top = top;
		this.bottom = bottom;
	}

	/**
	 * Sets all pixel boundaries at once.
	 * 
	 * @param left
	 *            left pixel boundary
	 * @param top
	 *            top pixel boundary
	 * @param right
	 *            right pixel boundary
	 * @param bottom
	 *            bottom pixel boundary
	 * @throws IllegalArgumentException
	 *             if left is not less than right
	 * @throws IllegalArgumentException
	 *             if top is not less than bottom
	 */
	public void setAll(int left, int top, int right, int bottom)
	{
		setLeftRight(left, right);
		setTopBottom(top, bottom);
	}

	// #endregion

	// #region Size getters and setters
	/**
	 * Gets the width.
	 * 
	 * @return width in pixels
	 */
	public int getWidth()
	{
		return getRight() - getLeft();
	}

	/**
	 * Sets the width by adjusting the left and right bounds evenly.
	 * 
	 * @param width
	 *            in pixels
	 * @throws IllegalArgumentException
	 *             if width is not greater than 0
	 */
	public void setWidth(int width)
	{
		// Check the variables.
		if (width <= 0)
		{
			throw new IllegalArgumentException("Width must be greater than 0.");
		}

		// Split the width.
		int[] pixelSplit = Position.calculateBounds(getLeft(), getRight(),
				width);
		int left = pixelSplit[0];
		int right = pixelSplit[1];

		// Set the new position.
		setLeftRight(left, right);
	}

	/**
	 * Gets the height.
	 * 
	 * @return height in pixels
	 */
	public int getHeight()
	{
		return getBottom() - getTop();
	}

	/**
	 * Sets the height by adjusting the top and bottom bounds evenly.
	 * 
	 * @param height
	 *            in pixels
	 * @throws IllegalArgumentException
	 *             if height is not greater than 0
	 */
	public void setHeight(int height)
	{
		// Check the variables.
		if (height <= 0)
		{
			throw new IllegalArgumentException("Height must be greater than 0.");
		}

		// Split the height.
		int[] pixelSplit = Position.calculateBounds(getTop(), getBottom(),
				height);
		int top = pixelSplit[0];
		int bottom = pixelSplit[1];

		// Set the new position.
		setTopBottom(top, bottom);
	}

	// #endregion

	// #region Specific positions
	/**
	 * Uses an object's width and height to determine a central position on a
	 * screen size.
	 * 
	 * @param width
	 *            object in pixels
	 * @param height
	 *            object in pixels
	 * @return a Position representing the center of the screen
	 * @throws IllegalArgumentException
	 *             if width is not greater than 0
	 * @throws IllegalArgumentException
	 *             if height is not greater than 0
	 * @see Screen
	 */
	public static Position getCenterPosition(int width, int height)
	{
		// Check the variables.
		if (width <= 0)
		{
			throw new IllegalArgumentException("Width must be greater than 0.");
		}
		if (height <= 0)
		{
			throw new IllegalArgumentException("Height must be greater than 0.");
		}

		// Calculate numbers to help with centering, ignoring any extra pixel.
		int middleX = Screen.getScreenWidth() / 2;
		int middleY = Screen.getScreenHeight() / 2;
		int halfWidth = width / 2;
		int halfHeight = height / 2;

		// Calculate the center position.
		int left = middleX - halfWidth;
		int top = middleY - halfHeight;
		int right = middleX + halfWidth;
		int bottom = middleY + halfHeight;

		// Deal with the extra pixel.
		if (width % 2 == 1)
		{
			left--;
		}
		if (height % 2 == 1)
		{
			top--;
		}

		// Return a central position.
		return new Position(left, top, right, bottom);
	}

	// #endregion

	// #region Pixel math
	/**
	 * Scales two pixel bounds evenly according to a new distance.
	 * 
	 * @param start
	 *            starting bound in pixels
	 * @param end
	 *            ending bound in pixels
	 * @param distance
	 *            new distance between the start and end bounds
	 * @return two integers defining the new pixel bounds, starting with the
	 *         smallest
	 * @throws IllegalArgumentException
	 *             if start is not less than end
	 * @throws IllegalArgumentException
	 *             if distance is not greater than 0
	 */
	public static int[] calculateBounds(int start, int end, int distance)
	{
		// Check the variables.
		if (start >= end)
		{
			throw new IllegalArgumentException("Start must be less than end.");
		}
		if (distance <= 0)
		{
			throw new IllegalArgumentException(
					"Distance must be greater than 0.");
		}

		// Determine the change in total.
		int currentTotal = end - start;
		int totalChange = distance - currentTotal;

		// Ignore the possible extra pixel.
		int halfChange = totalChange / 2;

		// Split the change equally between the first and second position.
		int first = start - halfChange;
		int second = end + halfChange;

		// If needed, deal with the extra pixel.
		if (totalChange % 2 == 1)
		{
			// Can use either first or second here,
			// but the sign needs to be reversed if changed.
			if (totalChange < 0)
			{
				second--;
			}
			else
			{
				second++;
			}
		}

		// Create and return the result.
		int[] result = { first, second };
		return result;
	}

	// #endregion
}