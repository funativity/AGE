package funativity.age.opengl.animation;

import funativity.age.opengl.Drawable;

/**
 * This class holds animation data. It is intended to be used with animations
 * with multiple frames. The logic to change the current frame is handled in
 * here.
 * 
 * @author riedla
 * 
 */
public abstract class AnimatedMesh implements Drawable
{
	// if there is no frame rate
	private static final float NO_FRAME_RATE = 0;

	/**
	 * Change frame timing
	 */
	private float frameDelay = 0.5f;

	/**
	 * Current frames timing status
	 */
	private float currentFrameDelay = 0;

	/**
	 * current frame of this animation
	 */
	private int frameIndex = 0;

	/**
	 * number of frames in this animation
	 */
	private int frameCount = 0;

	/**
	 * Default constructor calls reset on this animation.
	 */
	public AnimatedMesh()
	{
		reset();
	}

	/**
	 * Get the number of frames that make up this animation
	 * 
	 * @return number of frames
	 */
	public int getFrameCount()
	{
		return frameCount;
	}

	/**
	 * Get the index of the current frame. This is set in the update method
	 * 
	 * @return current frame's index
	 */
	public int getFrameIndex()
	{
		return frameIndex;
	}

	/**
	 * Get the current frame delay. This is a timer that goes up each frame.
	 * Once it hits a value defined by setFrameRate(), this animation goes to
	 * the next frame.
	 * 
	 * @return time on current frame in seconds
	 */
	protected float getCurrentFrameDelay()
	{
		return currentFrameDelay;
	}

	/**
	 * Get the amount of time that is used to delay on each frame.
	 * 
	 * @return delay time in seconds
	 */
	protected float getFrameDelay()
	{
		return frameDelay;
	}

	/**
	 * Set how many frames are used in this animation. This value is used for
	 * updating the frame. If count<=0 there will be undefined results
	 * 
	 * @param count
	 *            total number of frames for this animation
	 */
	protected void setFrameCount(int count)
	{
		this.frameCount = count;
	}

	/**
	 * Set the current frame of this animation to the given frame index.
	 * 
	 * @param index
	 *            Frame to index to
	 */
	public void setFrameIndex(int index)
	{
		this.frameIndex = index % frameCount;
	}

	/**
	 * Set how fast frames change for this animation in Frames Per Second. Also
	 * resets the duration this sprite has been on this frame.
	 * 
	 * @param fps
	 *            number of frames per second this animation has
	 */
	public void setFrameRate(float fps)
	{
		if (fps <= NO_FRAME_RATE)
			frameDelay = NO_FRAME_RATE;
		else
			frameDelay = 1 / fps;
	}

	/**
	 * Reset this animation to the first frame
	 */
	public void reset()
	{
		currentFrameDelay = 0;
		frameIndex = 0;
		update(0);
	}

	/**
	 * Update this animation. This is where the changing of frames is handled.
	 * This should be called every frame inside the main update loop.
	 * 
	 * @param delta
	 *            number of seconds since last update
	 */
	@Override
	public void update(float delta)
	{
		// if it is worth updating the frame
		if (frameCount > 1 && frameDelay > NO_FRAME_RATE)
		{
			// keep updating the frame until we get to the right spot (or not at
			// all)
			while ((currentFrameDelay += delta) >= frameDelay)
			{
				currentFrameDelay -= frameDelay;
				setFrameIndex(frameIndex + 1);
			}
		}
	}
}
