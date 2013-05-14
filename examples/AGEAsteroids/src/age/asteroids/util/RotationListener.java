package age.asteroids.util;

import age.asteroids.Entity.Player;
import age.asteroids.scene.Level;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Sensor listener for handling player rotation using the gravity sensor.
 */
public class RotationListener implements SensorEventListener
{
	private Level levelScene;
	private MovingAverage xAverage;
	private MovingAverage yAverage;
	private MovingAverage zAverage;

	private float xNorm = 0;
	private float yNorm = 0;
	private float zNorm = 0;

	private boolean isNormSet = false;

	public RotationListener(Level levelScene)
	{
		this.levelScene = levelScene;
		xAverage = new MovingAverage(5);
		yAverage = new MovingAverage(5);
		zAverage = new MovingAverage(5);

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

		xAverage.add(-y);
		yAverage.add(x);
		zAverage.add(z);

		if (isNormSet)
		{
			float dx = xAverage.getAverage() - xNorm;
			float dy = yAverage.getAverage() - yNorm;
			float dz = zAverage.getAverage() - zNorm;

			Player p = levelScene.player;
			final float scale = 0.003f;
			final float zScale = scale / 2.5f;

			if (p != null)
			{
				p.yaw(dx * scale);
				p.pitch(dy * scale * 2);

				// Calculate a more proper roll based on the current movement
				if (dx > 0 && dy > 0)
				{
					dz = (dx * dy / 2) * zScale;
				}
				else if (dx < 0 && dy < 0)
				{
					dz = (dx * dy / 2) * -zScale;
				}
				else if (dx > 0 && dy < 0)
				{
					dz = (dx * dy / 2) * -zScale;
				}
				else if (dx < 0 && dy > 0)
				{
					dz = (dx * dy / 2) * zScale;
				}

				p.roll(dz);
			}
		}
	}

	/**
	 * Zeros in the readings to create a base of no rotation.
	 */
	public void readSensorForNorm()
	{
		if (isNormSet)
		{
			return;
		}
		xNorm = xAverage.getAverage();
		yNorm = yAverage.getAverage();
		zNorm = zAverage.getAverage();

		isNormSet = true;
	}

}
