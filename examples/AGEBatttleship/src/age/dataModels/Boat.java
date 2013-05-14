package age.dataModels;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import age.enums.BoatType;
import age.enums.Direction;
import android.content.res.AssetManager;
import funativity.age.opengl.Entity;
import funativity.age.opengl.Mesh;
import funativity.age.util.Geometry3f;

/**
 * The object that contains all the needed information for the boat
 * 
 * @author binisha
 * 
 */
public class Boat implements Serializable
{
	private static final long serialVersionUID = 9003973102937150572L;

	/**
	 * The head coordinate of the boat
	 */
	private Coordinate head;

	/**
	 * The direction the boat is facing
	 */
	private Direction direction;

	/**
	 * The number of times this boat has been hit
	 */
	private int hits;

	/**
	 * The type of the boat
	 */
	private BoatType boatType;

	/**
	 * The entity of the boat
	 */
	private Entity entity;

	/**
	 * The basic boat constructor that uses a default coordiante of 0,0 and a
	 * direction of vertical
	 * 
	 * @param type
	 *            The type of boat being modeled
	 */
	public Boat(BoatType type)
	{
		this(type, new Coordinate(0, 0), Direction.VERTICAL);
	}

	/**
	 * Constructor for the boat using a default direction of vertical
	 * 
	 * @param type
	 *            The type of boat being modeled
	 * @param head
	 *            The head coordinate of the boat
	 */
	public Boat(BoatType type, Coordinate head)
	{
		this(type, head, Direction.VERTICAL);
	}

	/**
	 * The complete constructor for the boat
	 * 
	 * @param type
	 *            The type of boat being modeled
	 * @param head
	 *            The head coordinate of the boat
	 * @param direction
	 *            The direction the boat is orientated
	 */
	public Boat(BoatType type, Coordinate head, Direction direction)
	{
		this.head = head;
		this.direction = direction;
		this.boatType = type;
	}

	/**
	 * Determine if this boat has sunk.
	 * 
	 * @return True if this boat is dead
	 */
	public boolean isBoatDead()
	{
		return (hits == boatType.getLength());
	}

	/**
	 * Inform this boat that it has been hit
	 */
	public void hit()
	{
		hits++;
	}

	/**
	 * Gets a list of coordinates this boat occupies, starting from the head and
	 * working towards the tail.
	 * 
	 * @return list of coordinates this boat occupies
	 */
	public List<Coordinate> getCoordinates()
	{
		List<Coordinate> coords = new ArrayList<Coordinate>();

		for (int pos = 0; pos < boatType.getLength(); pos++)
		{
			// Start from the head.
			int currentX = head.x;
			int currentY = head.y;

			if (direction == Direction.HORIZONTAL)
			{
				// Go left from the head.
				currentX -= pos;
			}
			else if (direction == Direction.VERTICAL)
			{
				// Go down from the head.
				currentY += pos;
			}

			// Add to a list.
			Coordinate current = new Coordinate(currentX, currentY);
			coords.add(current);
		}

		return coords;
	}

	/**
	 * Determine if this boat exists on a coordinate.
	 * 
	 * @param coord
	 *            coordinate on grid
	 * @return true if a part of this boat exists on the coordinate, false
	 *         otherwise
	 */
	public boolean isAt(Coordinate coord)
	{
		for (Coordinate boatCoord : getCoordinates())
		{
			if (boatCoord.equals(coord))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Moves the head of this boat to a coordinate on the grid.
	 * 
	 * @param coord
	 *            coordinate on grid
	 */
	public void moveHeadTo(Coordinate coord)
	{
		setHead(coord.x, coord.y);
		int visualFix = direction == Direction.HORIZONTAL ? 1 : 0;
		entity.setPosition(new Geometry3f(coord.x + visualFix, coord.y));
	}

	/**
	 * Moves the head of the boat by a horizontal and vertical change.
	 * 
	 * @param dx
	 *            horizontal change in coordinates
	 * @param dy
	 *            vertical change in coordinates
	 */
	public void translate(int dx, int dy)
	{
		setHead(head.x + dx, head.y + dy);
		entity.getPosition().translate(dx, dy, 0);
	}

	/**
	 * Switches the direction of this boat and updates it visually.
	 */
	public void swapDirection()
	{
		if (direction == Direction.HORIZONTAL)
		{
			direction = Direction.VERTICAL;
		}
		else if (direction == Direction.VERTICAL)
		{
			direction = Direction.HORIZONTAL;
		}

		updateDirection();
	}

	/**
	 * Visually update according to the direction of the boat.
	 */
	public void updateDirection()
	{
		if (direction == Direction.HORIZONTAL)
		{
			entity.setRZ(0);
		}
		else if (direction == Direction.VERTICAL)
		{
			entity.setRZ(-90);
		}
	}

	/**
	 * Get the name of the boat
	 * 
	 * @return The name of the boat
	 */
	public String getName()
	{
		return boatType.getName();
	}

	/**
	 * Get the direction of the boat
	 * 
	 * @return The direction of the boat
	 */
	public Direction getDirection()
	{
		return direction;
	}

	/**
	 * Get the length of the boat
	 * 
	 * @return The length of the boat
	 */
	public int getLength()
	{
		return boatType.getLength();
	}

	/**
	 * Get the type of boat this is
	 * 
	 * @return
	 */
	public BoatType getType()
	{
		return boatType;
	}

	/**
	 * Get the head of the boat
	 * 
	 * @return The head of the boat
	 */
	public Coordinate getHead()
	{
		return head;
	}

	/**
	 * Get the entity of the boat
	 * 
	 * @return The entity of the boat
	 */
	public Entity getEntity()
	{
		return entity;
	}

	/**
	 * Set the entity of the boat
	 * 
	 * @param entity
	 *            New entity of the boat
	 */
	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}

	/**
	 * Set the location of the head of the boat
	 * 
	 * @param head
	 *            New location of the head
	 */
	public void setHead(Coordinate head)
	{
		this.head.x = head.x;
		this.head.y = head.y;
	}

	/**
	 * Set the location of the head of the boat
	 * 
	 * @param x
	 *            New x-location of the head
	 * @param y
	 *            New y-location of the head
	 */
	public void setHead(int x, int y)
	{
		this.head.x = x;
		this.head.y = y;
	}

	/**
	 * This creates the sprite for the boat
	 * @param am The asset manager
	 * @param tileSize The size of the tiles
	 * @return The mesh that holds the boat
	 */
	public Mesh CreateSprite(AssetManager am, float tileSize)
	{
		return boatType.createSprite(am, tileSize);
	}

	/**
	 * This will write out some information on the boat
	 * @param out Where the info will be outputted to
	 * @throws IOException Throws exception if cannot write to out stream
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(head);
		out.writeObject(direction);
		out.writeInt(hits);
		out.writeObject(boatType);
	}

	/**
	 * This will read in input data into the input stream to load
	 * @param in The input stream
	 * @throws IOException Will throw an exception if cannot read from input stream
	 * @throws ClassNotFoundException Will throw exception if cannot load the inputted data
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException
	{

		this.head = (Coordinate) in.readObject();
		this.direction = (Direction) in.readObject();
		this.hits = in.readInt();
		this.boatType = (BoatType) in.readObject();
	}

}
