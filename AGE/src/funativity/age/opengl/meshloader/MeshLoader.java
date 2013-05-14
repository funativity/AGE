package funativity.age.opengl.meshloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.res.AssetManager;
import android.content.res.Resources;
import funativity.age.error.OversizedMeshException;
import funativity.age.opengl.DrawMode;
import funativity.age.opengl.Mesh;
import funativity.age.opengl.animation.AnimatedModel;
import funativity.age.opengl.shaders.SimpleAnimatedTechnique;
import funativity.age.util.Logger;

/**
 * The MeshLoader is responsible for parsing in a file and creating a Mesh out
 * of the data contained. Along with being able to return a Mesh, the specific
 * data that was parsed out of the file is also stored.
 * 
 */
public class MeshLoader
{
	/**
	 * Represents the file type that the MeshLoader is to parse.
	 */
	public enum FileType
	{
		OBJ_FILE;
	}

	/** Number of vertices in each triangle face */
	public static final int VERTICES_PER_TRIANGLE = 3;

	/** Number of texture coordinates for each vertex */
	public static final int TEXTURE_COORDS_PER_VERTEX = 2;

	/** Number of normal vector data pieces for each vertex */
	public static final int NORMAL_DATA_PER_VERTEX = 3;

	/**
	 * Parses the given file using the FileType provided
	 * 
	 * @param type
	 *            FileType for the file
	 * @param is
	 *            InputStream containing any data pertaining to the mesh
	 * @return MeshData that as has been filled with the parsed data and a Mesh
	 * @throws OversizedMeshException
	 *             Thrown when the mesh contained in the file has too many faces
	 */
	private static MeshData parseFile(FileType type, InputStream is)
			throws OversizedMeshException
	{
		MeshData meshData = null;
		Parser parser = null;
		switch (type)
		{
			case OBJ_FILE:
			{
				parser = new OBJParser();
				break;
			}

			default:
			{
				break;
			}
		}

		if (parser != null)
		{
			meshData = parser.parseMesh(is);
		}

		return meshData;
	}

	/**
	 * Reads an asset into an InputStream using the Activity's AssetManager.
	 * 
	 * @param fileName
	 *            Path to the file based off the AssetManager
	 * @param am
	 *            AssetManager for the given Activity
	 * @return InputStream containing any data pertaining to the mesh, null if
	 *         the file failed to open
	 */
	public static InputStream openAsset(String fileName, AssetManager am)
	{
		try
		{
			return am.open(fileName);
		}
		catch (IOException e)
		{
			Logger.e("Failed to open file: " + fileName);
		}

		return null;
	}

	/**
	 * Reads a resource into an InputStream using the Activity's Resources.
	 * 
	 * @param resId
	 *            Resource ID
	 * @param res
	 *            Resources for the given Activity
	 * @return InputStream containing any data pertaining to the mesh, null if
	 *         the resource failed to open
	 */
	public static InputStream openResource(int resId, Resources res)
	{
		try
		{
			return res.openRawResource(resId);
		}
		catch (Resources.NotFoundException e)
		{
			Logger.e("Failed to open resource ID: " + resId);
		}

		return null;
	}

	/**
	 * Loads a Mesh out of the given file.
	 * 
	 * @param type
	 *            MeshLoader.FileType of the file being loaded
	 * @param is
	 *            InputStream containing any data pertaining to the mesh
	 * @return A Mesh as defined by the parsed file
	 * @throws OversizedMeshException
	 *             If the Mesh has too many faces to be properly handled.
	 */
	public static Mesh loadMesh(FileType type, InputStream is)
			throws OversizedMeshException
	{
		Mesh mesh = new Mesh();

		MeshData data = parseFile(type, is);

		MeshDataPoint[] meshPoints = data.getPointData();

		float[] dataArray = new float[meshPoints.length
				* (VERTICES_PER_TRIANGLE + TEXTURE_COORDS_PER_VERTEX + NORMAL_DATA_PER_VERTEX)];

		int index = 0;
		int dataIndex = 0;

		// Fill dataArry with point data
		while (dataIndex < dataArray.length)
		{
			// Fill vertex data
			dataArray[dataIndex++] = meshPoints[index].v1;
			dataArray[dataIndex++] = meshPoints[index].v2;
			dataArray[dataIndex++] = meshPoints[index].v3;

			// Fill texture data
			dataArray[dataIndex++] = meshPoints[index].t1;
			dataArray[dataIndex++] = meshPoints[index].t2;

			// Fille normal data
			dataArray[dataIndex++] = meshPoints[index].n1;
			dataArray[dataIndex++] = meshPoints[index].n2;
			dataArray[dataIndex++] = meshPoints[index].n3;

			index++;
		}

		// drawOrder is null to force ordered drawing on mesh creation
		mesh.initMesh(dataArray, null, DrawMode.GL_TRIANGLES);
		return mesh;
	}

