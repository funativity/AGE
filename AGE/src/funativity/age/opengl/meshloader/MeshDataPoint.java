package funativity.age.opengl.meshloader;

/**
 * This class is used to hold data that was parsed in for a 3D mesh.
 * Specifically, this is used to house the data in an orderly fasion
 * so that it can be passed down to OpenGL in a clean manner.
 */
public class MeshDataPoint
{
	/** Vertex 1 */
	public float v1;
	
	/** Vertex 2 */
	public float v2;
	
	/** Vertex 3 */
	public float v3;
	
	/** Texture Coordinate 1 */
	public float t1;
	
	/** Texture Coordinate 1 */
	public float t2;
	
	/** Normal Vector point 1 */
	public float n1;
	
	/** Normal Vector point 2 */
	public float n2;
	
	/** Normal Vector point 3 */
	public float n3;
	
	/**
	 * Basic Constructor.  Defaults all values to 0.0f
	 */
	public MeshDataPoint()
	{
		v1 = v2 = v3 = t1 = t2 = n1 = n2 = n3 = 0.0f;
	}
}
