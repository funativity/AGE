package funativity.age.opengl.animation;

import java.util.ArrayList;

import funativity.age.opengl.AGEColor;
import funativity.age.opengl.Mesh;
import funativity.age.opengl.shaders.SimpleAnimatedTechnique;

/**
 * 
 * Class that holds information for an animated model. Intended for keyframe
 * animation. Creates a mesh for each frame, and renders the current frame.
 * 
 */
public class AnimatedModel extends AnimatedMesh
{
	// the frames that make up this animation
	private ArrayList<Mesh> frames = new ArrayList<Mesh>();

	// technique that all frames are using to render
	private SimpleAnimatedTechnique technique;

	// underlying color that is applied to all frames
	private AGEColor color = new AGEColor();

	/**
	 * Default constructor. Sets the technique used to draw this model to the
	 * SimpleAnimatedTechnique
	 */
	public AnimatedModel()
	{
		technique = SimpleAnimatedTechnique.getTechnique();
	}

	/**
	 * Get how much to blend the current and next frame together. Values
	 * returned are between 0 and 1. 0 for 100% current frame, and 1 for 100%
	 * next frame.
	 * 
	 * @return amount to blend current and next frame.
	 */
	private float getBlend()
	{
		return getCurrentFrameDelay() / getFrameDelay();
	}

	@Override
	public void draw()
	{
		// dont try to draw if there is nothing to draw
		if (getFrameCount() < 1)
			return;

		// draw using the current frame
		Mesh frame = frames.get(getFrameIndex());

		// make sure the frame is using the same technique and color
		frame.setTechnique(technique);
		frame.setColor(color);

		// set blend
		technique.getShaderProgram().useProgram();
		technique.setFrameBlend(getBlend());

		// draw the current frame
		frame.draw();
	}

	/**
	 * Add a frame to this animation.
	 * 
	 * @param frame
	 *            to be added to the animation
	 */
	public void addFrame(Mesh frame)
	{
		frames.add(frame);
		setFrameCount(frames.size());
	}

	@Override
	public AGEColor getColor()
	{
		return color;
	}

	@Override
	public void setColor(AGEColor color)
	{
		this.color = color;
	}

	/**
	 * Set the technique that is used for all frames of this animation.
	 * 
	 * @param technique
	 *            what technique to use
	 */
	public void setTechnique(SimpleAnimatedTechnique technique)
	{
		this.technique = technique;
	}

	/**
	 * get the technique that is being used by this animation.
	 * 
	 * @return technique that this animation is using
	 */
	public SimpleAnimatedTechnique getTechnique()
	{
		return technique;
	}
}
