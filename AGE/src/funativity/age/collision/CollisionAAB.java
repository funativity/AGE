package funativity.age.collision;

import funativity.age.opengl.Entity;
import funativity.age.util.Geometry3f;

/**
 * 
 * Axis Aligned Bounding Box used as a collision boundary.
 * 
 */
public class CollisionAAB extends CollisionShape
{
	// dimensions of this AAB
	private float width, height;

	/**
	 * Create an axis aligned box for the specified entity using specified
	 * collision listener. Defaults the size of this box to (1, 1) and uses (0,
	 * 0) for the offset
	 * 
	 * @param entity
	 *            Entity that represents this collision object
	 * @param listener
	 *            Listener that responds to the collisions
	 */
	public CollisionAAB(Entity entity, CollisionListener listener)
	{
		this(entity, listener, new Geometry3f(), 1, 1);
	}

	/**
	 * Create an axis aligned box for specified entity using specified collision
	 * listener. The size of this box is specified by width/height and uses (0,
	 * 0) for the offset
	 * 
	 * @param entity
	 *            Entity that represents this collision object
	 * @param listener
	 *            Listener that responds to the collisions
	 * @param width
	 *            Width of this collision object
	 * @param height
	 *            Height of this collision object
	 */
	public CollisionAAB(Entity entity, CollisionListener listener, float width,
			float height)
	{
		this(entity, listener, new Geometry3f(), width, height);
	}

	/**
	 * Create an axis aligned box for specified entity using specified collision
	 * listener. Defaults the size of this box to (1, 1) and uses provided
	 * offset for the offset
	 * 
	 * @param entity
	 *            Entity that represents this collision object
	 * @param listener
	 *            Listener that responds to the collisions
	 * @param offset
	 *            Distance from the origin (of provided entity) this collision
	 *            shape looks for collisions
	 */
	public CollisionAAB(Entity entity, CollisionListener listener,
			Geometry3f offset)
	{
		this(entity, listener, offset, 1, 1);
	}

	/**
	 * Create an axis aligned box for specified entity using specified collision
	 * listener. The size of this box is specified by width/height and uses
	 * provided offset for the offset
	 * 
	 * @param entity
	 *            Entity that represents this collision object
	 * @param listener
	 *            Listener that responds to the collisions
	 * @param offset
	 *            Distance from the origin (of provided entity) this collision
	 *            shape looks for collisions
	 * @param width
	 *            Width of this collision object
	 * @param height
	 *            Height of this collision object
	 */
	public CollisionAAB(Entity entity, CollisionListener listener,
			Geometry3f offset, float width, float height)
	{
		super(entity, listener, offset);
		this.width = width;
		this.height = height;
	}

	/**
	 * Get the width of this box
	 * 
	 * @return width of this box
	 */
	public float getWidth()
	{
		return width;
	}

	/**
	 * Set the width of this box
	 * 
	 * @param width
	 *            new width of this box
	 */
	public void setWidth(float width)
	{
		this.width = width;
	}

	/**
	 * Get the height of this box
	 * 
	 * @return height of this box
	 */
	public float getHeight()
	{
		return height;
	}

	/**
	 * Set the height of this box
	 * 
	 * @param height
	 *            new height of this box
	 */
	public void setHeight(float height)
	{
		this.height = height;
	}

	@Override
	public boolean isIntersect(CollisionShape other)
	{
		// Check if this class knows how to handle the other shape
		if (other instanceof CollisionAAB)
		{
			return CollisionDetect.isIntersectAAB(this, (CollisionAAB) other);
		}
		else if (other instanceof CollisionSphere)
		{
			return CollisionDetect.isIntersectAABToSphere(this,
					(CollisionSphere) other);
		}
		else
		{
			// If this class does not know how to handle the shape, see if the
			// other shape knows how to handle this shape.
			return other.isIntersect(this);
		}
	}
}
