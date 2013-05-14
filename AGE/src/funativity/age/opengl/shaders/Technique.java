package funativity.age.opengl.shaders;

import java.util.ArrayList;

import android.content.Context;
import android.opengl.GLES20;
import funativity.age.opengl.AGEColor;
import funativity.age.util.Logger;

/**
 * Technique is bound to a specific type of program, and is implemented with a
 * certain goal in mind. Many techniques are singletons because there is no
 * point in using up all of the GPU memory with the same shader code, but there
 * is no requirement in having a technique a singleton.
 * 
 * @author riedla
 * 
 */
public abstract class Technique
{
	private static Context context;

	// a list of technique that have been registered. used to reload after a
	// loss of the opengl context
	private static final ArrayList<Technique> registeredTechniques = new ArrayList<Technique>();

	// handle to shader program that is used by this technique
	private ShaderProgram program;

	/**
	 * Default constructor. This constructor gets the shader files that make up
	 * this technique, and link them together into a shaderprogram. The newly
	 * created program will be bound when leaving this constructor.
	 */
	protected Technique()
	{
		loadProgram();
		registeredTechniques.add(this);

		program.useProgram();

		Logger.checkOGLError();
	}

	/**
	 * build the shader program for this technique. Called in the default
	 * constructor, but may be needed when the OpenGL context is lost.
	 */
	public void loadProgram()
	{
		if (program != null)
			program.deleteProgram();

		program = new ShaderProgram(getShaderFiles(context), true);
	}

	/**
	 * Get an array of the Shader files that make this technique up. This should
	 * only need to be called when this technique is being loaded
	 * 
	 * @return array of shaders that will be linked into a shader program
	 *         (expects all shaders to be compiled)
	 */
	protected abstract Shader[] getShaderFiles(Context context);

	/**
	 * Start using this technique. Calls this technique's method to set the
	 * uniform variables.
	 */
	public final void useTechnique()
	{
		program.useProgram();
		setShaderUniforms();

		Logger.checkOGLError();
	}

	/**
	 * Pass variables to the shader. This is called after the shaderProgram is
	 * bound, but before the vertex data is bound.
	 */
	protected abstract void setShaderUniforms();

	/**
	 * Setup the attribute pointers that this shader technique uses. This is one
	 * of the last calls that should be called before drawing.
	 */
	public abstract void setAttributePointers();

	/**
	 * Get the ShaderProgram that this technique uses
	 * 
	 * @return Shader Program used by this technique to draw
	 */
	public ShaderProgram getShaderProgram()
	{
		return program;
	}

	/**
	 * Set the location where textures will be bound to. Use values provided by
	 * the Texture method getTextureUnitIndex()
	 * 
	 * @param index
	 *            Index of texture to use
	 */
	public abstract void setTextureSamplerIndex(int index);

	/**
	 * Set the uniform variable to specified color
	 * 
	 * @param color
	 *            Set shader uniform color to specified color
	 */
	public abstract void setColor(AGEColor color);

	/**
	 * Enable and define a pointer for an attribute
	 * 
	 * @param location
	 *            Location of attribute in shader
	 * @param size
	 *            Number of elements this attribute expects (a vec3 attribute
	 *            expects size to be 3)
	 * @param byteOffset
	 *            offset into data this attribute starts <B>(As Number of
	 *            Bytes)</B>
	 * @param byteStride
	 *            space between the starting of sets of data <B>(As Number of
	 *            Bytes)</B>
	 * @param GLDataType
	 *            OpenGL data type this attribute is expecting (GL_FLOAT)
	 */
	protected void setAttributePointer(int location, int size, int byteOffset,
			int byteStride, int GLDataType)
	{
		GLES20.glEnableVertexAttribArray(location);
		GLES20.glVertexAttribPointer(location, size, GLDataType, false,
				byteStride, byteOffset);
	}

	/**
	 * Returns the number of elements that are used in each vertex (total number
	 * of attributes used in the shader)
	 * 
	 * @return Number of elements in each vertex
	 */
	public abstract int getElementsPerVertex();

	/**
	 * Reload techniques. Called after returning from losing the opengl context
	 */
	public static void reloadTechniques()
	{
		for (Technique technique : registeredTechniques)
		{
			technique.loadProgram();
		}
	}

	/**
	 * Helper method to give techniques a handle to the context
	 * 
	 * @param context
	 *            of the program.
	 */
	public static void setContext(Context context)
	{
		Technique.context = context;
	}

}
