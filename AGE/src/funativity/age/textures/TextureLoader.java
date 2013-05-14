package funativity.age.textures;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import funativity.age.error.TextureTooLargeException;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Load an OpenGL texture with different ways to load the teture.
 * 
 * 
 * @author riedla
 * 
 */
public class TextureLoader
{
	/**
	 * Holds all of the previously loaded textures
	 */
	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	private static final String NO_TEXTURE = "NO_TEXTURE";

	/**
	 * Get a 1x1 pixel white texture. Possible use: Don't want to use a texture
	 * but shader requires a texture bound for every draw call.
	 * 
	 * <BR>
	 * This call creates a single texture, and returns that same texture for
	 * every call afterwards. Because all calls to this method use the same
	 * texture, it is assumed that nothing will change the data in this texture
	 * (otherwise it will affect everything else that uses it)
	 * 
	 * @return blank 1x1 texture
	 */
	public static Texture getNoTexture()
	{
		// check to see if this texture was already made
		Texture notexture = textures.get(NO_TEXTURE);
		if (notexture != null)
			return notexture;

		// create a 1x1 bitmap to use to load into OpenGL memory
		Bitmap image = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);

		// set that one pixel to white
		image.setPixel(0, 0, Color.rgb(255, 255, 255));

		// create and store no texture
		notexture = getTexture(image, GLES20.GL_TEXTURE_2D, GLES20.GL_NEAREST,
				GLES20.GL_NEAREST);
		textures.put(NO_TEXTURE, notexture);

		image.recycle();

