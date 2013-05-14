package funativity.age.opengl;

/**
 * Simple color class that is intended to have all values between 0 and 1.
 * 
 * @author riedla
 * 
 */
public class AGEColor
{
	/**
	 * Index to each part of the color
	 */
	private static final int r = 0, g = 1, b = 2, a = 3;

	/**
	 * Hold the color values
	 */
	private final float[] color = new float[4];

	/**
	 * Create a color that represents white
	 */
	public AGEColor()
	{
		this(1, 1, 1, 1);
	}

	/**
	 * Mimic given color.
	 * 
	 * @param color
	 */
	public AGEColor(AGEColor color)
	{
		this(color.getRed(), color.getGreen(), color.getBlue(), color
				.getAlpha());
	}

	/**
	 * Create a color that uses the given float value for all parts of this
	 * color (except alpha, which is set to 1)
	 * 
	 * @param color
	 */
	public AGEColor(float color)
	{
		this(color, color, color);
	}

	/**
	 * Create a specific color using the proved values (alpha is set to 1) all
	 * values need to be between 0 and 1.
	 * 
	 * @param r
	 *            red [0, 1]
	 * @param g
	 *            green [0, 1]
	 * @param b
	 *            blue [0, 1]
	 */
	public AGEColor(float r, float g, float b)
	{
		this(r, g, b, 1);
	}

	/**
	 * Create a color using the proved values. All values need to be between 0
	 * and 1.
	 * 
	 * @param r
	 *            red [0, 1]
	 * @param g
	 *            green [0, 1]
	 * @param b
	 *            blue [0, 1]
	 * @param alpha
	 *            alhpa [0, 1]
	 */
	public AGEColor(float red, float green, float blue, float alpha)
	{
		color[r] = red;
		color[g] = green;
		color[b] = blue;
		color[a] = alpha;
	}

	/**
	 * Get the alpha value for this color
	 * 
	 * @return Returns the alpha of this color
	 */
	public float getAlpha()
	{
		return color[a];
	}

	/**
	 * Get the blue value for this color
	 * 
	 * @return returns the blue value of this color
	 */
	public float getBlue()
	{
		return color[b];
	}

	/**
	 * Get the green value for this color
	 * 
	 * @return returns the green value of this color
	 */
	public float getGreen()
	{
		return color[g];
	}

	/**
	 * Get the red value for this color
	 * 
	 * @return returns the red value of this color
	 */
	public float getRed()
	{
		return color[r];
	}

	/**
	 * Get the alpha value for this color
	 * 
	 * @return returns the alpha value of this color
	 */
	public float getA()
	{
		return color[a];
	}

	/**
	 * Get the blue value for this color
	 * 
	 * @return returns the blue value of this color
	 */
	public float getB()
	{
		return color[b];
	}

	/**
	 * Get the green value for this color
	 * 
	 * @return returns the green value of this color
	 */
	public float getG()
	{
		return color[g];
	}

	/**
	 * Get the red value for this color
	 * 
	 * @return returns the red value of this color
	 */
	public float getR()
	{
		return color[r];
	}

	/**
	 * Set the red value for this color
	 * 
	 * @param r
	 *            new red value
	 */
	public void setR(float r)
	{
		color[AGEColor.r] = r;
	}

	/**
	 * Set the green value for this color
	 * 
	 * @param g
	 *            new green value
	 */
	public void setG(float g)
	{
		color[AGEColor.g] = g;
	}

	/**
	 * Set the blue value for this color
	 * 
	 * @param b
	 *            new blue value
	 */
	public void setB(float b)
	{
		color[AGEColor.b] = b;
	}

	/**
	 * Set the alpha value for this color
	 * 
	 * @param a
	 *            new alpha value
	 */
	public void setA(float a)
	{
		color[AGEColor.a] = a;
	}

	/**
	 * Get the backing array of values for this color. Any changes to this array
	 * will affect this color
	 * 
	 * @return
	 */
	public float[] getArray()
	{
		return color;
	}
}
