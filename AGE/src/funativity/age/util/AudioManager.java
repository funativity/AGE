package funativity.age.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

/**
 * This singleton class will manage all the AGEMediaPlayer instances for the
 * users.
 * 
 * @author vancer
 * 
 */
public class AudioManager
{
	private static AudioManager instance = null;
	private SparseArray<AGEMediaPlayer> players;

	/**
	 * Constructor to create player list
	 */
	private AudioManager()
	{
		players = new SparseArray<AGEMediaPlayer>();
	}

	/**
	 * This is the singleton instance for this class
	 * 
	 * @return
	 */
	public static AudioManager getAudioManager()
	{
		if (instance == null)
		{
			instance = new AudioManager();
		}

		return instance;
	}

	/**
	 * This will allow the user to add a AGEMediaPlayer to this class.
	 * 
	 * @param context
	 *            Context in which the sound will be used
	 * @param resId
	 *            The location of media. Will be R.raw.<NameOfMedia>
	 * @return An AGEMediaPlayer instance
	 */
	public AGEMediaPlayer addAGEMediaPlayer(Context context, int resId)
	{
		AGEMediaPlayer player = null;
		try
		{
			player = new AGEMediaPlayer(context, resId);
			players.put(resId, player);
		}
		catch (Exception e)
		{
			Log.e("AUDIO", "Could not create media player.");
		}

		return player;
	}

	/**
	 * This will allow the user to remove the AGEPlayer from this class's list
	 * 
	 * @param resId
	 */
	public void removeAGEMediaPlayer(int resId)
	{
		players.remove(resId);
	}

	/**
	 * This will return an instance of AGEMediaPlayer based on resId
	 * 
	 * @param resId
	 *            The location of media. Will be R.raw.<NameOfMedia>
	 * @return AGEMediaPlayer instance
	 */
	public AGEMediaPlayer getMediaPlayer(int resId)
	{
		return players.get(resId);
	}

	/**
	 * This will return the list of all media players in this manager
	 * 
	 * @return list of all players in the manager
	 */
	public List<AGEMediaPlayer> getAllMediaPlayers()
	{
		List<AGEMediaPlayer> list = new ArrayList<AGEMediaPlayer>();
		for (int i=0; i<players.size(); i++)
		{
			list.add(players.valueAt(i));
		}
		return list;
	}
}
