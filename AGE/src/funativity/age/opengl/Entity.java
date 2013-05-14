package funativity.age.opengl;

import funativity.age.state.Scene;
import funativity.age.util.Geometry3f;

/**
 * An OpenGL mesh container for performing physics-based transforms.
 * 
 */
public class Entity
{
	private Drawable drawable;

	// Physics.
	private Geometry3f position;
	private Geometry3f velocity;
	private Geometry3f acceleration;

	// Not instantiated in a constructor.
	private Geometry3f rotation = new Geometry3f();
	private Scene scene;

	/**
	 * Default constructor at the origin with no velocity (change in position)
	 * and no acceleration (change in velocity). Uses no mesh so nothing will
	 * render by default.
	 */
	public Entity()
	{
		this(null);
	}

	/**
	 * Creates an OpenGL mesh container at the origin with no velocity (change
	 * in position) and no acceleration (change in velocity).
	 * 
	 * @param drawable
	 *            OpenGL render
	 */
	public Entity(Drawable drawable)
	{
		this(drawable, new Geometry3f());
	}

	/**
	 * Creates an OpenGL drawable container with no velocity (change in
	 * position) and no acceleration (change in velocity).
	 * 
	 * @param drawable
	 *            OpenGL render
	 * @param position
	 *            position on all three axes (x, y, z)
	 */
	public Entity(Drawable drawable, Geometry3f position)
	{
		this(drawable, position, new Geometry3f());
	}

	/**
	 * Creates an OpenGL drawable container with no acceleration (change in
	 * velocity).
	 * 
	 * @param drawable
	 *            OpenGL render
	 * @param position
	 *            position on all three axes (x, y, z)
	 * @param velocity
	 *            velocity (change in position) on all three axes (x, y, z)
	 */
	public Entity(Drawable drawable, Geometry3f position, Geometry3f velocity)
	{
		this(drawable, position, velocity, new Geometry3f());
	}

	/**
	 * Creates an OpenGL drawable container.
	 * 
	 * @param drawable
	 *            OpenGL render
	 * @param position
	 *            position on all three axes (x, y, z)
	 * @param velocity
	 *            velocity (change in position) on all three axes (x, y, z)
	 * @param acceleration
	 *            acceleration (change in velocity) on all three axes (x, y, z)
	 */
	public Entity(Drawable drawable, Geometry3f position, Geometry3f velocity,
			Geometry3f acceleration)
	{
		set(drawable, position, velocity, acceleration);
	}

	/**
	 * Creates a new OpenGL drawable container using an old one.
	 * 
	 * @param drawable
	 *            OpenGL render
	 * @param old
	 *            container to copy
	 */
	public Entity(Drawable drawable, Entity old)
	{
		this(drawable, new Geometry3f(old.position), new Geometry3f(
				old.velocity), new Geometry3f(old.acceleration));
		setRotation(new Geometry3f(old.rotation));
	}

	/**
	 * Set all of this entities major attributes at once.
	 * 
	 * @param drawable
	 *            OpenGL render
	 * @param position
	 *            position on all three axes (x, y, z)
	 * @param velocity
	 *            velocity (change in position) on all three axes (x, y, z)
	 * @param acceleration
	 *            acceleration (change in velocity) on all three axes (x, y, z)
	 * @return this
	 */
	public Entity set(Drawable drawable, Geometry3f position,
			Geometry3f velocity, Geometry3f acceleration)
	{
		setDrawable(drawable);
		setPosition(position);
		setVelocity(velocity);
		setAcceleration(acceleration);
		return this;
	}

	/**
	 * Gets the Drawable object used by this Entity
	 * @return OpenGL render
	 */
	public Drawable getDrawable()
	{
		return drawable;
	}

	/**
	 * Sets the Drawable object used by this Entity
	 * 
	 * @param drawable
	 *            OpenGL render, use null for no render
	 * @return this
	 */
	public Entity setDrawable(Drawable drawable)
	{
		this.drawable = drawable;
		return this;
	}

	/**
	 * Gets the 3f position of this Entity
	 * 
	 * @return position on all three axes (x, y, z)
	 */
	public Geometry3f getPosition()
	{
		return position;
	}

