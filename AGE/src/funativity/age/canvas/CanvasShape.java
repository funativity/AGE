package funativity.age.canvas;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;

/**
 * Represents a shape drawn with the Android graphics library.
 * 
 */
public class CanvasShape extends ShapeDrawable
{
	// #region Enums
	/**
	 * Supported shape types.
	 * 
	 * @see Type.RECTANGLE
	 * @see Type.OVAL
	 */
	public enum Type
	{
		RECTANGLE, ELLIPSE
	}

	// #endregion

	// #region Fields
	/**
	 * The shape's type.
	 * 
	 * @see Type
	 */
	private Type type;

	/**
	 * The shape's current position.
	 * 
	 * @see Position
	 */
	private Position position;

	// #endregion

	// #region Constructors
	/**
	 * Creates a shape using rectangular bounds.
	 * 
	 * @param type
	 *            shape type
	 * @param position
	 *            rectangular bounds
	 * @see Position
	 */
	public CanvasShape(Type type, Position position)
	{
		setType(type);
		setPosition(position);
	}

	/**
	 * Creates a shape positioned in the center.
	 * 
	 * @param type
	 *            shape type
	 * @param width
	 *            width in pixels
	 * @param height
	 *            height in pixels
	 */
	public CanvasShape(Type type, int width, int height)
	{
		this(type, Position.getCenterPosition(width, height));
	}

	/**
	 * Creates a shape positioned in the center as a scale of the screen size.
	 * The smallest a shape can ever scale to is 1 x 1 pixels.
	 * 
	 * @param type
	 *            shape type
	 * @param widthScale
	 *            width scaling factor, 0.5f will occupy half the screen width
	 * @param heightScale
	 *            height scaling factor, 0.5f will occupy half the screen height
	 * @see Screen
	 */
	public CanvasShape(Type type, float widthScale, float heightScale)
	{
		this(type, Position.getCenterPosition(1, 1));

		// Scale with respect to the screen size.
		scaleToScreen(widthScale, heightScale);
	}

	// #endregion

	// #region Getters and setters
	/**
	 * Gets a shape's type.
	 * 
	 * @return shape's type
	 * @see Type
	 */
	public Type getType()
	{
		return type;
	}

	/**
	 * Sets a shape's type.
	 * 
	 * @param type
	 * @see Type
	 */
	public void setType(Type type)
	{
		if (type == Type.RECTANGLE)
		{
			setShape(new RectShape());
		}
		else if (type == Type.ELLIPSE)
		{
			setShape(new OvalShape());
		}

		this.type = type;
	}

	/**
	 * Gets the shape's rectangular bounds.
	 * 
	 * @return shape's rectangular bounds
	 * @see Position
	 */
	public Position getPosition()
	{
		return position;
	}

	/**
	 * Sets a shape's location using rectangular bounds.
	 * 
	 * @param position
	 *            rectangular bounds
	 * @throws IllegalArgumentException
	 *             if position is null
	 * @see Position
	 */
	public void setPosition(Position position)
	{
		// Check the variables.
		if (position == null)
		{
			throw new IllegalArgumentException("Position can't be null.");
		}

		this.position = position;

		// Update the bounds using the shape's position.
		setBounds(position);
	}

	/**
	 * Shortcut for setting rectangular bounds on the native shape.
	 * 
	 * @param position
	 *            rectangular bounds
	 * @see Position
	 */
	private void setBounds(Position position)
	{
		super.setBounds(position.getLeft(), position.getTop(),
				position.getRight(), position.getBottom());
	}

	/**
	 * Gets the shape's width in pixels.
	 * 
	 * @return width in pixels
	 */
	public int getWidth()
	{
		return position.getWidth();
	}

	/**
	 * Updates a shape's size by scaling horizontally. The smallest a shape can
	 * ever horizontally scale to is 1 pixel.
	 * 
	 * @param width
	 *            new width in pixels
	 */
	public void setWidth(int width)
	{
		// Check the variables.
		if (width <= 0)
		{
			width = 1;
		}

		// Update the shape's position.
		position.setWidth(width);

		// Update the bounds using the shape's position.
		setBounds(position);
	}

	/**
	 * Gets the shape's height in pixels.
	 * 
	 * @return height in pixels
	 */
	public int getHeight()
	{
		return position.getHeight();
	}

	/**
	 * Updates a shape's size by scaling vertically. The smallest a shape can
	 * ever vertically scale to is 1 pixel.
	 * 
	 * @param height
	 *            new height in pixels
	 */
	public void setHeight(int height)
	{
		// Check the variables.
		if (height <= 0)
		{
			height = 1;
		}

		// Update the shape's position.
		position.setHeight(height);

		// Update the bounds using the shape's position.
		setBounds(position);
	}

	/**
	 * Updates a shape's size by scaling both horizontally and vertically. The
	 * smallest a shape can ever scale to is 1 x 1 pixels.
	 * 
	 * @param width
	 *            new width in pixels
	 * @param height
	 *            new height in pixels
	 */
	public void setSize(int width, int height)
	{
		setWidth(width);
		setHeight(height);
	}

	// #endregion

	// #region Transforms
	/**
	 * Scales a shape equally using width and height scaling factors. The
	 * smallest a shape can ever scale to is 1 x 1 pixels.
	 * 
	 * @param widthScale
	 *            width scaling factor, 1.0f will give no change
	 * @param heightScale
	 *            height scaling factor, 1.0f will give no change
	 */
	public void scale(float widthScale, float heightScale)
	{
		// Get the old shape width and height.
		int shapeWidth = getWidth();
		int shapeHeight = getHeight();

		// Update the shape size references.
		shapeWidth = (int) (shapeWidth * widthScale);
		shapeHeight = (int) (shapeHeight * heightScale);

		// Update the shape with the new size.
		setSize(shapeWidth, shapeHeight);
	}

	/**
	 * Scales a shape equally on a screen using width and height scaling
	 * factors. The smallest a shape can ever scale to is 1 x 1 pixels.
	 * 
	 * @param widthScale
	 *            width scaling factor, 0.5f will occupy half the screen width
	 * @param heightScale
	 *            height scaling factor, 0.5f will occupy half the screen height
	 * @see Screen
	 */
	public void scaleToScreen(float widthScale, float heightScale)
	{
		// Get the screen size.
		int screenWidth = Screen.getScreenWidth();
		int screenHeight = Screen.getScreenHeight();

		// Set the shape's size with respect to screen size.
		int shapeWidth = (int) (screenWidth * widthScale);
		int shapeHeight = (int) (screenHeight * heightScale);

		// Update the shape with the new size.
		setSize(shapeWidth, shapeHeight);
	}

	// #endregion
}