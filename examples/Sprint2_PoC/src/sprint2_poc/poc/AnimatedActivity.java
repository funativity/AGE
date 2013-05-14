package sprint2_poc.poc;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.view.MotionEvent;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.Entity;
import funativity.age.opengl.MM;
import funativity.age.opengl.meshloader.MeshLoader;
import funativity.age.opengl.shaders.SimpleAnimatedTechnique;
import funativity.age.state.GameState;
import funativity.age.state.Scene;
import funativity.age.state.layout.AGELinearGLView;
import funativity.age.util.Geometry3f;

public class AnimatedActivity extends GameState
{
	private Entity mesh;

	@Override
	public void init()
	{
		// normal opengl activity setup
		AGELinearGLView ageGL = new AGELinearGLView(this);
		this.setLayout(ageGL);
		AnimatedScene scene = new AnimatedScene(this);
		ageGL.setScene(scene);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy)
	{
		final float scale = -0.2f;

		mesh.setRX(mesh.getRX() + dy * scale);
		mesh.setRY(mesh.getRY() + dx * scale);

		return true;
	}

	private class AnimatedScene extends Scene
	{
		public AnimatedScene(Context context)
		{
			super(context);
		}

		@Override
		public void init()
		{
			MM.lookAt(0, 0, 2, 0, 0, 0, 0, 1, 0);
			GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1);

			// setup depth test
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);

			// setup culling
			GLES20.glFrontFace(GLES20.GL_CCW);
			GLES20.glCullFace(GLES20.GL_BACK);
			GLES20.glEnable(GLES20.GL_CULL_FACE);

			try
			{
				// create and add animation to screen
				mesh = new Entity();
				mesh.setDrawable(new MeshLoader().loadAnimation("mesh",
						"boxman_", MeshLoader.FileType.OBJ_FILE, getAssets()));
				addEntity(mesh);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			super.onSurfaceChanged(gl, width, height);

			// setup opengl to adjust for size of screen.
			MM.perspective(45f, (float) width / height, 0.05f, 10);
		}

		@Override
		public void loadResources()
		{

		}

		@Override
		public void update(float delta)
		{
			super.update(delta);

			Geometry3f direction = new Geometry3f(-3, -1, -2);
			AGEColor color = new AGEColor(1f, 1f, 1f);
			SimpleAnimatedTechnique.getTechnique().getShaderProgram()
					.useProgram();
			SimpleAnimatedTechnique.getTechnique().setDirectionalLight(color,
					0.5f, 0.5f, direction);
		}

	}

}
