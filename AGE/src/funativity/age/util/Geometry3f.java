package funativity.age.util;

/**
 * Represents a geometric value in 3D space (x, y, z).
 * 
 */
public class Geometry3f
{
	private float x, y, z;

	/**
	 * Creates a geometric value at the origin of 3D space.
	 */
	public Geometry3f()
	{
		super();
	}

	/**
	 * Creates a new geometric value using an existing geometric value.
	 * 
	 * @param old
	 *            existing geometric value
	 */
	public Geometry3f(Geometry3f old)
	{
		set(old);
	}

	/**
	 * Creates a geometric value where the z-axis is 0.
	 * 
	 * @param x
	 *            position on x-axis
	 * @param y
	 *            position on y-axis
	 */
	public Geometry3f(float x, float y)
	{
		set(x, y, 0);
	}

	/**
	 * Creates a geometric value.
	 * 
	 * @param x
	 *            position on x-axis
	 * @param y
	 *            position on y-axis
	 * @param z
	 *            position on z-axis
	 */
	public Geometry3f(float x, float y, float z)
	{
		set(x, y, z);
	}

	/**
	 * Updates a geometric value's x-axis, y-axis, and z-axis positions to match
	 * an existing geometric value.
	 * 
	 * @param old
	 *            existing geometric value
	 * @return this
	 */
	public Geometry3f set(Geometry3f old)
	{
		set(old.x, old.y, old.z);
		return this;
	}

	/**
	 * Updates a geometric value's x-axis and y-axis positions.
	 * 
	 * @param x
	 *            position on x-axis
	 * @param y
	 *            position on y-axis
	 * @return this
	 */
	public Geometry3f set(float x, float y)
	{
		setX(x);
		setY(y);
		return this;
	}

	/**
	 * Updates a geometric value's x-axis, y-axis, and z-axis positions.
	 * 
	 * @param x
	 *            position on x-axis
	 * @param y
	 *            position on y-axis
	 * @param z
	 *            position on z-axis
	 * @return this
	 */
	public Geometry3f set(float x, float y, float z)
	{
		setX(x);
		setY(y);
		setZ(z);
		return this;
	}

	/**
	 * Updates a geometric value's x-axis position.
	 * 
	 * @param x
	 *            position on x-axis
	 * @return this
	 */
	public Geometry3f setX(float x)
	{
		this.x = x;
		return this;
	}

	/**
	 * Updates a geometric value's y-axis position.
	 * 
	 * @param y
	 *            position on y-axis
	 * @return this
	 */
	public Geometry3f setY(float y)
	{
		this.y = y;
		return this;
	}

	/**
	 * Updates a geometric value's z-axis position.
	 * 
	 * @param z
	 *            position on z-axis
	 * @return this
	 */
	public Geometry3f setZ(float z)
	{
		this.z = z;
		return this;
	}

	/**
	 * Gets a geometric value's x-axis position.
	 * 
	 * @return position on x-axis
	 */
	public float getX()
	{
		return x;
	}

	/**
	 * Gets a geometric value's y-axis position.
	 * 
	 * @return position on y-axis
	 */
	public float getY()
	{
		return y;
	}

	/**
	 * Gets a geometric value's z-axis position.
	 * 
	 * @return position on z-axis
	 */
	public float getZ()
	{
		return z;
	}

	/**
	 * Scales a geometric value's x-axis, y-axis, and z-axis positions using a
	 * scaling factor.
	 * 
	 * @param scale
	 *            scaling factor
	 * @return this
	 */
	public Geometry3f scale(float scale)
	{
		setX(x * scale);
		setY(y * scale);
		setZ(z * scale);
		return this;
	}

	/**
	 * Translates a geometric value's x-axis, y-axis, and z-axis positions.
	 * 
	 * @param dx
	 *            change in x
	 * @param dy
	 *            change in y
	 * @param dz
	 *            change in z
	 * @return this
	 */
	public Geometry3f translate(float dx, float dy, float dz)
	{
		setX(x + dx);
		setY(y + dy);
		setZ(z + dz);
		return this;
	}

