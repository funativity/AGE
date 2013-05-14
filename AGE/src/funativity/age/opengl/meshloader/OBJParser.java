package funativity.age.opengl.meshloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import funativity.age.error.OversizedMeshException;

/**
 * The OBJParser is responsible for parsing through an OBJ file and making the
 * data that is contained more accessible.
 * 
 * @author wittem
 * 
 */
public class OBJParser implements Parser
{
	/** float[] containing every vertex in the OBJ file */
	private float[] rawVertices = null;

	/** float[] containing every texture coordinate data point in the OBJ file */
	private float[] rawTexCoords = null;

	/** float[] containing every normal data point in the OBJ file */
	private float[] rawNormals = null;

	/** short[] containing indices to the texture coordinates in the OBJ file */
	private short[] rawTexCoordOrder = null;

	/** short[] containing indices to the normal data in the OBJ file */
	private short[] rawNormalOrder = null;

	/**
	 * Array of MeshDataPoint to be filled with point data for passing to OpenGL
	 */
	private MeshDataPoint[] meshPoints = null;

	/** Input stream for the OBJ file */
	private InputStream is;

	/** RegularExpression used to parse through the file's face data */
	private String faceParseRegEx = "";

	/**
	 * This enum is used to hold internal reference to the type of face data
	 * this file contains.
	 * 
	 * SingleVert - Only vertex data is held VertTexture - Vertex/Texture data
	 * is held VertNormal - Vertex/Normal data is held VertTextureNorma -
	 * Vertex/Texture/Normal data is held
	 * 
	 */
	private enum FaceType
	{
		SingleVert, VertTexture, VertNormal, VertTextureNormal
	}

	/**
	 * Hold the enum for keeping track of the face data type that the OBJ file
	 * contains
	 */
	private FaceType faceType = null;

	/**
	 * Defines how far the BufferedReader should go before voiding the mark.
	 * This should be defined so that the mark is never hit
	 */
	private static final int MARK_LIMIT = Integer.MAX_VALUE;

	@Override
	public MeshData parseMesh(InputStream is) throws OversizedMeshException
	{
		this.is = is;

		return readFile();
	}

