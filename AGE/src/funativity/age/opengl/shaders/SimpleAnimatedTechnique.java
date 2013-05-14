package funativity.age.opengl.shaders;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.MM;
import funativity.age.opengl.Mesh;
import funativity.age.util.Geometry3f;

/**
 * 
 * This technique is intended to be used for animated 3D models that use
 * keyframes for the animation. Does a linear interpolation on the data of the
 * current and next frame to create a smooth transition between frames. Current
 * location between frames is set using the setFrameBlend() method.
 * 
 * 
 */
public class SimpleAnimatedTechnique extends Technique
{
	// singleton technique
	private static SimpleAnimatedTechnique technique;

	// elements per parameter
	public static final int ELEMENT_COUNT_POSITION_1 = 3;
	public static final int ELEMENT_COUNT_TEXTURE_1 = 2;
	public static final int ELEMENT_COUNT_NORMAL_1 = 3;
	public static final int ELEMENT_COUNT_POSITION_2 = 3;
	public static final int ELEMENT_COUNT_NORMAL_2 = 3;

	// number of elements in a vertex
	public static final int ELEMENT_COUNT = ELEMENT_COUNT_POSITION_1
			+ ELEMENT_COUNT_TEXTURE_1 + ELEMENT_COUNT_NORMAL_1
			+ ELEMENT_COUNT_POSITION_2 + ELEMENT_COUNT_NORMAL_2;

	// bytes per parameter
	public static final int BYTE_COUNT_POSITION_1 = ELEMENT_COUNT_POSITION_1
			* Mesh.SIZE_FLOAT;
	public static final int BYTE_COUNT_TEXTURE_1 = ELEMENT_COUNT_TEXTURE_1
			* Mesh.SIZE_FLOAT;
	public static final int BYTE_COUNT_NORMAL_1 = ELEMENT_COUNT_NORMAL_1
			* Mesh.SIZE_FLOAT;
	public static final int BYTE_COUNT_POSITION_2 = ELEMENT_COUNT_POSITION_2
			* Mesh.SIZE_FLOAT;
	public static final int BYTE_COUNT_NORMAL_2 = ELEMENT_COUNT_NORMAL_2
			* Mesh.SIZE_FLOAT;

	// byte offset per parameter
	public static final int BYTE_OFFSET_POSITION_1 = 0;
	public static final int BYTE_OFFSET_TEXTURE_1 = BYTE_OFFSET_POSITION_1
			+ BYTE_COUNT_POSITION_1;
	public static final int BYTE_OFFSET_NORMAL_1 = BYTE_OFFSET_TEXTURE_1
			+ BYTE_COUNT_TEXTURE_1;
	public static final int BYTE_OFFSET_POSITION_2 = BYTE_OFFSET_NORMAL_1
			+ BYTE_COUNT_NORMAL_1;
	public static final int BYTE_OFFSET_NORMAL_2 = BYTE_OFFSET_POSITION_2
			+ BYTE_COUNT_POSITION_2;

	// size of a vertex in bytes
	public static final int STRIDE = BYTE_COUNT_POSITION_1 + BYTE_COUNT_TEXTURE_1
			+ BYTE_COUNT_NORMAL_1 + BYTE_COUNT_POSITION_2 + BYTE_COUNT_NORMAL_2;

	// attribues
	private final int vertexLocation1;
	private final int textureLocation1;
	private final int normalLocation1;
	private final int vertexLocation2;
	private final int normalLocation2;

	// uniforms
	private final int mvpMatrixLocation;
	private final int nMatrixLocation;
	private final int colorLocation;
	private final int samplerLocation;
	private final int frameBlendLocation;

	// directional lighting vars
	private final int dirColorLocation;
	private final int dirAmbientLocation;
	private final int dirStrengthLocation;
	private final int dirDirectionLocation;

