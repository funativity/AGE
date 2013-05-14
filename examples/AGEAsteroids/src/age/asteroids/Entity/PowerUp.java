package age.asteroids.Entity;

import age.asteroids.enums.Audio;

/**
 * This Entity represents a PowerUp within the game. Powerups can be pretty much
 * anything that the player would want to pick up during gameplay. Some exampls
 * are, extra lives, a temporary shield, or a new gun.
 * 
 */
public class PowerUp extends GameEntity
{
	public enum PowerUpType
	{
		SHOTS_PER_SEC(10), HEALTH(0);

		public final float duration;

		PowerUpType(float duration)
		{
			this.duration = duration;
		}
	}

	private float durationRemaining;
	private PowerUpType type;
	private Player user;

	public PowerUp(PowerUpType type)
	{
		super(null, 0.5f);
		this.type = type;
	}

	@Override
	public void collide(GameEntity other)
	{
		if (other instanceof Player)
		{
			remove();
			getLevel().state.play(Audio.POWERUP, true);

			user = (Player) other;
			playerPowerUp(type, user, true);
		}
	}

	/**
	 * update the duration remaining for this powerup. returns true if this
	 * powerup is finished. This method will update any change stats of the
	 * player affected by this powerup
	 * 
	 * @param delta
	 * @return
	 */
	public boolean decTime(float delta)
	{
		if (durationRemaining > 0 && (durationRemaining -= delta) <= 0)
		{
			playerPowerUp(type, user, false);
			return true;
		}
		return false;
	}

	private static void playerPowerUp(PowerUpType type, Player player,
			boolean activate)
	{
		switch (type)
		{
			case SHOTS_PER_SEC:
				player.stat_shots_per_sec += activate ? 1 : -1;
				break;
			case HEALTH:
				if (activate)
					player.health++;
				break;
		}
	}
}
