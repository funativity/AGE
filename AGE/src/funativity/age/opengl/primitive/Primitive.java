package funativity.age.opengl.primitive;

import funativity.age.opengl.DrawMode;
import funativity.age.opengl.Mesh;
import funativity.age.util.Geometry3f;

/**
 * OpenGL mesh representing a simple 2D or 3D shape.
 */
public abstract class Primitive extends Mesh
{
	private Geometry3f size;

	private float[] vertices;
	private short[] drawOrder;
	private DrawMode mode;

	/**
	 * Creates a primitive using a width, height, and depth of 1.
	 */
	public Primitive()
	{
		this(new Geometry3f(1, 1, 1));
	}

	/**
	 * Creates a primitive using the given size.
	 * 
	 * @param size
	 *            size on x-axis, y-axis, and z-axis
	 */
	public Primitive(Geometry3f size)
	{
		setSize(size);
	}

	/**
	 * Initializes the OpenGL mesh. Must be called to actually update the OpenGL
	 * mesh when any settings are changed after initialization.
	 */
	public void build()
	{
		initMesh(initVertices(), initDrawOrder(), initDrawMode());
	}

	/**
	 * @return size on x-axis, y-axis, and z-axis
	 */
	public Geometry3f getSize()
	{
		return size;
	}

	/**
	 * @param size
	 *            size on x-axis, y-axis, and z-axis
	 * @return this
	 */
	public Primitive setSize(Geometry3f size)
	{
		this.size = size;
		return this;
	}

	/**
	 * @return size on x-axis
	 */
	public float getWidth()
	{
		return size.getX();
	}

	/**
	 * @param width
	 *            size on x-axis
	 * @return this
	 */
	public Primitive setWidth(float width)
	{
		size.setX(width);
		return this;
	}

	/**
	 * @return size on y-axis
	 */
	public float getHeight()
	{
		return size.getY();
	}

	/**
	 * @param height
	 *            size on y-axis
	 * @return this
	 */
	public Primitive setHeight(float height)
	{
		size.setY(height);
		return this;
	}

	/**
	 * @return size on z-axis
	 */
	public float getDepth()
	{
		return size.getZ();
	}

	/**
	 * @param depth
	 *            size on z-axis
	 * @return this
	 */
	public Primitive setDepth(float depth)
	{
		size.setZ(depth);
		return this;
	}

	/**
	 * @return array of vertices
	 */
	public float[] getVertices()
	{
		return vertices;
	}

	/**
	 * @param vertices
	 *            array of vertices
	 * @return this
	 */
	public Primitive setVertices(float[] vertices)
	{
		this.vertices = vertices;
		return this;
	}

	/**
	 * @return array of vertex draw orders
	 */
	public short[] getDrawOrder()
	{
		return drawOrder;
	}

	/**
	 * @param drawOrder
	 *            array of vertex draw orders
	 * @return this
	 */
	public Primitive setDrawOrder(short[] drawOrder)
	{
		this.drawOrder = drawOrder;
		return this;
	}

	/**
	 * @return OpenGL drawing mode
	 */
	public DrawMode getMode()
	{
		return mode;
	}

	/**
	 * @param mode
	 *            OpenGL drawing mode
	 * @return this
	 */
	public Primitive setMode(DrawMode mode)
	{
		this.mode = mode;
		return this;
	}

	/**
	 * @return defines the vertices on initialization
	 */
	public abstract float[] initVertices();

	/**
	 * @return defines the draw orders on initialization
	 */
	public abstract short[] initDrawOrder();

	/**
	 * @return defines the draw mode on initialization
	 */
	public abstract DrawMode initDrawMode();
}