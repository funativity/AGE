package funativity.age.opengl.shaders;

import android.content.Context;
import android.opengl.GLES20;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.MM;
import funativity.age.opengl.Mesh;


/**
 * Very basic technique that has a shader that simply takes vertices and texture
 * coordinates and does basic matrix multiplication to adjust the vertices into
 * the correct location. This is a singleton class.
 * 
 * @author riedla
 * 
 */
public class SimpleTechnique extends Technique
{
	// singleton instance of this class
	private static SimpleTechnique technique;

	// elements per parameter
	public static final int ELEMENT_COUNT_POSITION = 3;
	public static final int ELEMENT_COUNT_TEXTURE = 2;

	// number of elements in a vertex
	public static final int ELEMENT_COUNT = ELEMENT_COUNT_POSITION
			+ ELEMENT_COUNT_TEXTURE;

	// bytes per parameter
	public static final int BYTE_COUNT_POSITION = ELEMENT_COUNT_POSITION
			* Mesh.SIZE_FLOAT;
	public static final int BYTE_COUNT_TEXTURE = ELEMENT_COUNT_TEXTURE
			* Mesh.SIZE_FLOAT;

	// byte offset per parameter
	public static final int BYTE_OFFSET_POSITION = 0;
	public static final int BYTE_OFFSET_TEXTURE = BYTE_OFFSET_POSITION
			+ BYTE_COUNT_POSITION;

	// size of a vertex in bytes
	public static final int STRIDE = BYTE_COUNT_POSITION + BYTE_COUNT_TEXTURE;

	// attribues
	private final int vertexLocation;
	private final int textureLocation;

	// uniforms
	private final int mvpMatrixLocation;
	private final int colorLocation;
	private final int samplerLocation;

	/**
	 * Private constructor to ensure singleton. Builds shader program, and sets
	 * up shader variable locations.
	 */
	protected SimpleTechnique()
	{
		// link the shader
		super();

		// attributes
		vertexLocation = this.getShaderProgram().getAttributeLocation(
				"inVertex");
		textureLocation = this.getShaderProgram().getAttributeLocation(
				"inTexCoord");

		// uniforms
		mvpMatrixLocation = this.getShaderProgram().getUniformLocation(
				"MVPMatrix");
		colorLocation = this.getShaderProgram().getUniformLocation("color");
		samplerLocation = this.getShaderProgram().getUniformLocation("sampler");

	}

	/**
	 * Get the singleton version of the SimpleTechnique class. This call MUST be
	 * done on the OpenGL call because it creates the shader code on the first
	 * time it is called, which can only be done on the OpenGL thread.
	 * 
	 * @return singleton instance of this class.
	 */
	public static SimpleTechnique getTechnique()
	{
		if (technique == null)
		{
			technique = new SimpleTechnique();
		}

		return technique;
	}

	@Override
	protected Shader[] getShaderFiles(Context context)
	{
		// hard coded to guarantee that it will be found (no IO errors can occur
		// from this)
		//@formatter:off
		String vertexCode = "" +
				"attribute vec3 inVertex;" + "\n" +
				"attribute vec2 inTexCoord;" + "\n" +
				"" + "\n" +
				"varying vec2 texCoord;" + "\n" +
				"" + "\n" +
				"uniform mat4 MVPMatrix;" + "\n" +
				"" + "\n" +
				"void main()" + "\n" +
				"{" + "\n" +
				"	gl_Position = MVPMatrix * vec4( inVertex, 1.0 );" + "\n" +
				"	texCoord = inTexCoord;" + "\n" +
				"}";
		Shader vertex = Shader.loadShaderFromCode( vertexCode, Shader.ShaderTypes.Vertex );

		String fragmentCode = "" +
				"precision mediump float;" + "\n" +
				"" + "\n" +
				"varying vec2 texCoord;" + "\n" +
				"uniform vec3 color;" + "\n" +
				"uniform sampler2D sampler;" + "\n" +
				"" + "\n" +
				"void main()" + "\n" +
				"{" + "\n" +
				"	gl_FragColor = texture2D( sampler, texCoord ) * vec4( color, 1.0 );" + "\n" +
				"}";
		Shader fragment = Shader.loadShaderFromCode( fragmentCode, Shader.ShaderTypes.Fragment );
		//@formatter:on

		return new Shader[] { vertex, fragment };
	}

	@Override
	public void setTextureSamplerIndex(int index)
	{
		getShaderProgram().setUniform1i(samplerLocation, index);
	}

	@Override
	public void setColor(AGEColor color)
	{
		getShaderProgram().setUniform3f(colorLocation, color.getR(),
				color.getG(), color.getB());
	}

	/**
	 * Pass specified matrix to this technique's shader as the
	 * modelViewProjection matrix
	 * 
	 * @param mvpMatrix
	 *            16 element float array representing the model view projection
	 *            matrix
	 */
	public void setMVPMatrix(float[] mvpMatrix)
	{
		getShaderProgram().setUniformMatrix4f(mvpMatrixLocation, mvpMatrix);
	}

	@Override
	protected void setShaderUniforms()
	{
		// send all needed uniform variables to shader
		setMVPMatrix(MM.getMVPMatrix());
	}

	@Override
	public void setAttributePointers()
	{
		// set vertex attribute
		setAttributePointer(vertexLocation, ELEMENT_COUNT_POSITION,
				BYTE_OFFSET_POSITION, STRIDE, GLES20.GL_FLOAT);

		// set texture attribute
		setAttributePointer(textureLocation, ELEMENT_COUNT_TEXTURE,
				BYTE_OFFSET_TEXTURE, STRIDE, GLES20.GL_FLOAT);
	}

	@Override
	public int getElementsPerVertex()
	{
		return ELEMENT_COUNT;
	}

}
