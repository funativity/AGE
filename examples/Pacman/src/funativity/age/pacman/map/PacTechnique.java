package funativity.age.pacman.map;

import android.content.Context;
import android.opengl.GLES20;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.MM;
import funativity.age.opengl.Mesh;
import funativity.age.opengl.shaders.Shader;
import funativity.age.opengl.shaders.Technique;

public class PacTechnique extends Technique
{
	private static PacTechnique technique;

	// elements per parameter
	public static final int ELEMENT_COUNT_POSITION = 2;
	public static final int ELEMENT_COUNT_SIZE = 1;

	// number of elements in a vertex
	public static final int ELEMENT_COUNT = ELEMENT_COUNT_POSITION
			+ ELEMENT_COUNT_SIZE;

	// bytes per parameter
	public static final int BYTE_COUNT_POSITION = ELEMENT_COUNT_POSITION
			* Mesh.SIZE_FLOAT;
	public static final int BYTE_COUNT_SIZE = ELEMENT_COUNT_SIZE
			* Mesh.SIZE_FLOAT;

	// byte offset per parameter
	public static final int BYTE_OFFSET_POSITION = 0;
	public static final int BYTE_OFFSET_SIZE = BYTE_OFFSET_POSITION
			+ BYTE_COUNT_POSITION;

	// size of a vertex in bytes
	public static final int STRIDE = BYTE_COUNT_POSITION + BYTE_COUNT_SIZE;

	// attribues
	private final int vertexLocation;
	private final int sizeLocation;

	// uniforms
	private final int mvpMatrixLocation;
	private final int colorLocation;

	private PacTechnique()
	{
		// link the shader
		super();

		// attributes
		vertexLocation = this.getShaderProgram().getAttributeLocation(
				"inVertex");
		sizeLocation = this.getShaderProgram().getAttributeLocation("inSize");

		// uniforms
		mvpMatrixLocation = this.getShaderProgram().getUniformLocation(
				"MVPMatrix");
		colorLocation = this.getShaderProgram().getUniformLocation("color");
	}

	public static PacTechnique getTechnique()
	{
		if (technique == null)
		{
			technique = new PacTechnique();
		}

		return technique;
	}

	@Override
	protected Shader[] getShaderFiles(Context context)
	{
		//@formatter:off
		String vertexCode = "" +
				"attribute vec2 inVertex;" + "\n" +
				"attribute float inSize;" + "\n" +
				"" + "\n" +
				"varying float size;" + "\n" +
				"" + "\n" +
				"uniform mat4 MVPMatrix;" + "\n" +
				"" + "\n" +
				"void main()" + "\n" +
				"{" + "\n" +
				"   gl_PointSize = inSize;" + "\n" +
				"   size = inSize;" + "\n" +
				"	gl_Position = MVPMatrix * vec4( inVertex, 0.0, 1.0 );" + "\n" +
				"}";
		Shader vertex = Shader.loadShaderFromCode( vertexCode, Shader.ShaderTypes.Vertex );

		String fragmentCode = "" +
				"precision mediump float;" + "\n" +
				"" + "\n" +
				"varying float size;" + "\n" +
				"" + "\n" +
				"uniform vec3 color;" + "\n" +
				"" + "\n" +
				"void main()" + "\n" +
				"{" + "\n" +
				"	float d = distance( gl_PointCoord, vec2( 0.5, 0.5 ) );" + "\n" +
				"	if( size <= 0.0 || d > 0.5 )" + "\n" +
				"		discard;" + "\n" +
				"	else" + "\n" +
				"		gl_FragColor = vec4( color, 1.0 );" + "\n" +
				"}";
		Shader fragment = Shader.loadShaderFromCode( fragmentCode, Shader.ShaderTypes.Fragment );
		//@formatter:on

		return new Shader[] { vertex, fragment };
	}

	@Override
	protected void setShaderUniforms()
	{
		setMVPMatrix(MM.getMVPMatrix());
	}

	@Override
	public void setAttributePointers()
	{
		// set vertex attribute
		setAttributePointer(vertexLocation, ELEMENT_COUNT_POSITION,
				BYTE_OFFSET_POSITION, STRIDE, GLES20.GL_FLOAT);

		// set texture attribute
		setAttributePointer(sizeLocation, ELEMENT_COUNT_SIZE, BYTE_OFFSET_SIZE,
				STRIDE, GLES20.GL_FLOAT);
	}

	@Override
	public void setTextureSamplerIndex(int index)
	{
		// do nothing
	}

	@Override
	public void setColor(AGEColor color)
	{
		getShaderProgram().setUniform3f(colorLocation, color.getR(),
				color.getG(), color.getB());
	}

	public void setMVPMatrix(float[] mvpMatrix)
	{
		getShaderProgram().setUniformMatrix4f(mvpMatrixLocation, mvpMatrix);
	}

	@Override
	public int getElementsPerVertex()
	{
		return ELEMENT_COUNT;
	}

}
