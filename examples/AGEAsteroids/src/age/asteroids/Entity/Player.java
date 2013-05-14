package age.asteroids.Entity;

import java.util.ArrayList;
import java.util.List;

import funativity.age.opengl.Mesh;
import funativity.age.state.Scene;
import funativity.age.util.Geometry3f;
import funativity.age.util.Logger;
import funativity.age.util.Quaternion;

/**
 * This Entity represents the Player and everything that the player can do
 * within the game.
 * 
 */
public class Player extends GameEntity
{

	// speed of bullets that are fired from the player
	public static final float BULLET_SPEED = 15;
	public static final float PLAYER_MAX_SPEED = 10;
	public static final float PLAYER_ACCELERATION = 7;

	public static final float CAMERA_OFFSET_MIN = 0.01f;
	public static final float CAMERA_OFFSET_MAX = 10;
	public float camera_offset_scale = 0;

	public static Mesh mesh;
	public static final float PLAYER_SIZE = 1f;

	public int health;
	private float immuneTime;

	public List<PowerUp> powerUps;

	// thruster info
	private boolean thrusterDown = false;

	// TODO finish using stats
	// stats
	public float stat_shots_per_sec = 5;

	public Player()
	{
		super(mesh, PLAYER_SIZE);
		health = 10;
		powerUps = new ArrayList<PowerUp>();
	}

	/**
	 * Force this player to shoot. Creates a bullet at the location of this
	 * player, and gives the bullet a velocity in the direction the player is
	 * facing
	 */
	public void shoot()
	{
		Bullet b = new Bullet(this, 1, 5);
		b.setPosition(Geometry3f.add(getPosition(), new Geometry3f(
				getFacingDirection()).scale(PLAYER_SIZE * 0.5f)));
		b.setVelocity(Geometry3f
				.add(mult(getFacingDirection(), BULLET_SPEED), getVelocity())
				.normalize().scale(BULLET_SPEED));
		b.quat.set(quat);
		b.addToLevel(getLevel());
	}

	/**
	 * Does this player have some health left?
	 * 
	 * @return true if this player is still alive
	 */
	public boolean isAlive()
	{
		return health > 0;
	}

	@Override
	public void collide(GameEntity other)
	{
		if (immuneTime > 0)
			return;

		if (other instanceof Bullet)
		{
			Bullet b = (Bullet) other;

			// intended equality check on reference type (because it has to be
			// THIS object)
			if (b.shooter != this)
			{
				health -= b.damage;
				immuneTime = 1;
			}
		}
		else if (other instanceof Asteroid)
		{
			health -= 1;
			immuneTime = 1;
			Logger.i("Player got hit! health: " + health);
		}
	}

	@Override
	public void update(Scene scene, float delta)
	{
		if (immuneTime > 0)
		{
			immuneTime -= delta;
		}

		if (thrusterDown)
		{
			setAcceleration(mult(getFacingDirection(), PLAYER_ACCELERATION));
		}
		else if (Math.abs(getVelocity().length()) > 0.2f)
		{
			setAcceleration(new Geometry3f(getVelocity()).normalize().scale(
					-PLAYER_ACCELERATION * 0.5f));
		}
		else
		{
			setAcceleration(new Geometry3f());
			setVelocity(new Geometry3f());
		}

		super.update(scene, delta);

		// limit max speed
		if (getVelocity().length() > PLAYER_MAX_SPEED)
		{
			getVelocity().normalize().scale(PLAYER_MAX_SPEED);
		}

		// update powerups
		ArrayList<PowerUp> removePowerUps = new ArrayList<PowerUp>();
		for (PowerUp p : powerUps)
		{
			if (p.decTime(delta))
				removePowerUps.add(p);
		}
		if (removePowerUps.size() > 0)
			powerUps.removeAll(removePowerUps);
	}

	/**
	 * Give this player a powerup. Some powerups MAY not have lasting effects
	 * and will not be added to the powerup list. Those types of powerups will
	 * have their effects done during this method.
	 * 
	 * @param powerUp
	 */
	public void addPowerUp(PowerUp powerUp)
	{
		/*
		 * if( powerUp.isOneTimeEffect() ) { //do effect } else
		 */
		powerUps.add(powerUp);
	}

	private Geometry3f mult(Geometry3f left, float right)
	{
		return new Geometry3f(left.getX() * right, left.getY() * right,
				left.getZ() * right);
	}

	public void thruster(boolean down)
	{
		thrusterDown = down;
	}

	public Geometry3f getCameraPos()
	{
		return Geometry3f.sub(getPosition(), new Geometry3f(
				getFacingDirection()).scale(camera_offset_scale));
	}

	@Override
	public void render()
	{
		// only render the player if the camera is outside of the ship
		if (camera_offset_scale > CAMERA_OFFSET_MIN)
		{
			super.render();
		}
	}

	public void reset()
	{
		setPosition(new Geometry3f());
		quat = Quaternion.identity();
		thrusterDown = false;
	}

}
