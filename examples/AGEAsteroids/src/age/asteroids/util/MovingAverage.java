package age.asteroids.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class represents a moving average over the
 * given capacity.
 *
 */
public class MovingAverage
{
	private Queue<Float> numbers;
	private int capacity;
	
	/**
	 * Constructor.  Capacity is the amount of values the moving average
	 * should keep track of.
	 * @param capacity Number of values to keep a record of for the average.
	 */
	public MovingAverage(int capacity)
	{
		if(capacity < 1)
		{
			capacity = 1;
		}
		this.capacity = capacity;
		numbers = new LinkedList<Float>();
	}
	
	/**
	 * Add a value to the moving average.  If the number of values added
	 * goes above the capacity, then the oldest entry is removed.
	 * @param value Value to add to the moving average.
	 */
	public void add(float value)
	{
		numbers.add(value);
		
		while( numbers.size() > capacity )
		{
			numbers.poll();
		}
	}
	
	/**
	 * Calculates the un-weighted average based off of the last 'capacity'
	 * entries.
	 * @return Average of the values.
	 */
	public float getAverage()
	{
		float retVal = 0;
		Float[] tempArray = new Float[numbers.size()];
		numbers.toArray(tempArray);
		for( int i=0; i < tempArray.length; i++)
		{
			retVal += tempArray[i];
		}
		retVal /= tempArray.length;
		
		return retVal;
	}
}