	/**
	 * Inverts a geometric value's x-axis, y-axis, and z-axis positions.
	 * 
	 * @return this
	 */
	public Geometry3f invert()
	{
		setX(-x);
		setY(-y);
		setZ(-z);
		return this;
	}

	/**
	 * Normalizes a geometric value's x-axis, y-axis, and z-axis positions. This
	 * causes each position value to be no greater than 1 and no less than -1.
	 * 
	 * @return this
	 */
	public Geometry3f normalize()
	{
		float length = length();
		if (length != 0)
		{
			set(x / length, y / length, z / length);
		}
		return this;
	}

	/**
	 * Finds the shortest angle between two geometric values from the origin.
	 * 
	 * @param left
	 *            first geometric value
	 * @param right
	 *            second geometric value
	 * @return angle in radians
	 */
	public static float angle(Geometry3f left, Geometry3f right)
	{
		// Division by 0 check.
		float lengthProduct = left.length() * right.length();
		if (lengthProduct == 0)
		{
			return 0;
		}
		else
		{
			// Hypotenuse / opposite.
			float ratio = dot(left, right) / lengthProduct;

			// Boundary checks.
			if (ratio < -1)
			{
				ratio = -1;
			}
			else if (ratio > 1)
			{
				ratio = 1;
			}

			return (float) Math.acos(ratio);
		}
	}

	/**
	 * Calculates the length from the origin to this geometric value.
	 * 
	 * @return this geometric value's length
	 */
	public float length()
	{
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Calculates the distance between two geometric values using their x-axis,
	 * y-axis, and z-axis positions.
	 * 
	 * @param left
	 *            first geometric value
	 * @param right
	 *            second geometric value
	 * @return distance between two geometric values
	 */
	public static float distance(Geometry3f left, Geometry3f right)
	{
		float dx = left.x - right.x;
		float dy = left.y - right.y;
		float dz = left.z - right.z;
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * Calculates the dot product of two geometric values.
	 * 
	 * @param left
	 *            first geometric value
	 * @param right
	 *            second geometric value
	 * @return dot product of two geometric values
	 */
	public static float dot(Geometry3f left, Geometry3f right)
	{
		return left.x * right.x + left.y * right.y + left.z * right.z;
	}

	/**
	 * Creates a new geometric value by adding two geometric values.
	 * 
	 * @param left
	 *            first geometric value
	 * @param right
	 *            second geometric value
	 * @return a new geometric value
	 */
	public static Geometry3f add(Geometry3f left, Geometry3f right)
	{
		float x = left.x + right.x;
		float y = left.y + right.y;
		float z = left.z + right.z;
		return new Geometry3f(x, y, z);
	}

	/**
	 * Creates a new geometric value by subtracting two geometric values.
	 * 
	 * @param left
	 *            first geometric value
	 * @param right
	 *            second geometric value
	 * @return a new geometric value
	 */
	public static Geometry3f sub(Geometry3f left, Geometry3f right)
	{
		float x = left.x - right.x;
		float y = left.y - right.y;
		float z = left.z - right.z;
		return new Geometry3f(x, y, z);
	}

	/**
	 * Creates a new geometric value with the cross product of two geometric
	 * values.
	 * 
	 * @param left
	 *            first geometric value
	 * @param right
	 *            second geometric value
	 * @return a new geometric value
	 */
	public static Geometry3f cross(Geometry3f left, Geometry3f right)
	{
		float x = left.y * right.z - left.z * right.y;
		float y = right.x * left.z - right.z * left.x;
		float z = left.x * right.y - left.y * right.x;
		return new Geometry3f(x, y, z);
	}

	/**
	 * Creates a readable string for this geometric value of the format
	 * "[x, y, z]".
	 * 
	 * @return string representing this geometric value
	 */
	public String toString()
	{
		return "[" + x + ", " + y + ", " + z + "]";
	}
}