		// give new no texture to caller
		return notexture;
	}

	/**
	 * Reloads all the textures. Uses the textures same target as well as the
	 * same min, and mag filters as the original texture used.
	 * 
	 * @param am
	 *            The asset manager for the texture.
	 * @throws IOException
	 *             There was an IO exception thrown.
	 * @throws TextureTooLargeException 
	 * 			   If the image being used is too big for a Bitmap to be created
	 */
	public static void reloadAllTextures(AssetManager am) throws IOException, TextureTooLargeException
	{
		Set<String> set = textures.keySet();
		for (String key : set)
		{
			Texture oldTexture = textures.get(key);
			Texture newTexture = null;
			Bitmap image = null;

			if (key.equalsIgnoreCase(NO_TEXTURE))
			{
				// create a 1x1 bitmap to use to load into OpenGL memory
				image = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
				// set that one pixel to white
				image.setPixel(0, 0, Color.rgb(255, 255, 255));
			}
			else
			{
				image = loadImage(key, am);
			}

			// load resource into an image, then convert it to a texture
			newTexture = getTexture(image, oldTexture.getTarget(),
					oldTexture.getMinFilter(), oldTexture.getMagFilter());

			image.recycle();

			oldTexture.setTextureID(newTexture.getTextureID());
			oldTexture.setImageRef(key);

		}

	}

	/**
	 * Create a texture out of a bitmap. This does no checks for multiple of the
	 * same bitmap being loaded.
	 * 
	 * @param image
	 *            bitmap to load into a texture
	 * @param target
	 *            generally GL_TEXTURE_2D
	 * @param minFilter
	 *            How OpenGL handles making the image smaller
	 * @param magFilter
	 *            How OpenGL handles stretching the image
	 * @return texture The texture that was requested.
	 */
	public static Texture getTexture(Bitmap image, int target, int minFilter,
			int magFilter)
	{
		// if there is nothing to convert to a texture stop
		if (image == null || image.getHeight() <= 0 || image.getWidth() <= 0)
		{
			throw new IllegalArgumentException(
					"Cannot convert a null image into a texture");
		}

		// create a texture to represent the OpenGL texture
		Texture texture = new Texture(target);

		// setup sizes of texture
		texture.setWidth(image.getWidth());
		texture.setHeight(image.getHeight());

		// bind texture so all OpenGL texture calls go to this texture
		texture.bind();

		// setup sampling variables
		texture.setMinMagFilter(minFilter, magFilter);

		// load data into OpenGL
		GLUtils.texImage2D(target, 0, image, 0);

		// cleanup
		texture.unbind();
		image.recycle();

		// give texture to caller
		return texture;
	}

	/**
	 * Create a texture out of an image. Uses the AssetManager to import the
	 * data. Only one texture will be loaded from each resource location. The
	 * same texture will be returned if multiple calls are made using the same
	 * location. This is intended to save memory.
	 * 
	 * <BR>
	 * <BR>
	 * Texture loaded using GL_TEXTURE_2D as target GL_NEAREST for both min and
	 * mag filter. If different values are desired, use the loadImage method and
	 * the getTexture method that takes in a bitmap to load the image. That
	 * method has options to choose the filters.
	 * 
	 * @param resource
	 *            location to image
	 * @param am
	 *            AssetManager
	 * @return texture The texture that was requested
	 * @throws IOException
	 *             There was an IO exception.
	 * @throws TextureTooLargeException 
	 * 			   If the image being used is too big for a Bitmap to be created
	 */
	public static Texture getTexture(String resource, AssetManager am)
			throws IOException, TextureTooLargeException
	{
		// check to see if the texture is already loaded
		Texture tex = textures.get(resource);

		// if the texture has already been loaded return it right away.
		// if the texture was deleted, then we will have to reload it.
		if (tex != null && tex.getTextureID() != 0)
			return tex;

		// load resource into an image, then convert it to a texture
		tex = getTexture(loadImage(resource, am), GLES20.GL_TEXTURE_2D, // target
				GLES20.GL_NEAREST, // min filter
				GLES20.GL_NEAREST // mag filter
		);

		// if there was a problem with loading, return null
		if (tex == null)
			return null;

		// save the texture
		tex.setImageRef(resource);
		textures.put(resource, tex);

		// give the newly made texture back
		return tex;
	}

	/**
	 * Uses a bitmapFactory to convert an image into a bitmap. This method adds
	 * alpha to any pixel of the color 0xFF00FF. If the image format is not
	 * supported by the BitmapFactory, this method will return null;
	 * 
	 * @param ref
	 *            Path to the image file
	 * @param am
	 *            AssetManager for the current context
	 * @return the decoded image Bitmap image, null if the bitmap cannot be read
	 * @throws IOException
	 *             If there is a problem with the ref
	 * @throws TextureTooLargeException 
	 * 			   If the image being used is too big for a Bitmap to be created
	 */
	public static Bitmap loadImage(String ref, AssetManager am)
			throws IOException, TextureTooLargeException
	{
		// Simply decode the image
		Bitmap myBitmap = BitmapFactory.decodeStream(am.open(ref));

		if (myBitmap == null)
		{
			return null;
		}

		// Grab the pixels[] from the decoded bitmap
		int[] pixels = new int[myBitmap.getHeight() * myBitmap.getWidth()];
		myBitmap.getPixels(pixels, 0, myBitmap.getWidth(), 0, 0,
				myBitmap.getWidth(), myBitmap.getHeight());

		// Turn any pixel data with the color 0xFF00FF into a pixel with full
		// alpha
		for (int i = 0; i < pixels.length; i++)
		{
			// if ((pixels[i] >>> 16 & 0xFF) == 0xFF && (pixels[i] & 0xFF) ==
			// 0xFF)
			if ((pixels[i] & 0xFFFFFF) == 0xFF00FF)
			{
				pixels[i] = 0x00FF00FF;
			}
		}

		Bitmap retMap = null;
		
		try
		{
			retMap = Bitmap.createBitmap(pixels, myBitmap.getWidth(),
					myBitmap.getHeight(), myBitmap.getConfig());
		}
		catch( OutOfMemoryError e)
		{
			e.printStackTrace();
			throw( new TextureTooLargeException() );
		}
		
		return retMap;
	}
}
