package age.asteroids.state;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import funativity.age.state.GameState;
import funativity.age.state.layout.AGELinearLayout;

/**
 * The MainMenuState is the entrance Activity for the game. It allows the player
 * to view HighScores, play a new game, see the instructions, and quit.
 */
public class MainMenuState extends GameState
{

	@Override
	public void init()
	{
		AGELinearLayout ageLL = new AGELinearLayout(this);
		setLayout(ageLL);
		((LinearLayout) this.getViewGroup()).setGravity(Gravity.CENTER);

		addNewGameButton();
		addViewHighScoreButton();
		addViewInstructionsButton();
		addQuitButton();
	}

	private void addNewGameButton()
	{
		final Button button = new Button(this);
		button.setText("New Game");
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(MainMenuState.this,
						GamePlayState.class);
				startActivity(intent);
			}
		});
		this.getViewGroup().addView(button);
	}

	private void addViewHighScoreButton()
	{
		final Button button = new Button(this);
		button.setText("High Scores");
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Logic goes here
			}
		});
		this.getViewGroup().addView(button);
	}

	private void addViewInstructionsButton()
	{
		final Button button = new Button(this);
		button.setText("Instructions");
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Logic goes here
			}
		});
		this.getViewGroup().addView(button);
	}

	private void addQuitButton()
	{
		final Button button = new Button(this);
		button.setText("Quit");
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Logic goes here
			}
		});
		this.getViewGroup().addView(button);
	}

}
