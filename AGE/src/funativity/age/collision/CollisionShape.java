package funativity.age.collision;

import java.util.List;

import funativity.age.opengl.Entity;
import funativity.age.util.Geometry3f;

/**
 * A shape used as a collision boundary.
 * 
 */
public abstract class CollisionShape
{
	// Constructor.
	private Entity entity;
	private CollisionListener listener;
	private Geometry3f offset;

	// Non-constructor.
	private CollisionManager manager;
	private List<CollisionShape> children;

	/**
	 * Creates a collision boundary.
	 * 
	 * @param entity
	 *            mesh container
	 * @param listener
	 *            event handler for collisions
	 */
	public CollisionShape(Entity entity, CollisionListener listener)
	{
		this(entity, listener, new Geometry3f());
	}

	/**
	 * Creates a collision boundary.
	 * 
	 * @param entity
	 *            mesh container
	 * @param listener
	 *            event handler for collisions
	 * @param offset
	 *            distance away from the entity's position
	 */
	public CollisionShape(Entity entity, CollisionListener listener,
			Geometry3f offset)
	{
		this(entity, listener, offset, null);
	}

	/**
	 * Creates a collision boundary.
	 * 
	 * @param entity
	 *            mesh container
	 * @param listener
	 *            event handler for collisions
	 * @param offset
	 *            distance away from the entity's position
	 * @param children
	 *            list of CollisionShapes to test collisions on when a collision
	 *            is detected on this shape
	 */
	public CollisionShape(Entity entity, CollisionListener listener,
			Geometry3f offset, List<CollisionShape> children)
	{
		set(entity, listener, offset, children);
	}

	/**
	 * 
	 * @param entity
	 *            mesh container
	 * @param listener
	 *            event handler for collisions
	 * @param offset
	 *            distance away from the entity's position
	 * @param children
	 *            list of CollisionShapes to test collisions on when a collision
	 *            is detected on this shape
	 * @return this
	 */
	public CollisionShape set(Entity entity, CollisionListener listener,
			Geometry3f offset, List<CollisionShape> children)
	{
		setEntity(entity);
		setListener(listener);
		setOffset(offset);
		setChildren(children);
		return this;
	}

	/**
	 * Gets the Entity using this CollisionShape
	 * 
	 * @return mesh container
	 */
	public Entity getEntity()
	{
		return entity;
	}

	/**
	 * Sets the Entity using this CollisionShape
	 * 
	 * @param entity
	 *            mesh container
	 * @return this
	 */
	public CollisionShape setEntity(Entity entity)
	{
		this.entity = entity;
		return this;
	}

	/**
	 * Gets the CollisionListener of this CollisionShape
	 * 
	 * @return event handler for collisions
	 */
	public CollisionListener getListener()
	{
		return listener;
	}

	/**
	 * Sets the CollisionListener for this CollisionShape
	 * 
	 * @param listener
	 *            event handler for collisions
	 * @return this
	 */
	public CollisionShape setListener(CollisionListener listener)
	{
		this.listener = listener;
		return this;
	}

	/**
	 * Gets the 3f offset of this CollisionShape
	 * 
	 * @return distance away from the entity's position
	 */
	public Geometry3f getOffset()
	{
		return offset;
	}

	/**
	 * Sets the 3f offset for this CollisionShape
	 * 
	 * @param offset
	 *            distance away from the entity's position
	 * @return this
	 */
	public CollisionShape setOffset(Geometry3f offset)
	{
		this.offset = offset;
		return this;
	}

	/**
	 * Gets the CollisionManager of this CollisionShape
	 * 
	 * @return CollisionManager using this CollisionShape
	 */
	public CollisionManager getManager()
	{
		return manager;
	}

	/**
	 * Sets the CollisionManager for this CollisionShape
	 * 
	 * @param manager
	 *            CollisionManager using this CollisionShape
	 * @return this
	 */
	public CollisionShape setManager(CollisionManager manager)
	{
		this.manager = manager;
		return this;
	}

	/**
	 * Gets the children CollisionShapes of this CollisionShape
	 * 
	 * @return list of CollisionShapes to test collisions on when a collision is
	 *         detected on this shape
	 */
	public List<CollisionShape> getChildren()
	{
		return children;
	}

