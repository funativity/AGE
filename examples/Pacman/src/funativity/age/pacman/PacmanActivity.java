package funativity.age.pacman;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import funativity.age.state.GameState;
import funativity.age.state.Scene;
import funativity.age.state.layout.AGELinearGLView;

public class PacmanActivity extends GameState
{
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
		((LinearLayout) getViewGroup()).setGravity(Gravity.CENTER);

		MainScene scene = new MainScene(this);
		ageGL.setScene(scene);

		// Text View
		TextView tv = new TextView(this);
		tv.setText("Pacman");
		tv.setTextColor(Color.YELLOW);
		tv.setTextSize(50);
		tv.setGravity(Gravity.CENTER);
		this.getViewGroup().addView(tv);

		Button startButton = new Button(this);
		startButton.setText("Start Game");
		startButton.setGravity(Gravity.CENTER);
		startButton.setTextSize(25);
		this.getViewGroup().addView(startButton);

		Button settingsButton = new Button(this);
		settingsButton.setText("Settings");
		settingsButton.setGravity(Gravity.CENTER);
		settingsButton.setTextSize(25);
		this.getViewGroup().addView(settingsButton);

		final Intent gameIntent = new Intent(PacmanActivity.this,
				InGameActivity.class);
		startButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(gameIntent);
			}
		});

		final Intent settingsIntent = new Intent(PacmanActivity.this,
				SettingsActivity.class);
		settingsButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(settingsIntent);
			}
		});
	}

	private class MainScene extends Scene
	{
		public MainScene(Context context)
		{
			super(context);
		}

		@Override
		public void init()
		{

		}

		@Override
		public void loadResources()
		{

		}
	}
}
