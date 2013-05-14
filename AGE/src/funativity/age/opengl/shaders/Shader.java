package funativity.age.opengl.shaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import funativity.age.util.Logger;

import android.content.res.AssetManager;
import android.opengl.GLES20;

/**
 * Class that represents a shader file. This is not a full shader program, but
 * simply a shader file. Almost always will be either a Vertex, or Fragment
 * shader. Pass instances of this object to a ShaderProgram to create a full
 * Shader
 * 
 * @author riedla
 * 
 */
public class Shader
{
	public enum ShaderTypes
	{
		Vertex(GLES20.GL_VERTEX_SHADER), Fragment(GLES20.GL_FRAGMENT_SHADER);

		private int type;

		ShaderTypes(int type)
		{
			this.type = type;
		}

		public int getType()
		{
			return type;
		}
	}

	// OpenGL handle to shader
	private final int shaderID;

	// marks if this shader file has been compiled yet.
	private boolean compiled = false;

	/**
	 * Create a shader file of shaderType. Pass shaderCode to OpenGL to have it
	 * compiled. shaderType must be either SHADERTYPE_VERTEX or
	 * SHADERTYPE_FRAGMENT. ShaderCode must be valid GLSL code. In versions of
	 * android that support it, any GLSL compile errors will be reported.
	 * 
	 * @param shaderCode
	 *            code for this shader
	 * @param shaderType
	 *            type of shader being made
	 * 
	 * @throws NullPointerException
	 *                thrown if shader code is null or empty, or if there was an
	 *                OpenGL error creating a handle to the shader
	 */
	private Shader(String shaderCode, ShaderTypes type)
	{
		// if nothing goes wrong, this will stay true
		compiled = true;

		// make sure there is code to compile
		if (shaderCode == null || shaderCode.trim().isEmpty())
			throw new NullPointerException(
					"ShaderCode cannot be empty or null.");

		// create the opengl shader
		shaderID = GLES20.glCreateShader(type.getType());
		if (shaderID == 0)
			throw new NullPointerException("Failed to create shader handle");

		// get shader code to GPU
		GLES20.glShaderSource(shaderID, shaderCode);
		GLES20.glCompileShader(shaderID);

		// Get the compilation status.
		final int[] compileStatus = new int[1];
		GLES20.glGetShaderiv(shaderID, GLES20.GL_COMPILE_STATUS, compileStatus,
				0);

		// check if there was problems with compiling
		if (compileStatus[0] == GLES20.GL_FALSE)
		{
			// report error
			Logger.checkShaderError("Failed to compile shader", shaderID);

			// delete shader out of context
			deleteShader();
		}

		// check for any other opengl errors
		Logger.checkOGLError();
	}

	/**
	 * Get the opengl handle for this shader <BR>
	 * <BR>
	 * This is not a public method. There should not be a reason for someone to
	 * get access to this handle outside of this package. The only reason to use
	 * this method is to attach this shader to a program during a building
	 * process.
	 * 
	 * @return ID handle to this shader.
	 */
	int getShaderID()
	{
		return shaderID;
	}

	/**
	 * Check to see if this shader is compiled.
	 * 
	 * @return true if this shader is compiled
	 */
	public boolean isCompiled()
	{
		return compiled;
	}

	/**
	 * Delete this shader
	 */
	public void deleteShader()
	{
		GLES20.glDeleteShader(shaderID);
		compiled = false;
	}

	/**
	 * Read from a file and pull out the code to be able to be passed into the
	 * OpenGL context. <BR>
	 * <BR>
	 * This code implements #include functionality. Format for this feature is:<BR>
	 * #include "[path to included shader code]" <BR>
	 * <BR>
	 * parentheses are expected around file path. Path is relative to current
	 * file being read. Each include MUST be on its own line.
	 * 
	 * @param fileName
	 *            file path to shader code
	 * @param am
	 *            Assetmanager of current context
	 * @return code read from file
	 * @throws IOException
	 *             thrown for any IO errors reading from file
	 */
	private static String getCodeFromFile(String fileName, AssetManager am)
			throws IOException
	{
		// string where the final code will be located
		String shaderCode = "";

		// get the location in the file system of this file
		String directory = new File(fileName).getParentFile().getPath();

		// IO vars
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;

		try
		{
			// open file and create a reader
			is = am.open(fileName);
			isr = new InputStreamReader(is);
			reader = new BufferedReader(isr);

			// var for current line of code
			String line;

			// as long as there is more code to read
			while ((line = reader.readLine()) != null)
			{
				line = line.trim();
				String first = line.split(" ")[0];

				// if the first token in this line is an include, attached the
				// linked file
				if (first.equals("#include"))
				{
					// find the start of the file
					int quote = line.indexOf('"') + 1;

					// pull the file name out of the string
					String file = line.substring(quote, line.length() - 1);

					// paste code from file into this code
					shaderCode += getCodeFromFile(directory + "/" + file, am)
							+ "\n";
				}
				else
					// if not an include line, simply add the next line to the
					// code
					shaderCode += line + "\n";
			}
		}
		finally
		{
			// File IO cleanup
			if (reader != null)
				reader.close();
			if (isr != null)
				isr.close();
			if (is != null)
				is.close();
		}

		// give back code pulled from file
		return shaderCode;
	}

	/**
	 * Create a shader file of shaderType. Pass shaderCode to OpenGL to have it
	 * compiled. shaderType must be either SHADERTYPE_VERTEX or
	 * SHADERTYPE_FRAGMENT. Also shaderCode cannot be null.
	 * 
	 * @param shaderCode
	 *            code for shader
	 * @param shaderType
	 *            type of shader being made
	 * 
	 * @exception NullPointerException
	 *                thrown if shader code is null or empty
	 * @exception NullPointerException
	 *                thrown if there was an error creating a handle to the
	 *                shader
	 * @exception RuntimeException
	 *                thrown if there was compile errors in the shader code
	 */
	public static Shader loadShaderFromCode(String shaderCode, ShaderTypes type)
	{
		return new Shader(shaderCode, type);
	}

	/**
	 * Create a shader file of shaderType. Pass shaderCode from a file to OpenGL
	 * to have it compiled. shaderType must be either SHADERTYPE_VERTEX or
	 * SHADERTYPE_FRAGMENT.
	 * 
	 * @param fileName
	 *            file pointing to shader code
	 * @param shaderType
	 *            type of shader being made
	 * @param am
	 *            AssetManager of context
	 * 
	 * @exception NullPointerException
	 *                thrown if shader code is null or empty
	 * @exception NullPointerException
	 *                thrown if there was an error creating a handle to the
	 *                shader
	 */
	public static Shader loadShaderFromFile(String fileName, ShaderTypes type,
			AssetManager am)
	{
		String shaderCode = "";

		try
		{
			// attempt to pull code out of file
			shaderCode = getCodeFromFile(fileName, am);
		}
		catch (Exception e)
		{
			Logger.e("Error reading shader from file " + fileName, e);
		}

		Logger.i("Loaded shader code from " + fileName);

		// build shader code, and return shader object
		return new Shader(shaderCode, type);
	}

}
