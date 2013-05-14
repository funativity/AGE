package age.asteroids.Entity;

import funativity.age.opengl.Mesh;
import funativity.age.state.Scene;
import funativity.age.util.Geometry3f;
import age.asteroids.enums.Audio;
import age.asteroids.scene.Level;

/**
 * This Entity represents a single Asteroid within the game. The logic for
 * controlling how it moves and reacts to collisions with Bullets is implemented
 * here.
 */
public class Asteroid extends GameEntity
{
	public static int count;
	public static Mesh mesh_size1;
	public static Mesh mesh_size2;
	public static Mesh mesh_size3;
	public static Mesh mesh_size4;
	public static Mesh mesh_size5;
	public static Mesh mesh_size6;
	public static final int MAX_SIZE = 6;

	private Geometry3f rotationVel;

	public Asteroid(int size)
	{
		super(getMesh(size > MAX_SIZE ? MAX_SIZE : size),
				size > MAX_SIZE ? MAX_SIZE : size);
		rotationVel = new Geometry3f(randomGeometry(20, 100));
		useQuatRotations = false;
	}

	private static Mesh getMesh(int size)
	{
		switch (size)
		{
			case 1:
				return mesh_size1;
			case 2:
				return mesh_size2;
			case 3:
				return mesh_size3;
			case 4:
				return mesh_size4;
			case 5:
				return mesh_size5;
			default:
			case 6:
				return mesh_size6;
		}
	}

	@Override
	public void update(Scene scene, float delta)
	{
		setRX(getRX() + rotationVel.getX() * delta);
		setRY(getRY() + rotationVel.getY() * delta);
		setRZ(getRZ() + rotationVel.getZ() * delta);

		super.update(scene, delta);
	}

	private static int getNextSize(float size)
	{
		return (int) (size / 2f);
	}

	@Override
	public void collide(GameEntity other)
	{
		// asteroids probably cant shoot, but may be worth checking
		if (!(other instanceof Bullet))
		{
			return;
		}
		Bullet b = (Bullet) other;

		// intended equality check on reference type (because it has to be
		// THIS object)
		if (b.shooter != this && b.damage > 0)
		{
			if (getSize() > 1)
			{
				for (int i = 0; i < getSize() / 1.5f; i++)
				{
					Asteroid a = new Asteroid(getNextSize(getSize()));
					a.setPosition(new Geometry3f(getPosition()));
					a.addToLevel((Level) getScene());
					count++;
					final double angle = Math.toRadians(360f / getSize() * i);
					final float dx = (float) Math.cos(angle);
					final float dy = 0;
					final float dz = (float) Math.sin(angle);
					a.setVelocity(Geometry3f.add(
							new Geometry3f(dx, dy, dz).scale(random(1, 5)),
							getVelocity()));
				}
			}

			getLevel().addToScore((int) (MAX_SIZE - getSize() + 1) * 10);
			getLevel().state.play(Audio.EXPLODE, true);
			count--;
			remove();
			b.remove();
		}
	}

	/**
	 * Set the location of this asteroid anywhere within the provided bounds
	 * 
	 * @param width
	 * @param height
	 * @param depth
	 */
	public void setRandomLocation(float width, float height, float depth)
	{
		setRandomLocation(width, height, depth, 0);
	}

	/**
	 * Set the location of this Asteroid anywhere within the provided bounds, as
	 * long as the random location is at least avoidRadius distance away from
	 * the center (0, 0, 0)
	 * 
	 * @param width
	 * @param height
	 * @param depth
	 * @param avoidRadius
	 */
	public void setRandomLocation(float width, float height, float depth,
			float avoidRadius)
	{
		final float hw = width * 0.5f;
		final float hh = height * 0.5f;
		final float hd = depth * 0.5f;

		Geometry3f center = new Geometry3f();
		Geometry3f pos;
		do
		{
			pos = new Geometry3f(random(-hw, hw), random(-hh, hh), random(-hd,
					hd));
		}
		while (Geometry3f.distance(pos, center) < avoidRadius);
		setPosition(pos);
	}

	/**
	 * Set the speed of this Asteroid to a random speed between the provided
	 * bounds. This will generate a random direction also.
	 * 
	 * @param minSpeed
	 * @param maxSpeed
	 */
	public void setRandomVelocity(float minSpeed, float maxSpeed)
	{
		setVelocity(randomGeometry(minSpeed, maxSpeed));
	}

	/**
	 * Generate a random number between 2 floats
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	private static float random(float min, float max)
	{
		return (float) (Math.random() * (max - min) + min);
	}

	/**
	 * Generate a random Geometry3f. The length of generated Geometry3f will be
	 * between min and max
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	private static Geometry3f randomGeometry(float min, float max)
	{
		Geometry3f r = new Geometry3f();
		do
		{
			r.set((float) (Math.random() - 0.5f),
					(float) (Math.random() - 0.5f),
					(float) (Math.random() - 0.5f));

		}
		while (r.length() <= 0);
		return r.normalize().scale(random(min, max));
	}

}
