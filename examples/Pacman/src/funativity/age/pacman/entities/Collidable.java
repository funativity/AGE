package funativity.age.pacman.entities;

import funativity.age.collision.CollisionShape;
import funativity.age.pacman.map.Map;

public class Collidable extends CollisionShape
{
	public Collidable(Player entity)
	{
		super(entity, entity);
	}

	@Override
	public boolean isIntersect(CollisionShape other)
	{
		if (other instanceof Collidable)
		{
			Player o = (Player) other.getEntity(); // other
			Player m = (Player) getEntity(); // me

			if (Map.getTileX(m) == Map.getTileX(o)
					&& Map.getTileY(m) == Map.getTileY(o))
				return true;

		}

		return false;
	}
}
