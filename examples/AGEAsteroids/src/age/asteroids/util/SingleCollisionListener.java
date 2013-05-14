package age.asteroids.util;

import age.asteroids.Entity.GameEntity;
import funativity.age.collision.CollisionListener;
import funativity.age.collision.CollisionShape;

public class SingleCollisionListener implements CollisionListener
{
	private static SingleCollisionListener listener;

	private SingleCollisionListener()
	{
	}

	public static SingleCollisionListener getListener()
	{
		if (listener == null)
		{
			listener = new SingleCollisionListener();
		}
		return listener;
	}

	@Override
	public boolean isCollide(CollisionShape shape1, CollisionShape shape2,
			float delta)
	{
		return shape1.isIntersect(shape2);
	}

	@Override
	public void onCollide(CollisionShape shape1, CollisionShape shape2,
			float delta)
	{
		if (shape1.getEntity() instanceof GameEntity
				&& shape2.getEntity() instanceof GameEntity)
		{
			((GameEntity) shape1.getEntity()).collide((GameEntity) shape2
					.getEntity());
			((GameEntity) shape2.getEntity()).collide((GameEntity) shape1
					.getEntity());
		}
	}
}
