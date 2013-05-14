package sprint2_poc.poc;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.view.MotionEvent;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.Entity;
import funativity.age.opengl.MM;
import funativity.age.opengl.Mesh;
import funativity.age.opengl.primitive.Sphere;
import funativity.age.opengl.primitive.Sphere.Quality;
import funativity.age.opengl.shaders.SimpleLightingTechnique;
import funativity.age.state.GameState;
import funativity.age.state.Scene;
import funativity.age.state.layout.AGELinearGLView;
import funativity.age.util.Geometry3f;

public class MeshLoaderActivity extends GameState
{
	Entity e;

	@Override
	public void init()
	{
		// normal opengl activity setup
		AGELinearGLView ageGL = new AGELinearGLView(this);
		this.setLayout(ageGL);

		BoxScene scene = new BoxScene(this);
		ageGL.setScene(scene);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy)
	{
		final float scale = 0.2f;

		e.setRX(e.getRX() - dy * scale);
		e.setRY(e.getRY() - dx * scale);

		return true;
	}

	private class BoxScene extends Scene
	{

		public BoxScene(Context context)
		{
			super(context);
		}

		@Override
		public void init()
		{
			// setup depth test
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);

			// setup culling
			GLES20.glFrontFace(GLES20.GL_CCW);
			GLES20.glCullFace(GLES20.GL_BACK);
			GLES20.glEnable(GLES20.GL_CULL_FACE);

			/*
			 * Mesh mesh = null; try { String fileName = "mesh/CarvingBall.obj";
			 * InputStream is = MeshLoader.openAsset(fileName, getAssets());
			 * mesh = MeshLoader.loadMesh(MeshLoader.FileType.OBJ_FILE, is); }
			 * catch (OversizedMeshException e1) { // TODO Auto-generated catch
			 * block e1.printStackTrace(); }
			 */

			Mesh mesh = Sphere.build(Quality.LOW, getResources());

			mesh.setTechnique(SimpleLightingTechnique.getTechnique());
			mesh.setColor(new AGEColor(0.8f, 0.4f, 0.4f));
			e = new Entity(mesh);
			addEntity(e);

			GLES20.glClearColor(0, 0, 0, 1);
		}

		@Override
		public void update(float delta)
		{
			super.update(delta);

			Geometry3f direction = new Geometry3f(-3, -3, 0);
			AGEColor color = new AGEColor(1f, 1f, 1f);
			SimpleLightingTechnique.getTechnique().getShaderProgram()
					.useProgram();
			SimpleLightingTechnique.getTechnique().setDirectionalLight(color,
					0.0f, 0.8f, direction);
		}

		@Override
		public void loadResources()
		{

		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			MM.perspective(45f, (float) width / height, 0.1f, 10);
			MM.lookAt(0, 0, 5, 0, 0, 0, 0, 1, 0);
			// MM.loadIdentity();

			super.onSurfaceChanged(gl, width, height);
		}
	}

}
