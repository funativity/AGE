package funativity.age.pacman;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import funativity.age.state.GameState;
import funativity.age.state.layout.AGELinearLayout;

public class SettingsActivity extends GameState
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
		this.setLayout(new AGELinearLayout(this));

	}

}
