package funativity.age.opengl.primitive;

import funativity.age.opengl.DrawMode;
import funativity.age.opengl.shaders.SimpleTechnique;
import funativity.age.util.Geometry3f;

/**
 * OpenGL mesh representing a closed triangle fan. The higher the number of
 * segments, the more rounded the shape appears.
 */
public class TriangleFan extends Primitive
{
	private int segments;

	/**
	 * Creates a centered, xy-plane closed triangle fan composed of the given
	 * number of triangles and sized using a width and height of 1.
	 * 
	 * @param segments
	 *            number of triangles to use
	 */
	public TriangleFan(int segments)
	{
		this(1, 1, segments);
	}

	/**
	 * Creates a centered, xy-plane closed triangle fan composed of the given
	 * number of triangles and sized using the same width and height.
	 * 
	 * @param radius
	 *            radius of the closed triangle fan on the x-axis and y-axis
	 * @param segments
	 *            number of triangles to use
	 */
	public TriangleFan(float radius, int segments)
	{
		this(2 * radius, 2 * radius, segments);
	}

	/**
	 * Creates a centered, xy-plane closed triangle fan composed of the given
	 * number of triangles and sized using the given width and height.
	 * 
	 * @param width
	 *            size on x-axis
	 * @param height
	 *            size on y-axis
	 * @param segments
	 *            number of triangles to use
	 */
	public TriangleFan(float width, float height, int segments)
	{
		super(new Geometry3f(width, height));
		setSegments(segments);
		build();
	}

	/**
	 * @return number of triangles composing this closed triangle fan
	 */
	public int getSegments()
	{
		return segments;
	}

	/**
	 * Note this will not be represented by the mesh until build() is called
	 * 
	 * @param segments
	 *            number of triangles composing this closed triangle fan
	 * @return this
	 */
	public TriangleFan setSegments(int segments)
	{
		this.segments = segments;
		return this;
	}

	/**
	 * Creates vertices for a closed triangle fan.
	 * 
	 * @param segments
	 *            number of triangles to use
	 * @return vertices for a closed triangle fan
	 */
	public float[] getTriangleFanVertices(int segments)
	{
		// One-time calculation of trigonometry values.
		float theta = 2 * (float) Math.PI / (float) segments;
		float sin = (float) Math.sin(theta);
		float cos = (float) Math.cos(theta);

		// Scaling factors.
		float xScale = getWidth() / 2f;
		float yScale = getHeight() / 2f;

		// Starting point.
		float x = 1;
		float y = 0;

		// Allocate an extra vertex for the center vertex.
		float[] vertices = new float[(segments + 1)
				* SimpleTechnique.ELEMENT_COUNT];

		// Create the center vertex.
		vertices[0] = 0;
		vertices[1] = 0;
		vertices[2] = 0;
		vertices[3] = 0;
		vertices[4] = 0;

		// For each triangle segment...
		for (int vertex = 1; vertex <= segments; vertex++)
		{
			// ...create a new vertex, scaling and translating.
			int vertexIndex = vertex * SimpleTechnique.ELEMENT_COUNT;
			vertices[vertexIndex] = xScale * x;
			vertices[vertexIndex + 1] = yScale * y;
			vertices[vertexIndex + 2] = 0;
			vertices[vertexIndex + 3] = 0;
			vertices[vertexIndex + 4] = 0;

			// Use Z-axis rotation math to find the next vertex.
			float oldX = x;
			float oldY = y;
			x = cos * oldX - sin * oldY;
			y = sin * oldX + cos * oldY;
		}
		return vertices;
	}

	/**
	 * Connects the vertices made with an amount of segments to create a closed
	 * triangle fan.
	 * 
	 * @param segments
	 *            number of triangles to use
	 * @return draw order for the vertices
	 */
	public short[] getTriangleFanDrawOrder(int segments)
	{
		// Make triangles for each non-center vertex.
		short[] drawOrder = new short[segments
				* SimpleTechnique.ELEMENT_COUNT_POSITION];
		for (int draw = 0; draw < segments; draw++)
		{
			int drawIndex = draw * SimpleTechnique.ELEMENT_COUNT_POSITION;

			// First vertex is always the center.
			drawOrder[drawIndex] = 0;

			// Second vertex.
			drawOrder[drawIndex + 1] = (short) (draw + 1);

			// Last vertex in the last draw is a special case.
			if (draw == segments - 1)
			{
				// Close the triangle fan.
				drawOrder[drawIndex + 2] = 1;
			}
			// Third vertex.
			else
			{
				drawOrder[drawIndex + 2] = (short) (draw + 2);
			}
		}
		return drawOrder;
	}

	@Override
	public float[] initVertices()
	{
		return getTriangleFanVertices(segments);
	}

	@Override
	public short[] initDrawOrder()
	{
		return getTriangleFanDrawOrder(segments);
	}

	@Override
	public DrawMode initDrawMode()
	{
		return DrawMode.GL_TRIANGLES;
	}
}