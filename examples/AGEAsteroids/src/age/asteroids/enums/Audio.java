package age.asteroids.enums;

import funativity.age.util.AGEMediaPlayer;
import funativity.age.util.AudioManager;
import age.asteroids.R;
import android.content.Context;

public enum Audio
{
	ATTACK(R.raw.attack), DIE(R.raw.die), EXPLODE(R.raw.explode), POWERUP(
			R.raw.powerup), MUSIC(R.raw.music);

	private int resID;
	private AGEMediaPlayer player;

	Audio(int resID)
	{
		this.resID = resID;
	}

	public void init(Context context)
	{
		player = AudioManager.getAudioManager().addAGEMediaPlayer(context,
				resID);
	}

	public AGEMediaPlayer getAGEPlayer()
	{
		return player;
	}
}