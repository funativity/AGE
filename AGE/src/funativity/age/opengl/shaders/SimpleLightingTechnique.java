package funativity.age.opengl.shaders;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.MM;
import funativity.age.opengl.Mesh;
import funativity.age.util.Geometry3f;

/**
 * Technique used for basic lighting. Uses ambient and directional lighting. It
 * is important that setDirectionalLight is called at least once before drawing,
 * otherwise everything drawn with this technique will be black. <BR>
 * <BR>
 * This technique is a singleton, and calling
 * SimpleLightingTechnique.getTechnique.bindTechnique(); will bind this
 * technique, and make it the technique that is being used to draw.
 * 
 * @author riedla
 * 
 */
public class SimpleLightingTechnique extends Technique
{
	// singleton instance of this class
	private static SimpleLightingTechnique technique;

	// elements per parameter
	public static final int ELEMENT_COUNT_POSITION = 3;
	public static final int ELEMENT_COUNT_TEXTURE = 2;
	public static final int ELEMENT_COUNT_NORMAL = 3;

	// number of elements in a vertex
	public static final int ELEMENT_COUNT = ELEMENT_COUNT_POSITION
			+ ELEMENT_COUNT_TEXTURE + ELEMENT_COUNT_NORMAL;

	// bytes per parameter
	public static final int BYTE_COUNT_POSITION = ELEMENT_COUNT_POSITION
			* Mesh.SIZE_FLOAT;
	public static final int BYTE_COUNT_TEXTURE = ELEMENT_COUNT_TEXTURE
			* Mesh.SIZE_FLOAT;
	public static final int BYTE_COUNT_NORMAL = ELEMENT_COUNT_NORMAL
			* Mesh.SIZE_FLOAT;

	// byte offset per parameter
	public static final int BYTE_OFFSET_POSITION = 0;
	public static final int BYTE_OFFSET_TEXTURE = BYTE_OFFSET_POSITION
			+ BYTE_COUNT_POSITION;
	public static final int BYTE_OFFSET_NORMAL = BYTE_OFFSET_TEXTURE
			+ BYTE_COUNT_TEXTURE;

	// size of a vertex in bytes
	public static final int STRIDE = BYTE_COUNT_POSITION + BYTE_COUNT_TEXTURE
			+ BYTE_COUNT_NORMAL;

	// attribues
	private final int vertexLocation;
	private final int textureLocation;
	private final int normalLocation;

	// uniforms
	private final int mvpMatrixLocation;
	private final int nMatrixLocation;
	private final int colorLocation;
	private final int samplerLocation;

	// directional lighting vars
	private final int dirColorLocation;
	private final int dirAmbientLocation;
	private final int dirStrengthLocation;
	private final int dirDirectionLocation;

	/**
	 * Private constructor to ensure singleton. Builds shader program, and sets
	 * up shader variable locations.
	 */
	private SimpleLightingTechnique()
	{
		super();

		// attributes
		vertexLocation = this.getShaderProgram().getAttributeLocation(
				"inVertex");
		textureLocation = this.getShaderProgram().getAttributeLocation(
				"inTexCoord");
		normalLocation = this.getShaderProgram().getAttributeLocation(
				"inNormal");

		// uniforms
		mvpMatrixLocation = this.getShaderProgram().getUniformLocation(
				"MVPMatrix");
		nMatrixLocation = this.getShaderProgram().getUniformLocation("NMatrix");
		colorLocation = this.getShaderProgram().getUniformLocation("color");
		samplerLocation = this.getShaderProgram().getUniformLocation("sampler");

		// directional light
		dirColorLocation = getShaderProgram().getUniformLocation(
				"dirLight.color");
		dirAmbientLocation = getShaderProgram().getUniformLocation(
				"dirLight.ambient");
		dirStrengthLocation = getShaderProgram().getUniformLocation(
				"dirLight.strength");
		dirDirectionLocation = getShaderProgram().getUniformLocation(
				"dirLight.direction");
	}

	/**
	 * Get the singleton version of the SimpleLightingTechnique class. This call
	 * MUST be done on the OpenGL call because it creates the shader code on the
	 * first time it is called, which can only be done on the OpenGL thread.
	 * 
	 * @return singleton instance of this class.
	 */
	public static SimpleLightingTechnique getTechnique()
	{
		if (technique == null)
		{
			technique = new SimpleLightingTechnique();
		}

		return technique;
	}

	@Override
	protected Shader[] getShaderFiles(Context context)
	{
		Shader vertex = Shader.loadShaderFromCode(vertexCode,
				Shader.ShaderTypes.Vertex);
		Shader fragment = Shader.loadShaderFromCode(
				CommonCode.FRAGMENT_DIRECTIONALLIGHTING,
				Shader.ShaderTypes.Fragment);

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

	/**
	 * Pass normal matrix to this technique's shader. This method takes the
	 * model matrix, and converts it into what is needed as the normal matrix
	 * before passing the data to the GPU.
	 * 
	 * @param MMatrix
	 *            16 element float array representing the model matrix
	 */
	public void setNMatrix(float[] MMatrix)
	{
		float[] inv = new float[16];
		float[] nMatrix = new float[16];
		Matrix.invertM(inv, 0, MMatrix, 0);
		Matrix.transposeM(nMatrix, 0, inv, 0);

		// normal matrix is the inverse/transpose of the model matrix
		getShaderProgram().setUniformMatrix4f(nMatrixLocation, nMatrix);
	}

	/**
	 * Setup the directional lighting variables.
	 * 
	 * @param color
	 *            Color of the light
	 * @param ambient
	 *            How much ambient light is available (light that is everywhere)
	 * @param strength
	 *            How strong the directional light is
	 * @param direction
	 *            Direction of this light
	 */
	public void setDirectionalLight(AGEColor color, float ambient,
			float strength, Geometry3f direction)
	{
		this.getShaderProgram().setUniform3f(dirColorLocation, color.getRed(),
				color.getGreen(), color.getBlue());
		this.getShaderProgram().setUniform1f(dirAmbientLocation, ambient);
		this.getShaderProgram().setUniform1f(dirStrengthLocation, strength);

		direction.normalize();
		this.getShaderProgram().setUniform3f(dirDirectionLocation,
				direction.getX(), direction.getY(), direction.getZ());
	}

	@Override
	protected void setShaderUniforms()
	{
		setMVPMatrix(MM.getMVPMatrix());
		setNMatrix(MM.getMMatrix());
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

		// set normal attribute
		setAttributePointer(normalLocation, ELEMENT_COUNT_NORMAL,
				BYTE_OFFSET_NORMAL, STRIDE, GLES20.GL_FLOAT);
	}

	@Override
	public int getElementsPerVertex()
	{
		return ELEMENT_COUNT;
	}

	//@formatter:off
	private final String vertexCode = "" +
		"attribute vec3 inVertex;									\n" +
		"attribute vec2 inTexCoord;									\n" +
		"attribute vec3 inNormal;									\n" +
		"" +
		"varying vec2 texCoord;										\n" +
		"varying vec3 normal;										\n" +
		"" +
		"uniform mat4 MVPMatrix;									\n" +
		"uniform mat4 NMatrix;										\n" +
		"" +
		"void main()												\n" +
		"{															\n" +
		"	gl_Position = MVPMatrix * vec4( inVertex, 1.0 );		\n" +
		"" +		
		"	texCoord = inTexCoord;									\n" +
		"	normal = ( NMatrix * vec4( inNormal, 0.0 ) ).xyz;		\n" +
		"}";
	
	//@formatter:on

}
