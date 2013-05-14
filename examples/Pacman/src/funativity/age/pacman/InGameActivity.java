package funativity.age.pacman;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import funativity.age.state.GameState;
import funativity.age.state.layout.AGELinearGLView;

public class InGameActivity extends GameState
{
	private static final float TOUCH_DEADZONE = 0.0006f;
	private static final float TOUCH_OFFSETX = 0.0f;
	private static final float TOUCH_OFFSETY = 0.35f;

	private final GameScene scene;
	private TextView scoreNumber;
	private TextView label;

	public InGameActivity()
	{
		super();
		scene = new GameScene(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);

	}

	@Override
	public void init()
	{
		// normal opengl activity setup
		AGELinearGLView ageGL = new AGELinearGLView(this);
		this.setLayout(ageGL);
		((LinearLayout) this.getViewGroup()).setGravity(Gravity.BOTTOM);

		ageGL.setScene(scene);

		label = new TextView(this);
		label.setTextSize(30);
		label.setGravity(Gravity.LEFT);
		label.setTextColor(Color.YELLOW);
		getViewGroup().addView(label);
		scene.addToLives(0);

		scoreNumber = new TextView(this);
		scoreNumber.setTextSize(30);
		scoreNumber.setGravity(Gravity.LEFT);
		scoreNumber.setTextColor(Color.YELLOW);
		getViewGroup().addView(scoreNumber);
		updateScore(0);
	}

	public void updateScore(final int score)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				scoreNumber.setText("" + score);
			}
		});
	}

	public void updateLives(final int lives)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				label.setText("Score:");// x " + lives);
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if (scene == null || scene.getPacman() == null)
			return false;

		// if user releases, return the joystick to original position and skip
		// the rest
		if (event.getAction() == android.view.MotionEvent.ACTION_UP)
		{
			scene.getJoystick().setDirNone();
			return true;
		}

		final float x = (((float) event.getX()) / v.getWidth()) - 0.5f
				+ TOUCH_OFFSETX;
		final float y = 0.5f - (((float) event.getY()) / v.getHeight())
				+ TOUCH_OFFSETY;

		if (x * x + y * y < TOUCH_DEADZONE)
		{
			scene.getJoystick().setDirNone();
			return true;
		}

		float angle = (float) (Math.atan2(x, y) * 180 / Math.PI);

		if (x < 0)
			angle += 360;

		if (angle > 45 && angle < 135)
		{
			scene.getPacman().requestDirection(1, 0);
			scene.getJoystick().setDirRight();
		}
		else if (angle > 135 && angle < 225)
		{
			scene.getPacman().requestDirection(0, -1);
			scene.getJoystick().setDirDown();
		}
		else if (angle > 225 && angle < 315)
		{
			scene.getPacman().requestDirection(-1, 0);
			scene.getJoystick().setDirLeft();
		}
		else if (angle > 315 || angle < 45)
		{
			scene.getPacman().requestDirection(0, 1);
			scene.getJoystick().setDirUp();
		}

		return true;
	}

}