	/**
	 * Load in an animation. The model that is loaded in must be a keyframe
	 * animation type.
	 * 
	 * @param path
	 *            path to animation files, relative to the assets folder
	 * @param animationName
	 *            Name of animation, excluding ending numbers. ie files are
	 *            'animation1.obj', 'animation2.obj' - pass in 'animation'
	 * @param type
	 *            FileType of files being parsed
	 * @param am
	 *            AssetManager
	 * @return AnimatedModel that represents an animation
	 * @throws IOException
	 * @throws OversizedMeshException
	 */
	public AnimatedModel loadAnimation(String path, String animationName,
			FileType type, AssetManager am) throws IOException,
			OversizedMeshException
	{
		// create the model that will be returned
		final AnimatedModel model = new AnimatedModel();

		// get all of the files that are part of this animation
		final ArrayList<String> files = listFilesMatching(path, animationName
				+ ".*", am);

		// if we dont have enough frames, we wont know how to combine the data.
		if (files.size() <= 1)
			throw new ExceptionInInitializerError(path + "/" + animationName
					+ " does not have enough frames for an animation");

		// create a list to hold the data for each frame
		final ArrayList<MeshData> data = new ArrayList<MeshData>();

		// parse each file that makes up this animation, and add it to the list
		for (String file : files)
		{
			InputStream is = openAsset(path + "/" + file, am);
			data.add(parseFile(type, is));
		}

		// get the stride of the animated data. used to convert normal data to
		// animated data
		final int animatedStride = SimpleAnimatedTechnique.getTechnique()
				.getElementsPerVertex();

		// loop through all frames, and combine the data
		for (int frame = 0; frame < data.size(); frame++)
		{
			// grab the data for the current, and next frame
			final MeshData current = data.get(frame);
			final MeshData next = data.get((frame + 1) % data.size());

			final MeshDataPoint[] currentPoints = current.getPointData();
			final MeshDataPoint[] nextPoints = next.getPointData();

			// create a spot to put the new data
			final int vertexCount = currentPoints.length;
			final float[] vertices = new float[animatedStride * vertexCount];

			// loop through each vertex of the frame, and combine it with the
			// next frame's data
			for (int i = 0; i < vertexCount; i++)
			{
				// find the spot in the new data to continue to add
				final int animatedIndex = i * animatedStride;

				// add current frame's vertices to animated frame
				vertices[animatedIndex + 0] = currentPoints[i].v1;
				vertices[animatedIndex + 1] = currentPoints[i].v2;
				vertices[animatedIndex + 2] = currentPoints[i].v3;

				// add current texture coordinates
				vertices[animatedIndex + 3] = currentPoints[i].t1;
				vertices[animatedIndex + 4] = currentPoints[i].t2;

				// add current normals
				vertices[animatedIndex + 5] = currentPoints[i].n1;
				vertices[animatedIndex + 6] = currentPoints[i].n2;
				vertices[animatedIndex + 7] = currentPoints[i].n3;

				// add next vertices
				vertices[animatedIndex + 8] = nextPoints[i].v1;
				vertices[animatedIndex + 9] = nextPoints[i].v2;
				vertices[animatedIndex + 10] = nextPoints[i].v3;

				// add next normals
				vertices[animatedIndex + 11] = nextPoints[i].n1;
				vertices[animatedIndex + 12] = nextPoints[i].n2;
				vertices[animatedIndex + 13] = nextPoints[i].n3;
			}

			// at this point all of the data for the current frame should be
			// Constructed. Use this data to create a mesh, that will be
			// used as a frame of the animation
			Mesh newFrame = new Mesh();
			newFrame.initMesh(vertices, null, DrawMode.GL_TRIANGLES);
			model.addFrame(newFrame);
		}

		return model;
	}

	/**
	 * Helper method that uses regular expression to get files matching pattern
	 * 
	 * @param root
	 *            directory to look in
	 * @param regex
	 *            pattern to look for
	 * @return array of files that match pattern that are in root directory
	 * @throws IOException
	 */
	private static ArrayList<String> listFilesMatching(String root,
			String regex, AssetManager am) throws IOException
	{
		String[] files = am.list(root);
		ArrayList<String> rtn = new ArrayList<String>();

		for (int i = 0; i < files.length; i++)
		{
			if (files[i].matches(regex))
				rtn.add(files[i]);
		}

		return rtn;
	}
}
