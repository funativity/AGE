package funativity.age.util;

/**
 * 
 * Quaternion rotations class
 * 
 */
public class Quaternion
{
	public float x, y, z, w;

	private Quaternion()
	{
	}

	/**
	 * Build a quaternion out of known values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public Quaternion(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * Copy a quaternion into a new one.
	 * 
	 * @param q
	 */
	public Quaternion(Quaternion q)
	{
		x = q.x;
		y = q.y;
		z = q.z;
		w = q.w;
	}

	/**
	 * Create the identity quaternion
	 * 
	 * @return
	 */
	public static Quaternion identity()
	{
		return new Quaternion(0, 0, 0, 1);
	}

	/**
	 * Normalize provided quaternion
	 * 
	 * @param quaternion
	 */
	public static void normalize(Quaternion quaternion)
	{
		float magnitude = (float) Math
				.sqrt(((quaternion.x * quaternion.x)
						+ (quaternion.y * quaternion.y)
						+ (quaternion.z * quaternion.z) + (quaternion.w * quaternion.w)));

		if (magnitude != 0)
		{
			quaternion.x /= magnitude;
			quaternion.y /= magnitude;
			quaternion.z /= magnitude;
			quaternion.w /= magnitude;
		}
	}

	/**
	 * Set provided 4x4 matrix to the values converted from the quaternion
	 * 
	 * @param matrix
	 * @param quat
	 */
	public static void setMatrixFromQuaternion(float[] matrix, Quaternion quat)
	{
		matrix[0] = 1.0f - (2.0f * ((quat.y * quat.y) + (quat.z * quat.z)));
		matrix[1] = 2.0f * ((quat.x * quat.y) - (quat.z * quat.w));
		matrix[2] = 2.0f * ((quat.x * quat.z) + (quat.y * quat.w));
		matrix[3] = 0.0f;
		matrix[4] = 2.0f * ((quat.x * quat.y) + (quat.z * quat.w));
		matrix[5] = 1.0f - (2.0f * ((quat.x * quat.x) + (quat.z * quat.z)));
		matrix[6] = 2.0f * ((quat.y * quat.z) - (quat.x * quat.w));
		matrix[7] = 0.0f;
		matrix[8] = 2.0f * ((quat.x * quat.z) - (quat.y * quat.w));
		matrix[9] = 2.0f * ((quat.y * quat.z) + (quat.x * quat.w));
		matrix[10] = 1.0f - (2.0f * ((quat.x * quat.x) + (quat.y * quat.y)));
		matrix[11] = 0.0f;
		matrix[12] = 0.0f;
		matrix[13] = 0.0f;
		matrix[14] = 0.0f;
		matrix[15] = 1.0f;
	}

	/**
	 * Create a quaternion from an axis and an angle
	 * 
	 * @param axis
	 * @param angle
	 * @return
	 */
	public static Quaternion fromAxisAndAngle(Geometry3f axis, float angle)
	{
		Quaternion quat = new Quaternion();

		angle *= 0.5f;
		axis.normalize();
		float sinAngle = (float) Math.sin(angle);
		quat.x = (axis.getX() * sinAngle);
		quat.y = (axis.getY() * sinAngle);
		quat.z = (axis.getZ() * sinAngle);
		quat.w = (float) Math.cos(angle);

		return quat;
	}

	/**
	 * Get the axis this quaternion is pointing, and the angle offset required
	 * to rebuild this quaternion
	 * 
	 * @param quat
	 * @param axis
	 * @return angle
	 */
	public static float extractAxisAndAngle(Quaternion quat, Geometry3f axis)
	{
		normalize(quat);
		float s = (float) Math.sqrt(1.0f - (quat.w * quat.w));
		if (Math.abs(s) < 0.0005f)
		{
			s = 1.0f;
		}

		if (axis != null)
		{
			axis.setX(quat.x / s);
			axis.setY(quat.y / s);
			axis.setZ(quat.z / s);
		}

		return (float) (Math.acos(quat.w) * 2.0f); // return angle as float
	}

	/**
	 * Multiply two quaternions together. Output is a new quaternion.
	 * 
	 * @param quat1
	 * @param quat2
	 * @return
	 */
	public static Quaternion multiply(Quaternion quat1, Quaternion quat2)
	{
		Geometry3f v1 = new Geometry3f(quat1.x, quat1.y, quat1.z);
		Geometry3f v2 = new Geometry3f(quat2.x, quat2.y, quat2.z);

		float angle = (quat1.w * quat2.w) - Geometry3f.dot(v1, v2);

		Geometry3f cp = Geometry3f.cross(v1, v2);

		v1.setX(v1.getX() * quat2.w);
		v1.setY(v1.getY() * quat2.w);
		v1.setZ(v1.getZ() * quat2.w);
		v2.setX(v2.getX() * quat1.w);
		v2.setY(v2.getY() * quat1.w);
		v2.setZ(v2.getZ() * quat1.w);

		return new Quaternion(v1.getX() + v2.getX() + cp.getX(), v1.getY()
				+ v2.getY() + cp.getY(), v1.getZ() + v2.getZ() + cp.getZ(),
				angle);
	}

	/**
	 * Invert provided quaternion
	 * 
	 * @param quat
	 */
	public static void invert(Quaternion quat)
	{
		float length = 1.0f / ((quat.x * quat.x) + (quat.y * quat.y)
				+ (quat.z * quat.z) + (quat.w * quat.w));
		quat.x *= -length;
		quat.y *= -length;
		quat.z *= -length;
		quat.w *= length;
	}

	/**
	 * Calculate the dot product between two quaternions
	 * 
	 * @param quat1
	 * @param quat2
	 * @return
	 */
	public static float dotProduct(Quaternion quat1, Quaternion quat2)
	{
		return quat1.x * quat2.x + quat2.y * quat2.y + quat1.z * quat2.z
				+ quat1.w * quat2.w;
	}

	/**
	 * Offset this quaternion by provided axis and angle
	 * 
	 * @param axis
	 * @param angle
	 */
	public void offsetFromAxisAndAngle(Geometry3f axis, float angle)
	{
		axis.normalize();

		axis.scale((float) Math.sin(angle / 2f));
		float scalar = (float) Math.cos(angle / 2f);

		Quaternion offset = new Quaternion(axis.getX(), axis.getY(),
				axis.getZ(), scalar);

		set(multiply(offset, this));
		normalize(this);
	}

	/**
	 * Set this quaternion to the same values as provided quaternion
	 * 
	 * @param q
	 */
	public void set(Quaternion q)
	{
		x = q.x;
		y = q.y;
		z = q.z;
		w = q.w;
	}
}
