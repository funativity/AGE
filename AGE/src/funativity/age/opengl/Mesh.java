package funativity.age.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import funativity.age.opengl.shaders.SimpleTechnique;
import funativity.age.opengl.shaders.Technique;
import funativity.age.textures.Texture;
import funativity.age.textures.TextureLoader;
import funativity.age.util.Logger;

/**
 * An OpenGL render.
 * 
 */
public class Mesh implements Drawable
{
	// number of bytes in a float
	public static final int SIZE_FLOAT = Float.SIZE / Byte.SIZE;

	// number of bytes in a short
	public static final int SIZE_SHORT = Short.SIZE / Byte.SIZE;

	// blank 1x1 white texture. Used when no other texture is being used, but
	// the technique needs a texture bound
	private static final Texture NO_TEXTURE = TextureLoader.getNoTexture();

	// drawing info
	private Texture texture;
	private AGEColor color = new AGEColor();
	private Technique technique;

	// Handles to OpenGL buffers. Points to where the data is being stored in
	// the OpenGL context. Used to draw this mesh.
	private int vbo;
	private int vio;

	// drawing vars
	private boolean indexDrawing = false;
	private int dataSize;
	private DrawMode mode;

	/**
	 * default constructor. Sets starting technique to the simpleTechnique
	 */
	public Mesh()
	{
		this(SimpleTechnique.getTechnique());
	}

	/**
	 * Set the starting technique of this mesh to specified technique.
	 * 
	 * @param technique
	 *            technique to use to draw this mesh
	 */
	public Mesh(Technique technique)
	{
		this.technique = technique;
		this.texture = NO_TEXTURE;
	}

	/**
	 * One-time preparation for the mesh. vertices contains all of the vertex
	 * data to draw the mesh. Indices is the order of drawing. If indices is
	 * null, uses normal ordered drawing, otherwise uses indexed drawing
	 * 
	 * @param vertices
	 *            mesh's vertex data - example (x, y, z, u, v)
	 * @param indices
	 *            mesh's vertex draw order
	 * @param mode
	 *            Mode of how this mesh will draw - example options
	 *            GL_TRIANGLES, or GL_POINTS
	 * 
	 * @throws NullPointerException
	 *             if vertices or mode is null
	 */
	public void initMesh(float[] vertices, short[] indices, DrawMode mode)
	{
		if (vertices == null)
			throw new NullPointerException("vertices cannot be null");

		setMode(mode);

		// setup vertex buffer
		{
			// create and bind the vertex VBO
			vbo = genBuffer();
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);

			// send data to OpenGL
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.length
					* SIZE_FLOAT, createBuffer(vertices), GLES20.GL_STATIC_DRAW);

