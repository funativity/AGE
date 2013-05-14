package funativity.age.util;

import java.io.IOException;
import funativity.age.state.GameState;
import funativity.age.state.layout.AGELinearLayout;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

/**
 * This class will allow the user to configure an audio to play.  They will use this class through the AGE.util.AudioManager class.
 * 
 * @author vancer
 * 
 */
public class AGEMediaPlayer implements MediaPlayer.OnPreparedListener
{

	private Context mContext;
	private int resId;
	private MediaPlayer mPlayer;
	private Button btnPlayPause;
	private Button btnStop;
	private boolean wasStopped;
	private SeekBar seekBar;
	private final Handler handler = new Handler();

	/**
	 * Constructor to create an AGE media player. This will do all the setup and
	 * create the Media Player for android based on the context and the uri
	 * supplied.
	 * 
	 * Exceptions will be logged to Logcat with the tag "AUDIOMANAGER"
	 * 
	 * @param context
	 *            The context of the activity (getApplicationContext())
	 * @param resId
	 *            The location of media.  Will be R.raw.<NameOfMedia>
	 * @throws Exception Throws exception if could not create the media player
	 */
	protected AGEMediaPlayer(Context context, int resId) throws Exception
	{
		this.mContext = context;
		this.resId = resId;

		wasStopped = false;

		if(!createMediaPlayer()){
			throw new Exception("MEDIA PLAYER: Could not create media player");
		}
	}

	/**
	 * This will create the media player based on the audio the user passed into the constructor.
	 * @return True if creation was successful
	 */
	private boolean createMediaPlayer()
	{
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		try
		{
			mediaPlayer = MediaPlayer.create(mContext, resId);			
			mPlayer = mediaPlayer;
			return true;
		}
		catch (IllegalArgumentException e)
		{
			Log.e("AUDIOMANAGER",
					"IllegalArgumentException " + e.getStackTrace());
			return false;
		}
		catch (SecurityException e)
		{
			Log.e("AUDIOMANAGER", "SecurityException " + e.getStackTrace());
			return false;
		}
		catch (IllegalStateException e)
		{
			Log.e("AUDIOMANAGER", "IllegalStateException " + e.getStackTrace());
			return false;
		}
	}

	/**
	 * Pause the playing of the audio track
	 */
	public void pausePlaying()
	{
		if (mPlayer == null)
			return;

		mPlayer.pause();
	}

	/**
	 * Start playing the audio track
	 */
	public void startPlaying()
	{
		if (mPlayer == null)
			return;
		
		if(wasStopped){
			restartMedia();
		}
		
		mPlayer.start();

		// Check if there is a seekBar
		if (seekBar != null)
			startPlayProgressUpdater();
	}

	/**
	 * Stop playing the audio track
	 */
	public void stopPlaying()
	{
		if (mPlayer == null)
			return;

		mPlayer.stop();
		
		// Check if there is a seekBar
		if (seekBar != null){
			seekBar.setProgress(0); //Reset the progress
			mPlayer.seekTo(0); //Reset song
			btnPlayPause.setText("Play");
			wasStopped = true;
		}
	}
	
	/**
	 * This will allow the user to set whether the media should loop or not.
	 * @param isLooping
	 */
	public void setLooping(boolean isLooping){
		if (mPlayer == null)
			return;
		
		mPlayer.setLooping(isLooping);
	}
	
	/**
	 * This will recreate and restart the media.  These steps must be done in order to restart the media
	 * @return true if able to restart
	 */
	public boolean restartMedia(){
		try
		{
			mPlayer.reset();
			mPlayer = MediaPlayer.create(mContext, resId);
			mPlayer.prepare();
			wasStopped = false;
			
			return true;
		}
		catch (IllegalStateException e)
		{
			Log.e("AUDIOMANAGER", "IllegalStateException " + e.getStackTrace());
			return false;
		}
		catch (IOException e)
		{
			Log.e("AUDIOMANAGER", "IOException " + e.getStackTrace());
			return false;
		}
	}

	/**
	 * Release the media player when done with the audio file.
	 */
	public void closeMediaPlayer()
	{
		if (mPlayer == null)
			return;

		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
		btnPlayPause = null;
		seekBar = null;
	}

	/**
	 * This will return Android's version of the media player.
	 * 
	 * @return The MediaPlayer object, if it was not correctly set up, the
	 *         player could be null
	 */
	public MediaPlayer getMediaPlayer()
	{
		return mPlayer;
	}

	@Override
	public void onPrepared(MediaPlayer arg0)
	{
		startPlaying();
	}
	
	/**
	 * This will update the seek bar
	 */
	private void startPlayProgressUpdater()
	{
		if (mPlayer.isPlaying())
		{
			seekBar.setProgress(mPlayer.getCurrentPosition());
			
			Runnable notification = new Runnable()
			{
				public void run()
				{
					startPlayProgressUpdater();
				}
			};
			handler.postDelayed(notification, 1000);
		}
	}

	/**
	 * This create a view that has a play/pause button and a seek bar
	 * 
	 * @param state
	 *            The state to add view to
	 * @return View with button and seek view
	 */
	public AGELinearLayout addMediaPlayerView(GameState state)
	{
		AGELinearLayout ageLL = new AGELinearLayout(state);

		state.setLayout(ageLL);
		((LinearLayout) state.getViewGroup()).setGravity(Gravity.CENTER);

		state.getViewGroup().addView(addSeekBar(state));
		state.getViewGroup().addView(addButtons());
		return ageLL;
	}

	/**
	 * This will create just a seek bar view that shows position in media
	 * 
	 * @param state
	 *            The state to add view to
	 * @return Seek bar object
	 */
	private SeekBar addSeekBar(GameState state)
	{
		seekBar = new SeekBar(state);
		seekBar.setMax(mPlayer.getDuration());
		seekBar.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				seekChange(v);
				return false;
			}

			private void seekChange(View v)
			{
				if (mPlayer.isPlaying())
				{
					SeekBar sb = (SeekBar) v;
					mPlayer.seekTo(sb.getProgress());
				}
			}
		});

		return seekBar;
	}
	
	/**
	 * Creates a new layout with play and stop buttons
	 * @return Layout with the buttons for stopping and playing
	 */
	private LinearLayout addButtons(){
		LinearLayout buttons = new LinearLayout(mContext);

		buttons.setOrientation(LinearLayout.HORIZONTAL);
		buttons.setGravity(Gravity.CENTER);
		
		buttons.addView(addPlayButton(buttons.getContext()));
		buttons.addView(addStopButton(buttons.getContext()));
		
		return buttons;
	}

	/**
	 * This will create just a play/pause button view that toggles playing and
	 * pausing
	 * @param context The view to add to
	 * @return Button object
	 */
	private Button addPlayButton(Context context)
	{
		btnPlayPause = new Button(context);
		btnPlayPause.setText("Play");
		btnPlayPause.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		btnPlayPause.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (btnPlayPause.getText().equals("Play"))
				{
					startPlaying();
					btnPlayPause.setText("Pause");
				}
				else
				{
					pausePlaying();
					btnPlayPause.setText("Play");
				}
			}
		});
		return btnPlayPause;
	}
	
	/**
	 * This creates the stop button
	 * @param context The view to add to
	 * @return Stop button
	 */
	private Button addStopButton(Context context){
		btnStop = new Button(context);
		btnStop.setText("Stop");
		btnStop.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		btnStop.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				stopPlaying();
			}
		});

		return btnStop;
	}
}
