package age.asteroids.util;

import funativity.age.util.sensor.RotationListener;
import age.asteroids.Entity.Player;
import age.asteroids.scene.Level;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Sensor listener for handling player rotation using the gravity sensor.
 */
public class AstRotationListener extends RotationListener implements SensorEventListener
{
	private Level levelScene;
	
	public AstRotationListener(Level levelScene)
	{
		super(5);
		this.levelScene = levelScene;
		
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
		super.onSensorChanged(event);
		
		float x = -yAverage.getAverage();
		float y = xAverage.getAverage();
		float z = zAverage.getAverage();
		
		if( isNormSet )
		{
			float dx = x - yNorm;
			float dy = y - xNorm;
			float dz = z - zNorm;
			
			Player p = levelScene.player;
			final float scale = 0.003f;
			final float zScale = scale / 2.5f;
			
			if (p != null)
			{
				p.yaw(dx * scale);
				p.pitch(dy * scale * 2);
				
				// Calculate a more proper roll based on the current movement
				if( dx > 0 && dy > 0 )
				{
					dz = (dx * dy / 2) * zScale;					
				}
				else if( dx < 0 && dy < 0)
				{
					dz = (dx * dy / 2) * -zScale;
				}
				else if( dx > 0 && dy < 0 )
				{
					dz = (dx * dy / 2) * -zScale;
				}
				else if( dx < 0 && dy > 0 )
				{
					dz = (dx * dy / 2) * zScale;
				}
					
				p.roll(dz);
			}
		}
	}
	
	@Override
	public void readSensorForNorm()
	{
		
		if( !isNormSet )
		{
			super.readSensorForNorm();
		}
	}
}
