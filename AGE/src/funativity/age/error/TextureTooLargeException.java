package funativity.age.error;

/**
 * This exception is thrown when a texture is too large for the Android device to handle.
 * It will generally be thrown due to an underlying OutOfMemoryError when creating the BitMap
 * for the texture.
 */
public class TextureTooLargeException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
