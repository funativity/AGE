package funativity.age.util.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import funativity.age.util.MovingAverage;

/**
 * The rotation listener wraps the data input from a Gravity sensor
 * using a moving average of the specified amount.  It also provides
 * a way to 'zero' the readings for the moving average.
 * 
 * For best use, extend this class and override the onSensorChanged() method.
 * Make sure to call super.onSensorChanged then utilze the moving average data
 * to accomplish what you want.
 *
 */
public class RotationListener implements SensorEventListener
{
	/** The MovingAverage for x */
	protected MovingAverage xAverage;
	/** The MovingAverage for y */
	protected MovingAverage yAverage;
	/** The MovingAverage for z */
	protected MovingAverage zAverage;
	
	/** The current zero'd value for x */
	protected float xNorm = 0;
	/** The current zero'd value for y */
	protected float yNorm = 0;
	/** The current zero'd value for z */
	protected float zNorm = 0;
	
	/** True if the norm has been set previously */
	protected boolean isNormSet = false;
	
	public RotationListener(int movingAverageHistory)
	{
		xAverage = new MovingAverage(movingAverageHistory);
		yAverage = new MovingAverage(movingAverageHistory);
		zAverage = new MovingAverage(movingAverageHistory);
		
		isNormSet = false;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// Do nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		// Add values
		float x = (float) event.values[0];
		float y = (float) event.values[1];
		float z = (float) event.values[2];
		
		xAverage.add(x);
		yAverage.add(y);
		zAverage.add(z);
		
	}
	
	/**
	 * Based off of the current moving average, zeros the x/y/z normal values.
	 */
	public void readSensorForNorm()
	{
		
		xNorm = xAverage.getAverage();
		yNorm = yAverage.getAverage();
		zNorm = zAverage.getAverage();
		
		isNormSet = true;
	}
	
	/**
	 * Sets x/y/z zero'd values to... zero and clears the isNormSet flag.
	 */
	public void clearNormValues()
	{
		
		xNorm = 0;
		yNorm = 0;
		zNorm = 0;
		
		isNormSet = false;
	}

}
