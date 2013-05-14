package age.asteroids.Entity;

import funativity.age.opengl.Mesh;
import funativity.age.state.Scene;

/**
 * This Entity represents a single Bullet the has been fired by the player.
 * 
 */
public class Bullet extends GameEntity
{
	public static Mesh mesh;

	/**
	 * Entity that shot this bullet (Generally a bullet cannot damage the one
	 * that shot it)
	 */
	public final GameEntity shooter;

	/**
	 * How much damage this bullet will deal when it hits something
	 */
	public int damage;
	private float duration;

	/**
	 * Create a bullet. The shooter cannot be damaged by this bullet. The bullet
	 * will automatically be removed from the world when it expires
	 * 
	 * @param shooter
	 *            who shot this bullet (cannot take damage from this bullet)
	 * @param damage
	 *            how much damage this bullet can deal
	 * @param duration
	 *            how much time (in seconds) this bullet will last before it
	 *            expires
	 */
	public Bullet(GameEntity shooter, int damage, float duration)
	{
		super(mesh, 0.2f);

		this.damage = damage;
		this.shooter = shooter;
		this.duration = duration;
	}

	@Override
	public void collide(GameEntity other)
	{
	}

	@Override
	public void update(Scene scene, float delta)
	{
		if ((duration -= delta) <= 0)
		{
			remove();
			damage = 0;
		}

		super.update(scene, delta);
	}
}
