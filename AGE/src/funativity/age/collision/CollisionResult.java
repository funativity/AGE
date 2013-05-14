package funativity.age.collision;

import funativity.age.util.Geometry3f;

/**
 * Holds popular collision event handlers.
 * 
 */
public class CollisionResult
{
	/**
	 * Swaps velocities to simulate a frictionless head-on collision between two
	 * entities.
	 * 
	 * @param shape1
	 *            first collision boundary
	 * @param shape2
	 *            second collision boundary
	 */
	public static void swapVelocity(CollisionShape shape1, CollisionShape shape2)
	{
		// Swap the vectors for the shape's entities.
		Geometry3f velocity1 = new Geometry3f(shape1.getEntity().getVelocity());
		Geometry3f velocity2 = new Geometry3f(shape2.getEntity().getVelocity());
		shape1.getEntity().setVelocity(velocity2);
		shape2.getEntity().setVelocity(velocity1);
	}

	/**
	 * Creates an event handler that uses intersection for checking all
	 * collisions and responds by swapping the velocities of each collision
	 * boundary's entity.
	 * 
	 * @return collision event handler
	 */
	public static CollisionListener getSwapVelocityListener()
	{
		CollisionListener listener = new CollisionListener()
		{
			@Override
			public boolean isCollide(CollisionShape shape1,
					CollisionShape shape2, float delta)
			{
				return shape1.isIntersect(shape2);
			}

			@Override
			public void onCollide(CollisionShape shape1, CollisionShape shape2,
					float delta)
			{
				swapVelocity(shape1, shape2);
			}
		};

		return listener;
	}
}