	/**
	 * Sets the 3f position of this Entity
	 * 
	 * @param position
	 *            position on all three axes (x, y, z)
	 * @return this
	 */
	public Entity setPosition(Geometry3f position)
	{
		this.position = position;
		return this;
	}

	/**
	 * Gets the 3f velocity of this Entity
	 * 
	 * @return velocity (change in position) on all three axes (x, y, z)
	 */
	public Geometry3f getVelocity()
	{
		return velocity;
	}

	/**
	 * Sets the 3f Velocity of this Entity
	 * 
	 * @param velocity
	 *            (change in position) on all three axes (x, y, z)
	 * @return this
	 */
	public Entity setVelocity(Geometry3f velocity)
	{
		this.velocity = velocity;
		return this;
	}

	/**
	 * Gets the 3f Acceleration of this Entity
	 * 
	 * @return acceleration (change in velocity) on all three axes (x, y, z)
	 */
	public Geometry3f getAcceleration()
	{
		return acceleration;
	}

	/**
	 * Sets the 3f Acceleration of this Entity
	 * 
	 * @param acceleration
	 *            (change in velocity) on all three axes (x, y, z)
	 * @return this
	 */
	public Entity setAcceleration(Geometry3f acceleration)
	{
		this.acceleration = acceleration;
		return this;
	}

	/**
	 * Gets the 3f rotation of this Entity
	 * 
	 * @return rotation (in degrees) on all three axes (x, y, z)
	 */
	public Geometry3f getRotation()
	{
		return rotation;
	}

	/**
	 * Sets the 3f rotation of this Entity
	 * 
	 * @param rotation
	 *            (in degrees) on all three axes (x, y, z)
	 * @return this
	 */
	public Entity setRotation(Geometry3f rotation)
	{
		this.rotation = rotation;
		return this;
	}

	/**
	 * Gets the x value of the position
	 * 
	 * @return position on x-axis
	 */
	public float getX()
	{
		return position.getX();
	}

	/**
	 * Sets the x value of the position
	 * 
	 * @param x
	 *            position on x-axis
	 * @return this
	 */
	public Entity setX(float x)
	{
		position.setX(x);
		return this;
	}

	/**
	 * Gets the y value of the position
	 * 
	 * @return position on y-axis
	 */
	public float getY()
	{
		return position.getY();
	}

	/**
	 * Sets the y value of the position
	 * 
	 * @param y
	 *            position on y-axis
	 * @return this
	 */
	public Entity setY(float y)
	{
		position.setY(y);
		return this;
	}

	/**
	 * Gets the z value of the position
	 * 
	 * @return position on z-axis
	 */
	public float getZ()
	{
		return position.getZ();
	}

	/**
	 * Sets the z value of the position
	 * 
	 * @param z
	 *            position on z-axis
	 * @return this
	 */
	public Entity setZ(float z)
	{
		position.setZ(z);
		return this;
	}

	/**
	 * Gets the x value of the velocity
	 * 
	 * @return velocity (change in position) on x-axis
	 */
	public float getDX()
	{
		return velocity.getX();
	}

	/**
	 * Sets the x value of the velocity
	 * 
	 * @param dx
	 *            velocity (change in position) on x-axis
	 * @return this
	 */
	public Entity setDX(float dx)
	{
		velocity.setX(dx);
		return this;
	}

	/**
	 * Gets the y value of the velocity
	 * 
	 * @return velocity (change in position) on y-axis
	 */
	public float getDY()
	{
		return velocity.getY();
	}

	/**
	 * Sets the y value of the velocity
	 * 
	 * @param dy
	 *            velocity (change in position) on y-axis
	 * @return this
	 */
	public Entity setDY(float dy)
	{
		velocity.setY(dy);
		return this;
	}

	/**
	 * Gets the z value of the velocity
	 * 
	 * @return velocity (change in position) on z-axis
	 */
	public float getDZ()
	{
		return velocity.getZ();
	}

	/**
	 * Sets the z value of the velocity
	 * 
	 * @param dz
	 *            velocity (change in position) on z-axis
	 * @return this
	 */
	public Entity setDZ(float dz)
	{
		velocity.setZ(dz);
		return this;
	}

	/**
	 * Gets the x value of the acceleration
	 * 
	 * @return acceleration (change in velocity) on x-axis
	 */
	public float getAX()
	{
		return acceleration.getX();
	}

