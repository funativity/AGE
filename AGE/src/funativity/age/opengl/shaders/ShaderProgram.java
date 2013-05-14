package funativity.age.opengl.shaders;

import funativity.age.util.Logger;
import android.opengl.GLES20;

/**
 * Class the represents a shader program. <B>Usually</B> consists of a vertex
 * and fragment shader. Shader programs are used by the graphics card to decide
 * how to draw data to the screen. <BR>
 * <BR>
 * <B>General Creation:</B><BR>
 * Shader v = Shader.loadShaderFromFile( V_FILENAME, Shader.SHADERTYPE_VERTEX,
 * assetManager);<BR>
 * Shader f = Shader.loadShaderFromFile( F_FILENAME, Shader.SHADERTYPE_FRAGMENT,
 * assetManager);<BR>
 * ShaderProgram sp = new ShaderProgram();<BR>
 * sp.attachShader( v );<BR>
 * sp.attachShader( f );<BR>
 * sp.linkProgram();<BR>
 * <BR>
 * <B>General Usage:</B><BR>
 * Use just before a draw call
 * 
 * sp.bindProgram();
 * 
 * 
 * 
 * @author riedla
 * 
 */
public class ShaderProgram
{
	// handle to shader program
	private final int programID;

	// is this program linked yet
	private boolean linked = false;

	/**
	 * Default constructor for a shader program. Creates an OpenGL handle for
	 * this program.
	 */
	public ShaderProgram()
	{
		programID = genProgramID();
	}

	/**
	 * Create a shader program using all of the provided shaders. Attaches each
	 * shader, then links this program. After linking, nothing is done with
	 * provided shaders. It is expected that their managment is handled by
	 * caller.
	 * 
	 * @param shaders
	 *            Array of shaders to link together to build this ShaderProgram
	 */
	public ShaderProgram(Shader[] shaders)
	{
		this();

		// add them all to the program
		for (int i = 0; i < shaders.length; i++)
		{
			attachShader(shaders[i]);
		}

		// link program
		linkProgram();

	}

	/**
	 * Create a shader program using all of the provided shaders. Attaches each
	 * shader, then links this program. Caller has option to detach, and delete
	 * shaders after program is linked. This is valuable if it is known after
	 * this program is linked the provided shaders will not be needed anymore.
	 * 
	 * @param shaders
	 *            Array of shaders to link together to build this ShaderProgram
	 * @param deleteShaders
	 *            If provided shaders should be detached, and deleted after this
	 *            program is created
	 */
	public ShaderProgram(Shader[] shaders, boolean deleteShaders)
	{
		this(shaders);

		if (deleteShaders)
		{
			// remove all shaders from program, and delete them
			for (int i = 0; i < shaders.length; i++)
			{
				detachShader(shaders[i]);
				shaders[i].deleteShader();
			}
		}
	}

	/**
	 * Get the handle to this shader program <BR>
	 * <BR>
	 * This is not a public method. There should not be a reason for someone to
	 * get access to this handle outside of this package.
	 * 
	 * @return Get the OpenGL handle for this program
	 */
	int getProgramID()
	{
		return programID;
	}

	/**
	 * Delete this program out of memory
	 */
	public void deleteProgram()
	{
		GLES20.glDeleteProgram(programID);
		linked = false;
	}

	/**
	 * Attach a shader to this shader program. If the shader is not compiled,
	 * the shader will not be added to this program.
	 * 
	 * @param shader
	 *            shader to attach to this program
	 */
	public void attachShader(Shader shader)
	{
		if (shader.isCompiled())
			GLES20.glAttachShader(programID, shader.getShaderID());
		else
			Logger.e("Cannot attached an uncompiled shader to a ShaderProgram.");

		Logger.checkOGLError();
	}

	/**
	 * Detach a shader from this program. This can be done to undo the effects
	 * of the attachShader() method.
	 * 
	 * @param shader
	 *            Shader to remove from this program
	 */
	public void detachShader(Shader shader)
	{
		GLES20.glDetachShader(programID, shader.getShaderID());
		Logger.checkOGLError();
	}

	/**
	 * Link attached shaders to this program. After this point no more shaders
	 * can be linked to this program.
	 */
	public void linkProgram()
	{
		// mark linked as true. Should stay true if nothing goes wrong
		linked = true;

		// link program
		GLES20.glLinkProgram(programID);

		// check to make sure program was linked
		final int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(programID, GLES20.GL_LINK_STATUS, linkStatus, 0);
		if (linkStatus[0] == GLES20.GL_FALSE)
		{
			Logger.checkShaderProgramError("Failed to link program", programID);

			// deletes shader, and marks linked to false
			deleteProgram();
		}

		// validate program
		GLES20.glValidateProgram(programID);

		// check the results of the validation
		final int[] validateStatus = new int[1];
		GLES20.glGetProgramiv(programID, GLES20.GL_VALIDATE_STATUS,
				validateStatus, 0);
		if (validateStatus[0] == GLES20.GL_FALSE)
		{
			Logger.checkShaderProgramError("Failed to validate program",
					programID);

			// deletes shader, and marks linked to false
			deleteProgram();
		}

		// check for any other opengl errors
		Logger.checkOGLError();
	}

	/**
	 * check if this program is linked.
	 * 
	 * @return true if this program is linked
	 */
	public boolean isLinked()
	{
		return linked;
	}

	/**
	 * Start using this shader program
	 */
	public void useProgram()
	{
		if (linked)
			GLES20.glUseProgram(programID);
		else
			Logger.e("Cannot use a ShaderProgram that is not linked.");
	}

