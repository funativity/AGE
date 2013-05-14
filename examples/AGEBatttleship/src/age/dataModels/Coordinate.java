package age.dataModels;

import java.io.Serializable;

/**
 * The object that represents a map coordinate
 * 
 * @author binisha
 * 
 */
public class Coordinate implements Serializable
{
	private static final long serialVersionUID = -3077886798080078240L;

	/**
	 * This represents the x value of the coordinate
	 */
	public int x;

	/**
	 * This represents the y value of the coordinate
	 */
	public int y;

	/**
	 * Intended for use only with shots. True if this shot hit a boat, false
	 * otherwise.
	 */
	public boolean hit = false;

	/**
	 * The main constructor for a coordinate
	 * 
	 * @param x
	 *            The x value of the coordinate
	 * @param y
	 *            The y value of the coordinate
	 */
	public Coordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Copy data from specified coordinate into this new instance
	 * 
	 * @param c The new coordinate data
	 */
	public Coordinate(Coordinate c)
	{
		this(c.x, c.y);
		this.hit = c.hit;
	}

	/**
	 * Auto-generated with Eclipse.
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (hit ? 1231 : 1237);
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	/**
	 * Auto-generated with Eclipse.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Coordinate))
			return false;
		Coordinate other = (Coordinate) obj;
		if (hit != other.hit)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}