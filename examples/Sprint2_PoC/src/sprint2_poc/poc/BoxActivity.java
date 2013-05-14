package sprint2_poc.poc;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;
import funativity.age.opengl.Entity;
import funativity.age.opengl.MM;
import funativity.age.opengl.primitive.Box;
import funativity.age.state.GameState;
import funativity.age.state.Scene;
import funativity.age.state.layout.AGELinearGLView;
import funativity.age.textures.TextureLoader;

public class BoxActivity extends GameState
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

			Box box = new Box(1, 1, 1);

			try
			{
				box.setTexture(TextureLoader.getTexture(
						"images/textures/crate.jpg", getAssets()));
			}
			catch (IOException e1)
			{
				Log.e("DEBUG", "Failed to load texture.", e1);
			}

			e = new Entity(box);
			addEntity(e);
		}

		@Override
		public void update(float delta)
		{
			super.update(delta);

			GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1);
		}

		@Override
		public void loadResources()
		{

		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			MM.perspective(45f, (float) width / height, 0.05f, 10);
			MM.lookAt(0, 0, 4, 0, 0, 0, 0, 1, 0);

			super.onSurfaceChanged(gl, width, height);
		}
	}

}