	/**
	 * Sets the children CollisionShapes of this CollisionShape
	 * 
	 * @param children
	 *            list of CollisionShapes to test collisions on when a collision
	 *            is detected on this shape
	 * @return this
	 */
	public CollisionShape setChildren(List<CollisionShape> children)
	{
		this.children = children;
		return this;
	}

	/**
	 * Calculates the distance between two shape centers, including any offsets.
	 * 
	 * @param shape1
	 *            first collision boundary
	 * @param shape2
	 *            second collision boundary
	 * @return
	 * 		  Distance between the shape centers
	 */
	public static float getDistance(CollisionShape shape1, CollisionShape shape2)
	{
		Geometry3f position1 = shape1.getEntity().getPosition();
		Geometry3f position2 = shape2.getEntity().getPosition();
		Geometry3f offset1 = shape1.getOffset();
		Geometry3f offset2 = shape2.getOffset();
		Geometry3f adjusted1 = Geometry3f.add(position1, offset1);
		Geometry3f adjusted2 = Geometry3f.add(position2, offset2);
		float distance = Geometry3f.distance(adjusted1, adjusted2);
		return distance;
	}

	/**
	 * Determines whether two collision boundaries are overlapping.
	 * 
	 * @param other
	 *            second collision boundary
	 * @return true if the collision boundaries are compatible and intersecting,
	 *         false otherwise
	 */
	public abstract boolean isIntersect(CollisionShape other);

	/**
	 * Determines whether two collision boundaries will overlap in the next
	 * frame. This is done by dividing the delta by a number of segments to
	 * determine the amount of positions to check, and incrementally checking
	 * each of these positions for an intersection.
	 * 
	 * @param other
	 *            second collision boundary
	 * @param delta
	 *            time since last update in seconds
	 * @param segments
	 *            amount of increments to check between these two frames;
	 *            setting to 1 will only check the next frame
	 * @return true immediately (stops looking) on intersection, false otherwise
	 */
	public boolean willIntersect(CollisionShape other, float delta, int segments)
	{
		// Arbitrary argument check for division by 0.
		if (segments <= 0)
		{
			segments = 1;
		}

		// Determine the new increment.
		float subDelta = delta / (float) segments;

		// Clone the entities, just caring about the physics.
		Entity old1 = getEntity();
		Entity old2 = other.getEntity();
		Entity new1 = new Entity(null, old1);
		Entity new2 = new Entity(null, old2);
		setEntity(new1);
		other.setEntity(new2);

		// Simulate the in-between frames with the clones.
		boolean willIntersect = false;
		for (int frame = 1; frame <= segments; frame++)
		{
			// Update each entity's physics.
			new1.update(null, subDelta);
			new2.update(null, subDelta);

			// Check each new frame.
			if (isIntersect(other))
			{
				// No point looking further.
				willIntersect = true;
				break;
			}
		}

		// Reset the entities to their old reference.
		setEntity(old1);
		other.setEntity(old2);

		return willIntersect;
	}

	/**
	 * Triggers the collision event handler.
	 * 
	 * @param other
	 *            second collision boundary
	 * @param delta
	 *            time since last update (in seconds)
	 */
	public void performCollision(CollisionShape other, float delta)
	{
		if (listener != null)
		{
			listener.onCollide(this, other, delta);
		}
	}

	/**
	 * Checks if two collision boundaries are considered collided.
	 * 
	 * @param other
	 *            second collision boundary
	 * @param delta
	 *            time since last update (in seconds)
	 * @return true if the listener is defined and detects a collision, false
	 *         otherwise
	 */
	public boolean checkCollision(CollisionShape other, float delta)
	{
		if (listener != null && listener.isCollide(this, other, delta))
		{
			// Check any children before returning.
			if (children != null)
			{
				for (CollisionShape child : children)
				{
					child.testCollision(other, delta);
				}
			}

			// Collision detected.
			return true;
		}

		// No listener or no collision detected.
		return false;
	}

	/**
	 * Checks if two shapes are colliding and triggers the collision event
	 * handler.
	 * 
	 * @param other
	 *            second collision boundary
	 * @param delta
	 *            time since last update (in seconds)
	 */
	public void testCollision(CollisionShape other, float delta)
	{
		if (checkCollision(other, delta))
		{
			performCollision(other, delta);
		}
	}
}