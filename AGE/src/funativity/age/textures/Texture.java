package funativity.age.textures;

import android.opengl.GLES20;

/**
 * OpenGL Texture wrapper class. Intended to be used with the TextureLoader
 * class, but manual loading can be done. Creating an instance of a Texture will
 * create an OpenGL textureID. Bind this texture, then load the texture data. It
 * is up to the loader (TextureLoader or whatever else is loading the texture)
 * to set the height and width of the texture.
 * 
 * @author riedla
 * 
 */
public class Texture
{
	/*
	 * Texture Unit values are used in shaders to change where the texture is
	 * bound to. Used for mutli-texturing
	 */
	public static final int TEXTURE_UNIT_0 = GLES20.GL_TEXTURE0;
	public static final int TEXTURE_UNIT_1 = GLES20.GL_TEXTURE1;
	public static final int TEXTURE_UNIT_2 = GLES20.GL_TEXTURE2;
	public static final int TEXTURE_UNIT_3 = GLES20.GL_TEXTURE3;
	public static final int TEXTURE_UNIT_4 = GLES20.GL_TEXTURE4;
	public static final int TEXTURE_UNIT_5 = GLES20.GL_TEXTURE5;
	public static final int TEXTURE_UNIT_6 = GLES20.GL_TEXTURE6;
	public static final int TEXTURE_UNIT_7 = GLES20.GL_TEXTURE7;

	// type of texture (Texture_2D, Texture_1D...)
	private int target;

	// OpenGL handle on texture
	private int textureID;

	// OpenGL texture array index
	private int textureUnit = TEXTURE_UNIT_0;

	// if this texture was loaded from a file, this is that reference
	private String imageRef;

	// min and mag filter for this texture
	private int minFilter, magFilter;

	// size of texture
	private int width;
	private int height;

	/**
	 * Create a texture. Will generate a texture ID which is used to bind this
	 * texture to the OpenGL context. <BR>
	 * <BR>
	 * The 'target' parameter is the OpenGL texture target. For common textures,
	 * GL_TEXTURE_2D is used.
	 * 
	 * @param target
	 */
	public Texture(int target)
	{
		this.target = target;
		this.textureID = generateTextureID();
	}

	/**
	 * Bind this texture to the context. Binds the texture to the current
	 * texture unit (default is 0)
	 */
	public void bind()
	{
		GLES20.glActiveTexture(textureUnit);
		GLES20.glBindTexture(target, textureID);
	}

	/**
	 * Delete this texture out of the OpenGL context
	 */
	public void delete()
	{
		// remove data from OpenGL
		GLES20.glDeleteTextures(1, new int[] { textureID }, 0);

		// reset id so this texture cant be used anymore
		textureID = 0;
	}

	/**
	 * Get the height of this texture. The value this returns should be set from
	 * the TextureLoader class.
	 * 
	 * 
	 * @return height of this texture in pixels
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * Get the reference to this texture. If this texture was loaded using the
	 * TextureLoader class, and came from a file, this value is set to the
	 * location that was used to load this texture.
	 * 
	 * @return reference to this texture
	 */
	public String getImageRef()
	{
		return imageRef;
	}

	/**
	 * OpenGL texture ID. Used to bind/delete this texture. Should rarely be
	 * used outside of this class
	 * 
	 * @return
	 */
	public int getTextureID()
	{
		return textureID;
	}

	public void setTextureID(int ID)
	{
		textureID = ID;
	}

	/**
	 * Get the texture sampler index that is assigned to this texture. Default
	 * TextureUnit is TEXTURE_UNIT_0. This value is used when passing
	 * information to the shader programs. TextureUnits and TextureSampleIndexs
	 * generally are used when multi-texturing.
	 * 
	 * @return textureUnitIndex assigned to this texture
	 */
	public int getTextureSamplerIndex()
	{
		switch (textureUnit)
		{
			case TEXTURE_UNIT_0:
				return 0;
			case TEXTURE_UNIT_1:
				return 1;
			case TEXTURE_UNIT_2:
				return 2;
			case TEXTURE_UNIT_3:
				return 3;
			case TEXTURE_UNIT_4:
				return 4;
			case TEXTURE_UNIT_5:
				return 5;
			case TEXTURE_UNIT_6:
				return 6;
			case TEXTURE_UNIT_7:
				return 7;
			default:
				return 0;
		}
	}

