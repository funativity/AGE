package funativity.age.collision;

/**
 * A collision event handler.
 * 
 */
public interface CollisionListener
{
	/**
	 * Called to check if two shapes are considered collided in a
	 * CollisionManager layer.
	 * 
	 * @param shape1
	 *            always the earlier shape in the layer
	 * @param shape2
	 *            always the latter shape in the layer
	 * @param delta
	 *            time since last update (in seconds)
	 * @return true if a collision between the two shapes is compatible and
	 *         detected, false otherwise
	 */
	public boolean isCollide(CollisionShape shape1, CollisionShape shape2,
			float delta);

	/**
	 * Called when two shapes are considered collided in a CollisionManager
	 * layer.
	 * 
	 * @param shape1
	 *            always the earlier shape in the layer
	 * @param shape2
	 *            always the latter shape in the layer
	 * @param delta
	 *            time since last update (in seconds)
	 */
	public void onCollide(CollisionShape shape1, CollisionShape shape2,
			float delta);
}