			// unbind buffer
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		}

		if (indices == null)
		{
			indexDrawing = false;
			dataSize = vertices.length;
		}
		// setup index buffer
		else
		{
			indexDrawing = true;
			dataSize = indices.length;

			// create and bind the index VBO
			vio = genBuffer();
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vio);

			// send index data
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indices.length
					* SIZE_SHORT, createBuffer(indices), GLES20.GL_STATIC_DRAW);

			// unbind buffer
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		}

		Logger.checkOGLError();
	}

	/**
	 * Send new data into the buffer. Parameters are just like assigning an
	 * attribute pointer: count is number of elements in a set, size is number
	 * of bytes per set, offset is number of bytes into the buffer to start, and
	 * stride is number of bytes to skip between new assignments.
	 * 
	 * @param data
	 *            New data being passed in
	 * @param count
	 *            Number of elements in a set
	 * @param byteSize
	 *            Number of bytes in a set <B>(Remember it is number of
	 *            bytes)</B>
	 * @param byteOffset
	 *            Number of bytes into the buffer to start new data <B>(Remember
	 *            it is number of bytes)</B>
	 * @param byteStride
	 *            Number of bytes to skip between sets <B>(Remember it is number
	 *            of bytes)</B>
	 */
	public void bufferSubData(float[] data, int count, int byteSize,
			int byteOffset, int byteStride)
	{
		// bind buffer
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);

		// setup data to send
		FloatBuffer buffer = createBuffer(data);
		int sets = data.length / count;

		// send data using the stride based in
		for (int i = 0; i < sets; i++)
		{
			GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, byteOffset + i
					* byteStride, byteSize, buffer.position(i * count));
		}

		// unbind buffer
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		Logger.checkOGLError();
	}

	/**
	 * Draw this mesh. This method calls setup methods on the technique that is
	 * assigned to this mesh, and then draws.
	 * 
	 * @throws NullPointerException
	 *             if mode is null. It should never be able to be null at this
	 *             point.
	 */
	@Override
	public void draw()
	{
		// setup shader
		technique.useTechnique();
		technique.setColor(color);

		// bind texture
		(texture == null ? NO_TEXTURE : texture).bind();

		// bind vertex buffer, and ask technique to setup pointers
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);
		technique.setAttributePointers();

		// PLEASE NOTE:
		// Both DrawElements and DrawArrays count parameter take in the number
		// of indices. Because DrawElements is using the actual index array,
		// that is the size that is needed. DrawArrays on the other hand needs
		// to figure out how many indices there are by dividing out the number
		// of elements per vertex. This action is done every frame just in case
		// the technique is changed on the fly and uses a different element
		// size than the originally bound technique.

		// draw using indices or normal array
		if (indexDrawing)
		{
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vio);

			GLES20.glDrawElements(mode.getMode(), dataSize,
					GLES20.GL_UNSIGNED_SHORT, 0);

			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		else
		{
			int count = dataSize / technique.getElementsPerVertex();
			GLES20.glDrawArrays(mode.getMode(), 0, count);
		}

		// cleanup
		(texture == null ? NO_TEXTURE : texture).unbind();
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		Logger.checkOGLError();
	}

	/**
	 * All logic that needs to be ran every frame is put in this method. Use
	 * delta to update everything based on time instead of frame rate.
	 * 
	 * @param delta
	 *            Time since last update in seconds
	 */
	@Override
	public void update(float delta)
	{
		// Currently there is nothing that MUST be done for every Mesh
	}

	/**
	 * Get underlying color of this mesh (underlying meaning the color that is
	 * under the texture)
	 * 
	 * @return color of this mesh
	 */
	public AGEColor getColor()
	{
		return color;
	}

	/**
	 * Set the underlying color of this mesh (underlying meaning the color that
	 * is under the texture)
	 * 
	 * @param color
	 *            new color of this mesh
	 */
	public void setColor(AGEColor color)
	{
		this.color = color;
	}

	/**
	 * Get the texture this mesh uses
	 * 
	 * @return Texture of this mesh
	 */
	public Texture getTexture()
	{
		return texture;
	}

	/**
	 * Set the texture of this mesh. If null is passed in then the NO_TEXTURE
	 * texture will be bound before drawing.
	 * 
	 * @param texture
	 *            New texture to use on this mesh
	 */
	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}

	/**
	 * Get the technique that this mesh uses to draw
	 * 
	 * @return Technique this mesh uses
	 */
	public Technique getTechnique()
	{
		return technique;
	}

	/**
	 * Set what technique this mesh uses to draw
	 * 
	 * @param technique
	 *            New technique to use
	 */
	public void setTechnique(Technique technique)
	{
		if (technique == null)
			throw new NullPointerException(
					"Cannot set a Mesh's technique to null.");

		this.technique = technique;
	}

	/**
	 * Delete this mesh and take its data out of OpenGL. If it is known this
	 * mesh is no longer going to be used (or if it is about to go out of scope)
	 * this method should be called to open up resources.
	 */
	public void delete()
	{
		int[] b;

		// if using index drawing make sure to delete index buffer too
		if (indexDrawing)
		{
			b = new int[] { vbo, vio };
			GLES20.glDeleteBuffers(2, b, 0);
		}
		else
		{
			b = new int[] { vbo };
			GLES20.glDeleteBuffers(1, b, 0);
		}

		Logger.checkOGLError();
	}

	/**
	 * Sets the DrawMode for this Mesh
	 * 
	 * @param mode
	 *            The DrawMode required for drawing this Mesh
	 */
	private void setMode(DrawMode mode)
	{
		if (mode == null)
			throw new NullPointerException("DrawMode cannot be null");
		this.mode = mode;

	}

	/**
	 * Push all of the data from an array into a buffer. Used to pass data to
	 * OpenGL
	 * 
	 * @param array
	 *            data being added to buffer
	 * @return Buffer filled with data
	 */
	private FloatBuffer createBuffer(float[] floats)
	{
		FloatBuffer buffer = ByteBuffer
				.allocateDirect(floats.length * SIZE_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer().put(floats);
		buffer.rewind();
		return buffer;
	}

	/**
	 * Push all of the data from an array into a buffer. Used to pass data to
	 * OpenGL
	 * 
	 * @param array
	 *            data being added to buffer
	 * @return Buffer filled with data
	 */
	private ShortBuffer createBuffer(short[] shorts)
	{
		ShortBuffer buffer = ByteBuffer
				.allocateDirect(shorts.length * SIZE_SHORT)
				.order(ByteOrder.nativeOrder()).asShortBuffer().put(shorts);
		buffer.rewind();
		return buffer;
	}

	/**
	 * Create a handle to an OpenGL buffer.
	 * 
	 * @return Handle to buffer.
	 */
	private int genBuffer()
	{
		int[] b = new int[1];
		GLES20.glGenBuffers(1, b, 0);
		return b[0];
	}

}
