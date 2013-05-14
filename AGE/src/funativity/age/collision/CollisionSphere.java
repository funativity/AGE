package funativity.age.collision;

import funativity.age.opengl.Entity;
import funativity.age.util.Geometry3f;

/**
 * A perfect sphere used as a collision boundary.
 * 
 */
public class CollisionSphere extends CollisionShape
{
	private float radius;

	/**
	 * Creates a spherical collision boundary.
	 * 
	 * @param entity
	 *            mesh container
	 * @param listener
	 *            event handler for collisions
	 * @param radius
	 *            distance
	 */
	public CollisionSphere(Entity entity, CollisionListener listener,
			float radius)
	{
		this(entity, listener, new Geometry3f(), radius);
	}

	/**
	 * Creates a spherical collision boundary.
	 * 
	 * @param entity
	 *            mesh container
	 * @param listener
	 *            event handler for collisions
	 * @param offset
	 *            distance away from the entity's position
	 * @param radius
	 *            distance
	 */
	public CollisionSphere(Entity entity, CollisionListener listener,
			Geometry3f offset, float radius)
	{
		super(entity, listener, offset);
		setRadius(radius);
	}

	/**
	 * Gets the radius of this CollisionSphere
	 * 
	 * @return distance
	 */
	public float getRadius()
	{
		return radius;
	}

	/**
	 * Sets the radius of this CollisionSphere
	 * 
	 * @param radius
	 *            distance
	 */
	public void setRadius(float radius)
	{
		this.radius = radius;
	}

	@Override
	public boolean isIntersect(CollisionShape other)
	{
		if (getClass() == other.getClass())
		{
			// Same shape collision.
			CollisionSphere that = (CollisionSphere) other;
			return CollisionDetect.isIntersectSpheres(this, that);
		}
		else
		{
			// Assume the other shape handles it.
			return other.isIntersect(this);
		}
	}
}