	/**
	 * Generate a handle to a shader program
	 * 
	 * @return id to shader
	 */
	private static int genProgramID()
	{
		int handle = GLES20.glCreateProgram();

		if (handle == 0)
			Logger.e("Failed to generate handle to a ShaderProgram.");

		return handle;
	}

	// #region Shader variable location getters

	/**
	 * get the handle on specific shader variable from this shader program
	 * 
	 * @param uniformName
	 *            name of variable
	 */
	public int getUniformLocation(String uniformName)
	{
		final int location = GLES20
				.glGetUniformLocation(programID, uniformName);

		if (location == -1)
			Logger.e("Failed to find uniform: " + uniformName);

		return location;
	}

	/**
	 * get the handle on specific shader variable from this shader program
	 * 
	 * @param attributeName
	 *            name of variable
	 */
	public int getAttributeLocation(String attributeName)
	{
		final int location = GLES20.glGetAttribLocation(programID,
				attributeName);

		if (location == -1)
			Logger.e("Failed to find attribute: " + attributeName);

		return location;
	}

	// #endregion

	// #region Shader variable setters

	/**
	 * Set uniform at location using the provided float. This program must be
	 * bound for this to operate as expected.
	 * 
	 * @param location
	 *            shader variable location of uniform
	 * @param x
	 *            float being passed in
	 */
	public void setUniform1f(int location, float x)
	{
		GLES20.glUniform1f(location, x);
	}

	/**
	 * Set uniform at location using the 2 provided floats. This program must be
	 * bound for this to operate as expected.
	 * 
	 * @param location
	 *            shader variable location of uniform
	 * @param x
	 *            float 1
	 * @param y
	 *            float 2
	 */
	public void setUniform2f(int location, float x, float y)
	{
		GLES20.glUniform2f(location, x, y);
	}

	/**
	 * Set uniform at location using the 3 provided floats. This program must be
	 * bound for this to operate as expected.
	 * 
	 * @param location
	 *            shader variable location of uniform
	 * @param x
	 *            float 1
	 * @param y
	 *            float 2
	 * @param z
	 *            float 3
	 */
	public void setUniform3f(int location, float x, float y, float z)
	{
		GLES20.glUniform3f(location, x, y, z);
	}

	/**
	 * Set uniform at location using the 3 provided floats. This program must be
	 * bound for this to operate as expected.
	 * 
	 * @param location
	 *            shader variable location of uniform
	 * @param x
	 *            float 1
	 * @param y
	 *            float 2
	 * @param z
	 *            float 3
	 * @param w
	 *            float 4
	 */
	public void setUniform4f(int location, float x, float y, float z, float w)
	{
		GLES20.glUniform4f(location, x, y, z, w);
	}

	/**
	 * Set uniform 4x4 matrix at given location using provided float[]. Assumes
	 * the first 16 characters are the matrix. This program must be bound for
	 * this to operate as expected.
	 * 
	 * @param location
	 *            shader variable location of uniform
	 * @param matrix
	 *            matrix data
	 */
	public void setUniformMatrix4f(int location, float[] matrix)
	{
		setUniformMatrix4f(location, 1, matrix, 0);
	}

	/**
	 * Set uniform 4x4 matrix at given location using provided float[]. Count is
	 * the number of 4x4 matrices that are being passed in using the provided
	 * data. offset is the number of elements into the float[] to start. This
	 * program must be bound for this to operate as expected.
	 * 
	 * @param location
	 *            shader variable location of uniform
	 * @param count
	 *            number of 4x4 matrices
	 * @param matrix
	 *            matrix data
	 * @param offset
	 *            number of elements to skip
	 */
	public void setUniformMatrix4f(int location, int count, float[] matrix,
			int offset)
	{
		GLES20.glUniformMatrix4fv(location, count, false, matrix, offset);
	}

	/**
	 * Set uniform at location using the provided int. This program must be
	 * bound for this to operate as expected.
	 * 
	 * @param location
	 *            shader variable location of uniform
	 * @param x
	 *            int being passed in
	 */
	public void setUniform1i(int location, int x)
	{
		GLES20.glUniform1i(location, x);
	}

	/**
	 * Set uniform at location using the provided int. This program must be
	 * bound for this to operate as expected.
	 * 
	 * @param location
	 *            shader variable location of uniform
	 * @param x
	 *            int 1
	 * @param y
	 *            int 2
	 */
	public void setUniform2i(int location, int x, int y)
	{
		GLES20.glUniform2i(location, x, y);
	}

	/**
	 * Set uniform at location using the provided int. This program must be
	 * bound for this to operate as expected.
	 * 
	 * @param location
	 *            shader variable location of uniform
	 * @param x
	 *            int 1
	 * @param y
	 *            int 2
	 * @param z
	 *            int 3
	 */
	public void setUniform3i(int location, int x, int y, int z)
	{
		GLES20.glUniform3i(location, x, y, z);
	}

	/**
	 * Set uniform at location using the provided int. This program must be
	 * bound for this to operate as expected.
	 * 
	 * @param location
	 *            shader variable location of uniform
	 * @param x
	 *            int 1
	 * @param y
	 *            int 2
	 * @param z
	 *            int 3
	 * @param w
	 *            int 4
	 */
	public void setUniform4i(int location, int x, int y, int z, int w)
	{
		GLES20.glUniform4i(location, x, y, z, w);
	}

	// #endregion

}
