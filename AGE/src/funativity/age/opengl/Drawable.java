package funativity.age.opengl;

/**
 * Interface that all OpenGL drawable objects are based off of.
 * 
 * @author riedla
 * 
 */
public interface Drawable
{
	/**
	 * Draw this object.
	 */
	public void draw();

	/**
	 * All logic that needs to be ran every frame is put in this method. Use
	 * delta to update everything based on time instead of frame rate.
	 * 
	 * @param delta
	 *            Time since last update in seconds
	 */
	public void update(float delta);

	/**
	 * Get underlying color of this object
	 * 
	 * @return color of this object
	 */
	public AGEColor getColor();

	/**
	 * Set the underlying color of this object
	 * 
	 * @param color
	 *            new color of this object
	 */
	public void setColor(AGEColor color);
}
