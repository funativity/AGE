package age.asteroids.state;

import age.asteroids.Entity.Player;
import age.asteroids.enums.Audio;
import age.asteroids.scene.Level;
import age.asteroids.util.AstRotationListener;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import funativity.age.state.GameState;
import funativity.age.state.layout.AGELinearGLView;
import funativity.age.util.Logger;

/**
 * The GamePlayState is the Activity container for the Asteroids game. The logic
 * for player movement is implemented here because user input is necessary.
 * Player movement is dictated by the angular sensors on the device.
 * 
 */
public class GamePlayState extends GameState
{
	Level levelScene;

	private SensorManager sensorManager;
	private Sensor grav;
	private AstRotationListener rotListener;

	private TextView scoreView;
	private TextView statusView;

	@Override
	public void init()
	{
		// Set layout.
		AGELinearGLView ageGL = new AGELinearGLView(this);
		setLayout(ageGL);

		// Set scene.
		levelScene = new Level(this);
		ageGL.setScene(levelScene);

		if (grav != null)
		{
			Log.i("age.asteroids", "This device can use gravity!");
		}
		else
		{
			Log.w("age.asteroids", "This device can't use gravity!");
		}

		rotListener = new AstRotationListener(levelScene);

		scoreView = new TextView(this);
		scoreView.setText("Score: 0");
		getViewGroup().addView(scoreView);

		RelativeLayout layout = new RelativeLayout(this);

		// Update, Fire, Place Ships button
		RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		Button button = new Button(this);
		button.setText("Thruster");
		button.setLayoutParams(buttonParams);
		button.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (levelScene == null)
					return false;

				switch (event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						levelScene.player.thruster(true);
						break;
					case MotionEvent.ACTION_UP:
						levelScene.player.thruster(false);
						break;
				}
				return false;
			}
		});
		layout.addView(button);
		getViewGroup().addView(layout);

		//
		RelativeLayout.LayoutParams statusParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		statusParams.addRule(RelativeLayout.CENTER_VERTICAL);
		statusView = new TextView(this);
		statusView.setTextSize(60);
		statusView.setTextColor(Color.RED);
		statusView.setLayoutParams(statusParams);
		layout.addView(statusView);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		initAudio();
	}

	private void initAudio()
	{
		Audio.MUSIC.init(this);
		Audio.MUSIC.getAGEPlayer().setLooping(true);

		Audio.ATTACK.init(this);
		Audio.DIE.init(this);
		Audio.EXPLODE.init(this);
		Audio.POWERUP.init(this);
	}

	public void play(Audio audio, boolean seekToStart)
	{
		MediaPlayer player = audio.getAGEPlayer().getMediaPlayer();
		if (seekToStart && player.isPlaying())
		{
			player.seekTo(0);
		}
		else
		{
			player.start();
		}
	}

	public void stop(Audio audio)
	{
		audio.getAGEPlayer().stopPlaying();
	}

	@Override
	public boolean onDown(MotionEvent e)
	{
		levelScene.shoot();
		rotListener.readSensorForNorm();
		return super.onDown(e);
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector)
	{
		final float factor = detector.getScaleFactor();
		levelScene.player.camera_offset_scale /= factor;
		if (levelScene.player.camera_offset_scale > Player.CAMERA_OFFSET_MAX)
			levelScene.player.camera_offset_scale = Player.CAMERA_OFFSET_MAX;
		else if (levelScene.player.camera_offset_scale < Player.CAMERA_OFFSET_MIN)
			levelScene.player.camera_offset_scale = Player.CAMERA_OFFSET_MIN;
		return true;
	}

	public void onResume()
	{
		super.onResume();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		grav = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

		if (grav == null)
			Logger.e("Failed to create gravity sensor");

		sensorManager.registerListener(rotListener, grav,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		sensorManager.unregisterListener(rotListener);
	}

	public void setScoreLabel(final int score)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				scoreView.setText("Score: " + score);
			}
		});
	}

	public void setStatusLabel(final String status)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				statusView.setText(status);
			}
		});
	}

}
