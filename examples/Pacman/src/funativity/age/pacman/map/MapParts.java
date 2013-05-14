package funativity.age.pacman.map;

import java.util.ArrayList;
import java.util.List;

import funativity.age.opengl.AGEColor;
import funativity.age.opengl.DrawMode;
import funativity.age.opengl.Mesh;

import static funativity.age.pacman.map.MapLoader.*;

public class MapParts
{
	private static Mesh mapMesh;
	private static Mesh pacMesh;
	private static Mesh ggMesh;

	private static int convertTile(int tile)
	{
		if ((tile & B) == 0)
		{
			final int ALL_REMOVED_BITS = ALL_RESTRICTIONS | ALL_SPAWNS | T;

			tile = tile & ~ALL_REMOVED_BITS;
		}

		return tile;
	}

	public static void renderMap()
	{
		mapMesh.draw();
	}

	public static void renderExtras()
	{
		pacMesh.draw();
		ggMesh.draw();
	}

	public static void setMapColor(AGEColor color)
	{
		mapMesh.setColor(color);
	}

	private static void setPacColor(AGEColor color)
	{
		pacMesh.setColor(color);
	}

	public static void removePacAt(int index)
	{
		float[] newSize = { 0 };
		int SIZE_FLOAT = Float.SIZE / Byte.SIZE;
		pacMesh.bufferSubData(newSize, 1, SIZE_FLOAT, (index * 3 + 2)
				* SIZE_FLOAT, 0);
	}