	/**
	 * Sets the x value of the acceleration
	 * 
	 * @param ax
	 *            acceleration (change in velocity) on x-axis
	 * @return this
	 */
	public Entity setAX(float ax)
	{
		acceleration.setX(ax);
		return this;
	}

	/**
	 * Gets the y value of the acceleration
	 * 
	 * @return acceleration (change in velocity) on y-axis
	 */
	public float getAY()
	{
		return acceleration.getY();
	}

	/**
	 * Sets the y value of the acceleration
	 * 
	 * @param ay
	 *            acceleration (change in velocity) on y-axis
	 * @return this
	 */
	public Entity setAY(float ay)
	{
		acceleration.setY(ay);
		return this;
	}

	/**
	 * Gets the z value of the acceleration
	 * 
	 * @return acceleration (change in velocity) on z-axis
	 */
	public float getAZ()
	{
		return acceleration.getZ();
	}

	/**
	 * Sets the z value of the acceleration
	 * 
	 * @param az
	 *            acceleration (change in velocity) on z-axis
	 * @return this
	 */
	public Entity setAZ(float az)
	{
		acceleration.setZ(az);
		return this;
	}

	/**
	 * Gets the rotation value of the x axis
	 * 
	 * @return rotation (in degrees) on x-axis
	 */
	public float getRX()
	{
		return rotation.getX();
	}

	/**
	 * Sets the rotation value of the x axis
	 * 
	 * @param rx
	 *            rotation (in degrees) on x-axis
	 * @return this
	 */
	public Entity setRX(float rx)
	{
		rotation.setX(rx);
		return this;
	}

	/**
	 * Gets the rotation value of the y axis
	 * 
	 * @return rotation (in degrees) on y-axis
	 */
	public float getRY()
	{
		return rotation.getY();
	}

	/**
	 * Sets the rotation value of the y axis
	 * 
	 * @param ry
	 *            rotation (in degrees) on y-axis
	 * @return this
	 */
	public Entity setRY(float ry)
	{
		rotation.setY(ry);
		return this;
	}

	/**
	 * Gets the rotation value of the z axis
	 * 
	 * @return rotation (in degrees) on z-axis
	 */
	public float getRZ()
	{
		return rotation.getZ();
	}

	/**
	 * Sets the rotation value of the z axis
	 * 
	 * @param rz
	 *            rotation (in degrees) on z-axis
	 * @return this
	 */
	public Entity setRZ(float rz)
	{
		rotation.setZ(rz);
		return this;
	}

	/**
	 * Gets the Scene that this Entity exists in
	 * 
	 * @return Scene using this Entity
	 */
	public Scene getScene()
	{
		return scene;
	}

	/**
	 * Sets the Scene that this entity exists in
	 * 
	 * @param scene
	 *            Scene using this Entity
	 * @return this
	 */
	public Entity setScene(Scene scene)
	{
		this.scene = scene;
		return this;
	}

	/**
	 * Renders the drawable (if it exists) using the entity's location and
	 * rotation.
	 */
	public void render()
	{
		if (drawable == null)
			return;

		MM.pushMatrix();

		MM.translate(getX(), getY(), getZ());
		MM.rotate(getRX(), 1, 0, 0);
		MM.rotate(getRY(), 0, 1, 0);
		MM.rotate(getRZ(), 0, 0, 1);

		drawable.draw();

		MM.popMatrix();
	}

	/**
	 * Updates the position using velocity and delta, and then updates the
	 * velocity using acceleration and delta. Also requests the drawable (if it
	 * exists) to update using delta.
	 * 
	 * @param scene
	 *            entity's container
	 * @param delta
	 *            time (in seconds) since last update
	 */
	public void update(Scene scene, float delta)
	{
		// Update the entity's velocity.
		setDX(getDX() + getAX() * delta);
		setDY(getDY() + getAY() * delta);
		setDZ(getDZ() + getAZ() * delta);

		// Update the entity's position.
		setX(getX() + getDX() * delta);
		setY(getY() + getDY() * delta);
		setZ(getZ() + getDZ() * delta);

		// Request the mesh update.
		if (drawable != null)
		{
			drawable.update(delta);
		}
	}
}