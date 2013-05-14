package age.enums;

import android.content.res.AssetManager;
import funativity.age.opengl.Mesh;
import funativity.age.opengl.primitive.Rectangle;
import funativity.age.textures.TextureLoader;
import funativity.age.util.Logger;

/**
 * This enum represents the type of boat being displayed
 * 
 * @author binisha
 * 
 */
public enum BoatType
{
	// TODO Find a better way to get image paths

	//@formatter:off
	AIRCRAFT_CARRIER(5, "Aircraft Carrier", "Size5.png"),
	BATTLESHIP(4, "Battleship", "Size4.png"), 
	SUBMARINE( 3, "Submarine", "Size3 (2).png"), 
	DESTROYER(3, "Destroyer" , "Size3.png"), 
	PATROL_BOAT(2, "Patrol Boat", "Size2.png");
	//@formatter:on

	private int length;
	private String name;
	private String imagePath;
	public Rectangle mesh;

	BoatType(int length, String name, String imagePath)
	{
		this.length = length;
		this.name = name;
		this.imagePath = "images/" + imagePath;
	}

	/**
	 * Get the length of the boat
	 * @return The integer length of the boat
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * Get the name of the boat
	 * @return The name of the boat
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * This will create the mesh for the boat
	 * @param am The asset manager
	 * @param tileSize The size of the tiles
	 */
	private void createMesh(AssetManager am, float tileSize)
	{
		float length = getLength() * tileSize;
		float textureRatio = 1;

		switch (getLength())
		{
			case 3:
				textureRatio = 3f / 4f;
				break;
			case 5:
				textureRatio = 5f / 8f;
				break;
		}

		mesh = new Rectangle(length, tileSize, -length / 2f, tileSize / 2f);
		mesh.updateTexCoordData(0, textureRatio, 0, 1);

		try
		{
			mesh.setTexture(TextureLoader.getTexture(imagePath, am));
		}
		catch (Exception e)
		{
			Logger.e("Failed to load texture", e);
		}
	}

	/**
	 * This will create the sprite for the specified boat
	 * @param am The asset manager
	 * @param tileSize THe size of the tiles
	 * @return The created sprite for the boat
	 */
	public Mesh createSprite(AssetManager am, float tileSize)
	{
		if (mesh == null)
			createMesh(am, tileSize);

		return mesh;
	}
}