	/**
	 * Private constructor to keep this a singleton. Creates the shader program,
	 * and collects the locations of the variables in the program.
	 */
	private SimpleAnimatedTechnique()
	{
		super();

		// attributes
		vertexLocation1 = this.getShaderProgram().getAttributeLocation(
				"inVertex1");
		textureLocation1 = this.getShaderProgram().getAttributeLocation(
				"inTexCoord1");
		normalLocation1 = this.getShaderProgram().getAttributeLocation(
				"inNormal1");
		vertexLocation2 = this.getShaderProgram().getAttributeLocation(
				"inVertex2");
		normalLocation2 = this.getShaderProgram().getAttributeLocation(
				"inNormal2");

		// uniforms
		mvpMatrixLocation = this.getShaderProgram().getUniformLocation(
				"MVPMatrix");
		nMatrixLocation = this.getShaderProgram().getUniformLocation("NMatrix");
		colorLocation = this.getShaderProgram().getUniformLocation("color");
		samplerLocation = this.getShaderProgram().getUniformLocation("sampler");
		frameBlendLocation = this.getShaderProgram().getUniformLocation(
				"keyframeBlend");

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
	 * Get the singleton instance of SimpleAnimatedTechnique. If the technique
	 * is not created when this is called, it is created then. Should only be
	 * called on the OpenGL thread because of this.
	 * 
	 * @return SimpleAnimatedTechnique instance
	 */
	public static SimpleAnimatedTechnique getTechnique()
	{
		if (technique == null)
		{
			technique = new SimpleAnimatedTechnique();
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
	protected void setShaderUniforms()
	{
		setMVPMatrix(MM.getMVPMatrix());
		setNMatrix(MM.getMMatrix());
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
	 * Set how much to blend between the current frame and the next frame.
	 * Expected value is between 0, and 1. 0 being 100% current frame, 1 being
	 * 100% next frame. Values outside of these bounds have undefined results.
	 * 
	 * @param blend
	 *            blending factor
	 */
	public void setFrameBlend(float blend)
	{
		getShaderProgram().setUniform1f(frameBlendLocation, blend);
	}

	/**
	 * Pass specified matrix to this technique's shader as the
	 * modelViewProjection matrix
	 * 
	 * @param mvpMatrix
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
	public void setAttributePointers()
	{
		// set vertex attribute
		setAttributePointer(vertexLocation1, ELEMENT_COUNT_POSITION_1,
				BYTE_OFFSET_POSITION_1, STRIDE, GLES20.GL_FLOAT);

		// set texture attribute
		setAttributePointer(textureLocation1, ELEMENT_COUNT_TEXTURE_1,
				BYTE_OFFSET_TEXTURE_1, STRIDE, GLES20.GL_FLOAT);

		// set normal attribute
		setAttributePointer(normalLocation1, ELEMENT_COUNT_NORMAL_1,
				BYTE_OFFSET_NORMAL_1, STRIDE, GLES20.GL_FLOAT);

		setAttributePointer(vertexLocation2, ELEMENT_COUNT_POSITION_2,
				BYTE_OFFSET_POSITION_2, STRIDE, GLES20.GL_FLOAT);

		setAttributePointer(normalLocation2, ELEMENT_COUNT_NORMAL_2,
				BYTE_OFFSET_NORMAL_2, STRIDE, GLES20.GL_FLOAT);
	}

	@Override
	public int getElementsPerVertex()
	{
		return ELEMENT_COUNT;
	}

	//@formatter:off
	private final String vertexCode = "" +
		"attribute vec3 inVertex1;									\n" +
		"attribute vec2 inTexCoord1;								\n" +
		"attribute vec3 inNormal1;									\n" +
		"attribute vec3 inVertex2;									\n" + 
		"attribute vec3 inNormal2;									\n" +
		"															\n" +
		"varying vec2 texCoord;										\n" +
		"varying vec3 normal;										\n" +
		"															\n" +
		"uniform mat4 MVPMatrix;									\n" +
		"uniform mat4 NMatrix;										\n" +
		"uniform float keyframeBlend;								\n" +
		"															\n" +
		"void main()												\n" +
		"{															\n" +
		"	vec3 pos = mix( inVertex1, inVertex2, keyframeBlend );	\n" +
		"	gl_Position = MVPMatrix * vec4( pos, 1.0 );				\n" +
		"															\n" +
		"	texCoord = inTexCoord1;									\n" +
		"															\n" +
		"	vec3 norm = mix( inNormal1, inNormal2, keyframeBlend );	\n" +
		"	norm = normalize( norm );								\n" +
		"	normal = ( NMatrix * vec4( norm, 0.0 ) ).xyz;			\n" +
		"}";
	//@formatter:on
}