	/**
	 * Reads through the OBJ file line by line parsing out any relevant data.
	 * Currently we only support parsing the following data types.
	 * 
	 * Vertex, Normal, TextureCoordinate, Face.
	 * 
	 * @return MeshData MeshData object containing all of the relevant data that
	 *         was parsed from the file
	 * 
	 * @throws OversizedMeshException
	 *             Thrown if the OBJ file contains too many faces. Upper bounds
	 *             is the highest Short value
	 */
	public MeshData readFile() throws OversizedMeshException
	{
		BufferedReader reader = null;
		MeshData meshData = null;

		try
		{
			reader = new BufferedReader(new InputStreamReader(is));

			String data = "";
			reader.mark(MARK_LIMIT);

			// Parse through the entire file
			while ((data = reader.readLine()) != null)
			{
				String[] splitString = data.split(" ");
				String tag = splitString[0];
				if (tag.equals("v"))
				{
					reader.reset();
					rawVertices = extractVertices(reader);
				}
				else if (tag.equals("vt"))
				{
					reader.reset();
					rawTexCoords = extractTextureCoords(reader);
				}
				else if (tag.equals("vn"))
				{
					reader.reset();
					rawNormals = extractNormals(reader);
				}
				else if (tag.equals("f"))
				{
					reader.reset();
					extractFaces(reader);
				}
				reader.mark(MARK_LIMIT);
			} // End while

			// Fill the mesh data
			meshData = new MeshData(meshPoints);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return meshData;
	}

	/**
	 * Parses the section of the OBJ file pertaining to the vertex information
	 * into a float[].
	 * 
	 * @param reader
	 *            The BufferedReader set up for reading in the OBJ file
	 * @return float[] containing each piece of vertex data. Such that the first
	 *         vertex encountered is v1. float[0] would be v1.x, float[1] = v1.y
	 *         ... etc.
	 * @throws IOException
	 *             Thrown if an issue is encountered with reading the file
	 */
	protected float[] extractVertices(BufferedReader reader) throws IOException
	{
		String data = "";
		String temp = "";

		// Initial parse List
		ArrayList<String> verticesList = new ArrayList<String>();

		// When the vertex section of the file is determined to be completed,
		// this should
		// be flagged true
		boolean proceedToNextSection = false;

		reader.mark(MARK_LIMIT);

		// Parses vertex data into the format of [ xf, yf, zf ]
		while ((data = reader.readLine()) != null && !proceedToNextSection)
		{
			String[] splitString = data.split(" ");
			if (splitString[0].equals("v"))
			{
				for (int i = 1; i < splitString.length; i++)
				{
					temp = temp.concat(splitString[i] + "f,");
				}
				verticesList.add(temp);
				temp = "";
				reader.mark(MARK_LIMIT);
			}
			else
			{
				reader.reset();
				proceedToNextSection = true;
			}
		}

		float[] verticesArray = new float[verticesList.size()
				* MeshLoader.VERTICES_PER_TRIANGLE];

		// Parses the String Array into single entries
		int index = 0;
		for (String vertex : verticesList)
		{
			String[] points = vertex.split(",");
			verticesArray[index] = Float.valueOf(points[0]);
			index++;
			verticesArray[index] = Float.valueOf(points[1]);
			index++;
			verticesArray[index] = Float.valueOf(points[2]);
			index++;
		}

		reader.reset();

		return verticesArray;
	}

	/**
	 * Parses the section of the OBJ file pertaining to the texture coordinate
	 * information into an array of floats.
	 * 
	 * @param reader
	 *            The BufferedReader set up for reading in the OBJ file
	 * @return float[] in such a format that float[0] = t1.u, float[1] = t1.v
	 *         ... etc.
	 * 
	 * @throws IOException
	 *             Thrown if an issue is encountered with reading the file
	 */
	protected float[] extractTextureCoords(BufferedReader reader)
			throws IOException
	{
		String data = "";
		String temp = "";

		// Initial List of texture coordinate data
		ArrayList<String> textureCoordsList = new ArrayList<String>();

		// When the vertex section of the file is determined to be completed,
		// this should
		// be flagged true
		boolean proceedToNextSection = false;

		reader.mark(MARK_LIMIT);

		// Parses the data into the format of [u, v]
		while (((data = reader.readLine()) != null) && !proceedToNextSection)
		{

			String[] splitString = data.split(" ");
			if (splitString[0].equals("vt"))
			{
				for (int i = 1; i < splitString.length; i++)
				{
					temp = temp.concat(splitString[i] + "f,");
				}
				textureCoordsList.add(temp);
				temp = "";
				reader.mark(MARK_LIMIT);
			}
			else
			{
				reader.reset();
				proceedToNextSection = true;
			}
		}

		float[] texCoordsArray = new float[textureCoordsList.size()
				* MeshLoader.TEXTURE_COORDS_PER_VERTEX];

		// Parses the String Array into single entries
		int index = 0;
		for (String texCoord : textureCoordsList)
		{
			String[] points = texCoord.split(",");
			texCoordsArray[index] = Float.valueOf(points[0]);
			index++;
			texCoordsArray[index] = Float.valueOf(points[1]);
			index++;
		}

		reader.reset();

		return texCoordsArray;
	}

	/**
	 * Parses the section of the OBJ file pertaining to the normal information
	 * into an array of floats.
	 * 
	 * @param reader
	 *            The BufferedReader set up for reading in the OBJ file
	 * @return float[] in such a format that float[0] = n1.x, float[1] = n1.y
	 *         ... etc.
	 * @throws IOException
	 *             Thrown if an issue is encountered with reading the file
	 */
	protected float[] extractNormals(BufferedReader reader) throws IOException
	{
		String data = "";
		String temp = "";

		// Initial List of normal data
		ArrayList<String> normalsList = new ArrayList<String>();

		// When the vertex section of the file is determined to be completed,
		// this should
		// be flagged true
		boolean proceedToNextSection = false;

		reader.mark(MARK_LIMIT);

		// Parse the data into the format [x, y, z]
		while ((data = reader.readLine()) != null && !proceedToNextSection)
		{

			String[] splitString = data.split(" ");
			if (splitString[0].equals("vn"))
			{
				for (int i = 1; i < splitString.length; i++)
				{
					temp = temp.concat(splitString[i] + "f,");
				}
				normalsList.add(temp);
				temp = "";
				reader.mark(MARK_LIMIT);
			}
			else
			{
				reader.reset();
				proceedToNextSection = true;
			}
		}

		float[] normalsArray = new float[normalsList.size()
				* MeshLoader.NORMAL_DATA_PER_VERTEX];

		// Parse the original String Array into individual floats.
		int index = 0;
		for (String normal : normalsList)
		{
			String[] points = normal.split(",");
			normalsArray[index] = Float.valueOf(points[0]);
			index++;
			normalsArray[index] = Float.valueOf(points[1]);
			index++;
			normalsArray[index] = Float.valueOf(points[2]);
			index++;
		}

		reader.reset();

		return normalsArray;
	}

	/**
	 * Parses the section of the OBJ file pertaining to the face information. In
	 * the process creates an array of all point data to be used when creating a
	 * mesh.
	 * 
	 * @param reader
	 *            The BufferedReader set up for reading in the OBJ file
	 * @return short[] parsed in such a way that each index points to a vertex.
	 *         Groups of 3 make a triangle.
	 * @throws IOException
	 *             Thrown if an issue is encountered with reading the file
	 * @throws OversizedMeshException
	 *             Thrown if the number of faces is too large
	 */
	protected short[] extractFaces(BufferedReader reader) throws IOException,
			OversizedMeshException
	{
		String data = "";
		String temp = "";

		// Original List of parsed data
		ArrayList<String> facesList = new ArrayList<String>();

		// When the vertex section of the file is determined to be completed,
		// this should
		// be flagged true
		boolean proceedToNextSection = false;

		// For knowing when to stop parsing for the RegEx.
		boolean isTypeFound = false;

		reader.mark(MARK_LIMIT);

		// Determine the type of face data we have
		// This is used when putting all of the data together.
		// We need to know how to interpret the face data when constructing
		// the float[] containing all of the data
		while ((data = reader.readLine()) != null && !isTypeFound)
		{
			String[] lineData = data.split(" ");
			if (lineData[0].equals("f"))
			{
				// Check the second group on the line
				// If it splits properly we now know what data format to use
				String[] vertData = lineData[1].split("//");
				if (vertData.length == 2)
				{
					isTypeFound = true;
					faceType = FaceType.VertNormal;
					faceParseRegEx = "//";
				}
				else
				{
					vertData = lineData[1].split("/");
					if (vertData.length == 3)
					{
						isTypeFound = true;
						faceType = FaceType.VertTextureNormal;
						faceParseRegEx = "/";
					}
					else if (vertData.length == 2)
					{
						isTypeFound = true;
						faceType = FaceType.VertTexture;
						faceParseRegEx = "/";
					}
					else
					{
						isTypeFound = true;
						faceType = FaceType.SingleVert;
						faceParseRegEx = " ";
					}
				}
			}
		} // End while loop

		reader.reset();

		// Parse the initial file into a List of the form (f1, v1/t1/n1,
		// v2/t2/n2, ... etc.)
		// Data reference indices.
		while ((data = reader.readLine()) != null && !proceedToNextSection)
		{
			String[] vertEntries = data.split(" ");
			if (vertEntries[0].equals("f"))
			{
				for (int i = 1; i < vertEntries.length; i++)
				{
					temp = temp.concat(vertEntries[i] + ",");
				}
				facesList.add(temp);
				temp = "";
				reader.mark(MARK_LIMIT);
			}
			else
			{
				reader.reset();
				proceedToNextSection = true;
			}
		}

		// We don't want to accidentally overflow the facesArray when we merge
		// all the data
		if (facesList.size() > Short.MAX_VALUE)
		{
			throw new OversizedMeshException();
		}

		// Populate draw order lists

		// Draw order for each data type
		short[] facesArray = new short[facesList.size()
				* MeshLoader.VERTICES_PER_TRIANGLE];
		rawTexCoordOrder = new short[facesList.size()
				* MeshLoader.VERTICES_PER_TRIANGLE];
		rawNormalOrder = new short[facesList.size()
				* MeshLoader.VERTICES_PER_TRIANGLE];

		// Create the MeshDataPoint holder to contain a list of each point
		meshPoints = new MeshDataPoint[facesArray.length];

		int index = 0;
		for (String face : facesList)
		{
			String[] vertices = face.split(",");
			for (int i = 0; i < vertices.length; i++)
			{
				// vertData contains data in the form of v#/t#/n# assuming the
				// data is present.
				String[] vertData = vertices[i].split(faceParseRegEx);
				facesArray[index] = Short.parseShort(vertData[0]);
				facesArray[index] -= 1;

				meshPoints[index] = new MeshDataPoint();

				// Fill vertex data for each point
				int vertIndex = facesArray[index]
						* MeshLoader.VERTICES_PER_TRIANGLE;
				meshPoints[index].v1 = rawVertices[vertIndex];
				meshPoints[index].v2 = rawVertices[vertIndex + 1];
				meshPoints[index].v3 = rawVertices[vertIndex + 2];

				switch (faceType)
				{
					case VertTexture:
					{
						// Fill texture coordinate data for each point
						rawTexCoordOrder[index] = Short.parseShort(vertData[1]);
						rawTexCoordOrder[index] -= 1;

						int TexIndex = rawTexCoordOrder[index]
								* MeshLoader.TEXTURE_COORDS_PER_VERTEX;
						meshPoints[index].t1 = rawTexCoords[TexIndex];
						meshPoints[index].t2 = rawTexCoords[TexIndex + 1];
						break;
					}
					case VertNormal:
					{
						// Fill normal vector data for each point
						rawNormalOrder[index] = Short.parseShort(vertData[1]);
						rawNormalOrder[index] -= 1;

						int normIndex = rawNormalOrder[index]
								* MeshLoader.NORMAL_DATA_PER_VERTEX;
						meshPoints[index].n1 = rawNormals[normIndex];
						meshPoints[index].n2 = rawNormals[normIndex + 1];
						meshPoints[index].n3 = rawNormals[normIndex + 2];
						break;
					}
					case VertTextureNormal:
					{
						// Fill texture coordinate data for each point
						rawTexCoordOrder[index] = Short.parseShort(vertData[1]);
						rawTexCoordOrder[index] -= 1;

						int TexIndex = rawTexCoordOrder[index]
								* MeshLoader.TEXTURE_COORDS_PER_VERTEX;
						meshPoints[index].t1 = rawTexCoords[TexIndex];
						meshPoints[index].t2 = rawTexCoords[TexIndex + 1];

						rawNormalOrder[index] = Short.parseShort(vertData[2]);
						rawNormalOrder[index] -= 1;

						// Fill normal vector data for each point
						int normIndex = rawNormalOrder[index]
								* MeshLoader.NORMAL_DATA_PER_VERTEX;
						meshPoints[index].n1 = rawNormals[normIndex];
						meshPoints[index].n2 = rawNormals[normIndex + 1];
						meshPoints[index].n3 = rawNormals[normIndex + 2];
						break;
					}
					default:
						break;
				} // End switch
				index++;
			}// End inner for loop

		}// End outer for loop

		reader.reset();

		return facesArray;
	}

}