	/**
	 * Get the width of this texture. The value this returns should be set from
	 * the TextureLoader class.
	 * 
	 * 
	 * @return width of this texture in pixels
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * Set the height of this texture.
	 * 
	 * @param height
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}

	/**
	 * Set the reference used to load this texture. If this texture was not
	 * loaded from an external file, do not use this method.
	 * 
	 * @param ref
	 */
	public void setImageRef(String ref)
	{
		this.imageRef = ref;
	}

	/**
	 * Assign rendering parameter to this texture. <BR>
	 * NOTE: This method should ONLY be used while this texture is bound.
	 * Undefined results otherwise.
	 * 
	 * @param pname
	 *            Name of variable being changed
	 * @param param
	 *            Value variable is being changed to
	 */
	public void setTexParameter(int pname, float param)
	{
		GLES20.glTexParameterf(target, pname, param);
	}

	/**
	 * Assign rendering parameter to this texture. <BR>
	 * NOTE: This method should ONLY be used while this texture is bound.
	 * Undefined results otherwise.
	 * 
	 * @param pname
	 *            Name of variable being changed
	 * @param param
	 *            Value variable is being changed to
	 */
	public void setTexParameter(int pname, int param)
	{
		GLES20.glTexParameteri(target, pname, param);
	}

	/**
	 * Set the texture unit that is assigned to this texture. Default
	 * TextureUnit is TEXTURE_UNIT_0. This value is used when passing
	 * information to the shader programs. TextureUnits and TextureUnitIndexs
	 * generally are used when multi-texturing.
	 * 
	 * <BR>
	 * <BR>
	 * 
	 * Accepted values are publicly available from this class. Only the first 8
	 * are exposed, if more are needed, values can be pulled from OpenGLES20
	 * 
	 * @param textureUnit
	 *            assign this texture to a specific textureUnit
	 */
	public void setTextureUnit(int textureUnit)
	{
		this.textureUnit = textureUnit;
	}

	/**
	 * Set the width of this texture.
	 * 
	 * @param width
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}

	/**
	 * Unbind this texture. In reality this method unbinds all textures from
	 * what ever target this texture is assigned to.
	 */
	public void unbind()
	{
		GLES20.glBindTexture(target, 0);
	}

	/**
	 * Set both the minification filter as well as the magnification filter that
	 * is used by this texture during rendering. This method assumes this
	 * texture is bound.
	 * 
	 * @param minFilter
	 *            minification filter to use for this texture
	 * @param magFilter
	 *            minification filter to use for this texture
	 */
	public void setMinMagFilter(int minFilter, int magFilter)
	{
		setTexParameter(GLES20.GL_TEXTURE_MIN_FILTER, minFilter);
		setTexParameter(GLES20.GL_TEXTURE_MAG_FILTER, magFilter);
		this.minFilter = minFilter;
		this.magFilter = magFilter;
	}

	/**
	 * Get the mag filter value that has been assigned to this texture.
	 * 
	 * @return MagFilter
	 */
	public int getMagFilter()
	{
		return magFilter;
	}

	/**
	 * Get the min filter value that has been assigned to this texture.
	 * 
	 * @return MinFilter
	 */
	public int getMinFilter()
	{
		return minFilter;
	}

	/**
	 * Get the OpenGL target this texture is binding to
	 * 
	 * @return target this texture is binding to
	 */
	public int getTarget()
	{
		return target;
	}

	/**
	 * Generate an ID for a new texture. See OpenGL 'glGenTextures()'
	 * specification for more information.
	 * 
	 * @return unique ID for a texture
	 */
	public static int generateTextureID()
	{
		int[] id = new int[1];
		GLES20.glGenTextures(1, id, 0);
		return id[0];
	}
}
