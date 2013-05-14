package funativity.age.opengl.primitive;

import java.io.InputStream;

import android.content.res.Resources;

import funativity.age.R;
import funativity.age.error.OversizedMeshException;
import funativity.age.opengl.Mesh;
import funativity.age.opengl.meshloader.MeshLoader;
import funativity.age.util.Logger;

/**
 * OpenGL mesh representing a sphere.
 */
public class Sphere
{
	/**
	 * Represents the complexity of the OpenGL mesh. Higher quality spheres use
	 * more vertices and faces, and vice versa.
	 */
	public enum Quality
	{
		LOW(R.raw.sphere_low), MEDIUM_LOW(R.raw.sphere_medium_low), MEDIUM(
				R.raw.sphere_medium), MEDIUM_HIGH(R.raw.sphere_medium_high), HIGH(
				R.raw.sphere_high);

		private int resId;

		private Quality(int resId)
		{
			this.resId = resId;
		}

		/**
		 * @return sphere's resource id
		 */
		public int getResId()
		{
			return resId;
		}
	}

	// No constructor.
	private Sphere()
	{

	}

	/**
	 * Creates a Mesh representing a sphere centered on the origin.
	 * 
	 * @param quality
	 *            level of detail
	 * @param res
	 *            Activity's Resources
	 * @return Mesh representing a sphere, null if the sphere file is too
	 *         detailed to load
	 */
	public static Mesh build(Quality quality, Resources res)
	{
		try
		{
			InputStream is = MeshLoader.openResource(quality.getResId(), res);
			return MeshLoader.loadMesh(MeshLoader.FileType.OBJ_FILE, is);
		}
		catch (OversizedMeshException e)
		{
			Logger.e("Sphere file is too complex to load.");
		}

		return null;
	}
}