	public static void loadMap(int[][] map, AGEColor mapColor, AGEColor pacColor)
	{
		final int width = map.length;
		final int height = map[0].length;

		final ArrayList<float[]> mapVertexArray = new ArrayList<float[]>();
		final ArrayList<float[]> ggVertexArray = new ArrayList<float[]>();
		final float[] pacData = new float[width * height * 3];

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				int tile = convertTile(map[x][y]);
				addPart(mapVertexArray, ggVertexArray, tile, x, y);

				if ((tile & B) == 0)
				{
					// setup pacs
					int pacIndex = (y * width + x) * 3;
					float pacSize = 0.0f;
					if ((tile & PAC) != 0)
						pacSize = 10f;
					else if ((tile & SPC) != 0)
						pacSize = 27f;

					pacData[pacIndex + 0] = x;
					pacData[pacIndex + 1] = y;
					pacData[pacIndex + 2] = pacSize;
				}
			}
		}

		// copy each vertex into the single array
		final float[] mapVertices = new float[mapVertexArray.size() * 5];
		for (int i = 0; i < mapVertexArray.size(); i++)
		{
			int index = i * 5;
			for (int j = 0; j < 5; j++)
			{
				mapVertices[index + j] = mapVertexArray.get(i)[j];
			}
		}

		// copy each vertex into the single array (again for GG)
		final float[] ggVertices = new float[ggVertexArray.size() * 5];
		for (int i = 0; i < ggVertexArray.size(); i++)
		{
			int index = i * 5;
			for (int j = 0; j < 5; j++)
			{
				ggVertices[index + j] = ggVertexArray.get(i)[j];
			}
		}

		// if there was already a map loaded, delete it out of the context
		if (mapMesh != null)
			mapMesh.delete();

		mapMesh = new Mesh();
		mapMesh.initMesh(mapVertices, null, DrawMode.GL_TRIANGLES);
		setMapColor(mapColor);

		// if a pac mesh was already made, delete it out of the context
		if (pacMesh != null)
			pacMesh.delete();

		pacMesh = new Mesh(PacTechnique.getTechnique());
		pacMesh.initMesh(pacData, null, DrawMode.GL_POINTS);
		setPacColor(pacColor);

		// if a gg mesh was already made, delete it out of the context
		if (ggMesh != null)
			ggMesh.delete();

		final AGEColor pink = new AGEColor(1, 0.722f, 0.871f);
		ggMesh = new Mesh();
		ggMesh.initMesh(ggVertices, null, DrawMode.GL_TRIANGLES);
		ggMesh.setColor(pink);

	}

	private static void addRect(List<float[]> vertices, float width,
			float height, float offsetX, float offsetY)
	{
		final float hw = width / 2f;
		final float hh = height / 2f;

		vertices.add(vertex(-hw + offsetX, +hh + offsetY));
		vertices.add(vertex(-hw + offsetX, -hh + offsetY));
		vertices.add(vertex(+hw + offsetX, +hh + offsetY));

		vertices.add(vertex(+hw + offsetX, +hh + offsetY));
		vertices.add(vertex(-hw + offsetX, -hh + offsetY));
		vertices.add(vertex(+hw + offsetX, -hh + offsetY));
	}

	private static void addPart(List<float[]> map, List<float[]> gg, int tile,
			float offsetX, float offsetY)
	{
		final float hw = 1 / 2f;
		final float hh = 1 / 2f;
		final float fw = 1 / 4f;
		final float fh = 1 / 4f;

		if ((tile & B) == 0)
		{
			switch (tile)
			{
				case GG:
					addRect(gg, 1, 0.25f, offsetX, -0.25f + offsetY);
					break;
			}
		}
		else
		{
			switch (tile)
			{
			// Vertical
				case VE:
					addRect(map, 0.5f, 1, 0.25f + offsetX, offsetY);
					break;
				case VW:
					addRect(map, 0.5f, 1, -0.25f + offsetX, offsetY);
					break;
				// Horizontal
				case HN:
					addRect(map, 1, 0.5f, offsetX, 0.25f + offsetY);
					break;
				case HS:
					addRect(map, 1, 0.5f, offsetX, -0.25f + offsetY);
					break;
				// Inside Corners
				case INE:
					map.add(vertex(+0 + offsetX, +hh + offsetY));
					map.add(vertex(+0 + offsetX, fh + offsetY));
					map.add(vertex(+hw + offsetX, +hh + offsetY));

					map.add(vertex(+hw + offsetX, +hh + offsetY));
					map.add(vertex(+0 + offsetX, +fh + offsetY));
					map.add(vertex(+fw + offsetX, +0 + offsetY));

					map.add(vertex(+hw + offsetX, +hh + offsetY));
					map.add(vertex(+fw + offsetX, +0 + offsetY));
					map.add(vertex(+hw + offsetX, +0 + offsetY));
					break;
				case INW:
					map.add(vertex(-hw + offsetX, +hh + offsetY));
					map.add(vertex(+0 + offsetX, +fh + offsetY));
					map.add(vertex(+0 + offsetX, +hh + offsetY));

					map.add(vertex(-hw + offsetX, +hh + offsetY));
					map.add(vertex(-fw + offsetX, +0 + offsetY));
					map.add(vertex(+0 + offsetX, +fh + offsetY));

					map.add(vertex(-hw + offsetX, +hh + offsetY));
					map.add(vertex(-hw + offsetX, +0 + offsetY));
					map.add(vertex(-fw + offsetX, +0 + offsetY));
					break;
				case ISE:
					map.add(vertex(+hw + offsetX, -hh + offsetY));
					map.add(vertex(+0 + offsetX, -fh + offsetY));
					map.add(vertex(+0 + offsetX, -hh + offsetY));

					map.add(vertex(+hw + offsetX, -hh + offsetY));
					map.add(vertex(+fw + offsetX, -0 + offsetY));
					map.add(vertex(+0 + offsetX, -fh + offsetY));

					map.add(vertex(+hw + offsetX, -hh + offsetY));
					map.add(vertex(+hw + offsetX, -0 + offsetY));
					map.add(vertex(+fw + offsetX, -0 + offsetY));
					break;
				case ISW:
					map.add(vertex(-hw + offsetX, -hh + offsetY));
					map.add(vertex(-fw + offsetX, -0 + offsetY));
					map.add(vertex(-hw + offsetX, -0 + offsetY));

					map.add(vertex(-hw + offsetX, -hh + offsetY));
					map.add(vertex(-0 + offsetX, -fh + offsetY));
					map.add(vertex(-fw + offsetX, -0 + offsetY));

					map.add(vertex(-hw + offsetX, -hh + offsetY));
					map.add(vertex(-0 + offsetX, -hh + offsetY));
					map.add(vertex(-0 + offsetX, -fh + offsetY));
					break;
				// Outside Corners
				case ONE:
					map.add(vertex(+hw + offsetX, +hh + offsetY));
					map.add(vertex(-hw + offsetX, +hh + offsetY));
					map.add(vertex(-hw + offsetX, +0 + offsetY));

					map.add(vertex(+hw + offsetX, +hh + offsetY));
					map.add(vertex(+0 + offsetX, -hh + offsetY));
					map.add(vertex(-hw + offsetX, +0 + offsetY));

					map.add(vertex(+hw + offsetX, +hh + offsetY));
					map.add(vertex(+hw + offsetX, -hh + offsetY));
					map.add(vertex(+0 + offsetX, -hh + offsetY));
					break;
				case ONW:
					map.add(vertex(-hw + offsetX, +hh + offsetY));
					map.add(vertex(+hw + offsetX, +0 + offsetY));
					map.add(vertex(+hw + offsetX, +hh + offsetY));

					map.add(vertex(-hw + offsetX, +hh + offsetY));
					map.add(vertex(-0 + offsetX, -hh + offsetY));
					map.add(vertex(+hw + offsetX, +0 + offsetY));

					map.add(vertex(-hw + offsetX, +hh + offsetY));
					map.add(vertex(-hw + offsetX, -hh + offsetY));
					map.add(vertex(-0 + offsetX, -hh + offsetY));
					break;
				case OSE:
					map.add(vertex(+hw + offsetX, -hh + offsetY));
					map.add(vertex(+0 + offsetX, +hh + offsetY));
					map.add(vertex(+hw + offsetX, +hh + offsetY));

					map.add(vertex(+hw + offsetX, -hh + offsetY));
					map.add(vertex(-hw + offsetX, -0 + offsetY));
					map.add(vertex(+0 + offsetX, +hh + offsetY));

					map.add(vertex(+hw + offsetX, -hh + offsetY));
					map.add(vertex(-hw + offsetX, -hh + offsetY));
					map.add(vertex(-hw + offsetX, -0 + offsetY));
					break;
				case OSW:
					map.add(vertex(-hw + offsetX, -hh + offsetY));
					map.add(vertex(-0 + offsetX, +hh + offsetY));
					map.add(vertex(-hw + offsetX, +hh + offsetY));

					map.add(vertex(-hw + offsetX, -hh + offsetY));
					map.add(vertex(+hw + offsetX, -0 + offsetY));
					map.add(vertex(-0 + offsetX, +hh + offsetY));

					map.add(vertex(-hw + offsetX, -hh + offsetY));
					map.add(vertex(+hw + offsetX, -hh + offsetY));
					map.add(vertex(+hw + offsetX, -0 + offsetY));
					break;
				// Border corners
				case BNE:
				case BSW:
					map.add(vertex(-hw + offsetX, +hh + offsetY));
					map.add(vertex(-hw + offsetX, +0 + offsetY));
					map.add(vertex(-0 + offsetX, +hh + offsetY));

					map.add(vertex(-0 + offsetX, +hh + offsetY));
					map.add(vertex(-hw + offsetX, +0 + offsetY));
					map.add(vertex(-0 + offsetX, -hh + offsetY));

					map.add(vertex(-0 + offsetX, +hh + offsetY));
					map.add(vertex(-0 + offsetX, -hh + offsetY));
					map.add(vertex(+hw + offsetX, +0 + offsetY));

					map.add(vertex(+hw + offsetX, +0 + offsetY));
					map.add(vertex(+0 + offsetX, -hw + offsetY));
					map.add(vertex(+hw + offsetX, -hh + offsetY));
					break;
				case BNW:
				case BSE:
					map.add(vertex(+hw + offsetX, +hh + offsetY));
					map.add(vertex(+0 + offsetX, +hh + offsetY));
					map.add(vertex(+hw + offsetX, +0 + offsetY));

					map.add(vertex(+0 + offsetX, +hh + offsetY));
					map.add(vertex(+0 + offsetX, -hh + offsetY));
					map.add(vertex(+hw + offsetX, +0 + offsetY));

					map.add(vertex(+0 + offsetX, +hh + offsetY));
					map.add(vertex(-hw + offsetX, -0 + offsetY));
					map.add(vertex(+0 + offsetX, -hh + offsetY));

					map.add(vertex(+0 + offsetX, -hh + offsetY));
					map.add(vertex(-hw + offsetX, -0 + offsetY));
					map.add(vertex(-hw + offsetX, -hh + offsetY));
					break;
				// Full
				case B:
					addRect(map, 1, 1, offsetX, offsetY);
					break;
			}
		}
	}

	private static float[] vertex(float x, float y)
	{
		return new float[] { x, y, 0, 0, 0 };
	}
}
