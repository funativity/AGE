package age.asteroids.Entity;

import age.asteroids.scene.Level;
import age.asteroids.util.SingleCollisionListener;
import android.opengl.Matrix;
import funativity.age.collision.CollisionSphere;
import funativity.age.opengl.Entity;
import funativity.age.opengl.MM;
import funativity.age.opengl.Mesh;
import funativity.age.state.Scene;
import funativity.age.util.Geometry3f;
import funativity.age.util.Quaternion;

public abstract class GameEntity extends Entity
{
	protected Quaternion quat = Quaternion.identity();
	private Geometry3f forward = new Geometry3f(0, 0, -1);
	private Geometry3f up = new Geometry3f(0, 1, 0);
	private Geometry3f right = new Geometry3f(1, 0, 0);
	private float yaw, pitch, roll;
	private boolean directionsDirty = true;
	public boolean useQuatRotations = true;

	public CollisionSphere collisionShape;

	/**
	 * Constructor of the GameEntity. Takes a Mesh. Use this constructor if this
	 * object does not collide with anything
	 * 
	 * @param mesh
	 */
	public GameEntity(Mesh mesh)
	{
		super(mesh);
	}

	/**
	 * Constructor of the GameEntity. Takes a Mesh, and a size. Use this
	 * constructor to create a collisionShape for this object
	 * 
	 * @param mesh
	 * @param size
	 */
	public GameEntity(Mesh mesh, float size)
	{
		super(mesh);
		collisionShape = new CollisionSphere(this,
				SingleCollisionListener.getListener(), size);
	}

	/**
	 * Get the collision size of this entity
	 * 
	 * @return
	 */
	public float getSize()
	{
		return collisionShape != null ? collisionShape.getRadius() : 0;
	}

	/**
	 * Remove this entity from the scene as well as the collisionManager
	 */
	public void remove()
	{
		if (collisionShape != null)
			getScene().getCollisionManager().removeChild(collisionShape, 0);
		getScene().removeEntity(this);
	}

	/**
	 * Add this entity to specified level, and that level's collision manager
	 * 
	 * @param Level
	 *            Level to add this entity to
	 */
	public void addToLevel(Level l)
	{
		if (collisionShape != null)
			l.getCollisionManager().addChild(collisionShape, 0);
		l.addEntity(this);
	}

	/**
	 * Get the Level (Scene) this entity is in.
	 * 
	 * @return
	 */
	public Level getLevel()
	{
		return (Level) getScene();
	}

	@Override
	public void update(Scene scene, float delta)
	{
		quat.offsetFromAxisAndAngle(new Geometry3f(0, 1, 0), yaw);
		quat.offsetFromAxisAndAngle(new Geometry3f(1, 0, 0), pitch);
		quat.offsetFromAxisAndAngle(new Geometry3f(0, 0, -1), roll);
		yaw = pitch = roll = 0;
		directionsDirty = true;

		super.update(scene, delta);

		if (!(scene instanceof Level))
			return;
		Level l = (Level) scene;

		final float hw = l.width * 0.5f;
		final float hh = l.height * 0.5f;
		final float hd = l.depth * 0.5f;

		if (getX() > hw || getX() < -hw)
			setX(-Integer.signum((int) getX()) * hw);
		if (getY() > hh || getY() < -hh)
			setY(-Integer.signum((int) getY()) * hh);
		if (getZ() > hd || getZ() < -hd)
			setZ(-Integer.signum((int) getZ()) * hd);
	}

	@Override
	public void render()
	{
		if (useQuatRotations)
		{
			MM.pushMatrix();
			MM.loadIdentity();

			float[] pos = new float[16];
			Matrix.setIdentityM(pos, 0);
			Matrix.translateM(pos, 0, getX(), getY(), getZ());

			float[] rot = new float[16];
			Quaternion.setMatrixFromQuaternion(rot, quat);

			Matrix.multiplyMM(MM.getMMatrix(), 0, pos, 0, rot, 0);

			getDrawable().draw();
			MM.popMatrix();
		}
		else
		{
			super.render();
		}
	}

	/**
	 * Collide this gameEntity with another GameEntity
	 * 
	 * @param other
	 */
	public abstract void collide(GameEntity other);

	private void cleanDirections()
	{
		float[] matrix = new float[16];
		Matrix.setIdentityM(matrix, 0);
		Quaternion.setMatrixFromQuaternion(matrix, quat);

		float[] upTemp = { 0, 1, 0, 0 };
		float[] up = new float[4];
		Matrix.multiplyMV(up, 0, matrix, 0, upTemp, 0);

		float[] forwardTemp = { 0, 0, -1, 0 };
		float[] forward = new float[4];
		Matrix.multiplyMV(forward, 0, matrix, 0, forwardTemp, 0);

		float[] rightTemp = { 1, 0, 0, 0 };
		float[] right = new float[4];
		Matrix.multiplyMV(right, 0, matrix, 0, rightTemp, 0);

		this.forward.set(forward[0], forward[1], forward[2]);
		this.up.set(up[0], up[1], up[2]);
		this.right.set(right[0], right[1], right[2]);

		directionsDirty = false;
	}

	/**
	 * Creates a normalized Geometry3f object that is pointing in the direction
	 * this object is looking
	 * 
	 * @return
	 */
	public Geometry3f getFacingDirection()
	{
		if (directionsDirty)
			cleanDirections();
		return forward;
	}

	/**
	 * Creates a normalized Geometry3f object that is pointing up based off of
	 * the direction this object is looking
	 * 
	 * @return
	 */
	public Geometry3f getVertical()
	{
		if (directionsDirty)
			cleanDirections();
		return up;
	}

	/**
	 * Creates a normalized Geometry3f object that is pointing to the right of
	 * this object based off of the direction this object is looking
	 * 
	 * @return
	 */
	public Geometry3f getRight()
	{
		if (directionsDirty)
			cleanDirections();
		return right;
	}

	public void roll(float angle)
	{
		if (getLevel() == null || !getLevel().inGame)
			return;

		roll += angle;
	}

	public void yaw(float angle)
	{
		if (getLevel() == null || !getLevel().inGame)
			return;

		yaw -= angle;
	}

	public void pitch(float angle)
	{
		if (getLevel() == null || !getLevel().inGame)
			return;

		pitch -= angle;
	}
}
