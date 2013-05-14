package funativity.age.opengl.meshloader;

import java.io.InputStream;

import funativity.age.error.OversizedMeshException;

/**
 * Interface for the Mesh FileParser
 * 
 * @author wittem
 * 
 */
public interface Parser
{

	/**
	 * Parses the given file to create a mesh as defined by the information
	 * contained within the file.
	 * 
	 * @param is
	 *            InputStream containing any data pertaining to the mesh
	 * @return A completed Mesh for use in OpenGL rendering
	 * @throws OversizedMeshException
	 *             Thrown if the OBJ file contains too many faces. Upper bounds
	 *             is the highest Short value
	 */
	public MeshData parseMesh(InputStream is) throws OversizedMeshException;
}
