package funativity.age.opengl.meshloader;


/**
 * This class is a data object used to hold parsed in data pertaining to a 3D
 * mesh.
 * 
 */
public class MeshData
{

	/**
	 * MeshDataPoint[] which contains the list of points as defined by the Mesh
	 * faces in OBJ
	 */
	private MeshDataPoint[] meshDataArray = null;


	/**
	 * Basic constructor for the MeshData. All data is necessary.
	 * 
	 * @param verts
	 *            float[] containing the vertex data
	 * @param texCoords
	 *            float[] holding raw texture coordinate data
	 * @param norms
	 *            float[] holding raw normal data
	 * @param faces
	 *            short[] holding raw face data
	 * @param meshDataArray
	 *            MeshDataPoint[] containing the data points for the mesh as
	 *            defined by the parsed file
	 */
	public MeshData(MeshDataPoint[] meshDataArray)
	{
		this.meshDataArray = meshDataArray;
	}

	/**
	 * Gets the array of MeshDataPoints in the order defined by the parsed file
	 * 
	 * @return MeshDataPoint[] containing all necessary data to render the mesh
	 */
	public MeshDataPoint[] getPointData()
	{
		return meshDataArray;
	}

}
