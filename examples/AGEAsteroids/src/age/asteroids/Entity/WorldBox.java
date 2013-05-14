package age.asteroids.Entity;

import funativity.age.opengl.DrawMode;
import funativity.age.opengl.Mesh;

public class WorldBox extends GameEntity
{
	public WorldBox(float width, float height, float depth)
	{
		super(createMesh(width, height, depth));
	}

	private static Mesh createMesh(float width, float height, float depth)
	{
		// Box b = new Box(width, height, depth);

		Mesh m = new Mesh();
		m.initMesh(initVertices(width, height, depth), initDrawOrder(),
				DrawMode.GL_TRIANGLES);
		return m;
	}

	@Override
	public void collide(GameEntity other)
	{
	}

	private static float[] initVertices(float width, float height, float depth)
	{
		float hw = width / 2f;
		float hh = height / 2f;
		float hd = depth / 2f;

		//@formatter:off
		return new float[] {
				// front
				-hw, hh, hd, 0, 0,
				-hw, -hh, hd, 0, 1, 
				hw, hh, hd, 1, 0,
				hw, -hh, hd, 1, 1,

				// back
				-hw, hh, -hd, 0, 0, 
				hw, hh, -hd, 1, 0, 
				-hw, -hh, -hd, 0, 1, 
				hw, -hh, -hd, 1, 1,

				// left
				-hw, hh, hd, 0, 0, 
				-hw, -hh, hd, 0, 1, 
				-hw, -hh, -hd, 1, 1,
				-hw, hh, -hd, 1, 0,

				// right
				hw, hh, hd, 0, 0, 
				hw, -hh, -hd, 1, 1, 
				hw, -hh, hd, 0, 1, 
				hw, hh, -hd, 1, 0,

				// top
				hw, hh, hd, 1, 1, 
				-hw, hh, hd, 0, 1, 
				hw, hh, -hd, 1, 0, 
				-hw, hh, -hd, 0, 0,

				// bottom
				hw, -hh, hd, 1, 1, 
				-hw, -hh, hd, 0, 1, 
				-hw, -hh, -hd, 0, 0, 
				hw, -hh, -hd, 1, 0
			};
		//@formatter:on
	}

	private static short[] initDrawOrder()
	{
		return new short[] {
				// front
				0, 2, 1, 2, 3, 1,

				// back
				4, 6, 5, 6, 7, 5,

				// left
				9, 10, 8, 10, 11, 8,

				// right
				13, 14, 12, 12, 15, 13,

				// top
				17, 18, 16, 17, 19, 18,

				// bottom
				20, 22, 21, 20, 23, 22 };
	}

}
