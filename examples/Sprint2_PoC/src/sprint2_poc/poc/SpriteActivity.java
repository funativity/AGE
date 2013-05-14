package sprint2_poc.poc;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import funativity.age.opengl.Entity;
import funativity.age.opengl.MM;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.animation.AnimatedSprite;
import funativity.age.opengl.animation.SpriteMap;
import funativity.age.state.GameState;
import funativity.age.state.Scene;
import funativity.age.state.layout.AGELinearGLView;
import funativity.age.textures.Texture;
import funativity.age.textures.TextureLoader;
import funativity.age.util.Geometry3f;

public class SpriteActivity extends GameState
{
	@Override
	public void init()
	{
		// normal opengl activity setup
		AGELinearGLView ageGL = new AGELinearGLView(this);
		this.setLayout(ageGL);
		SpriteScene scene = new SpriteScene(this);
		ageGL.setScene(scene);
	}

	private class SpriteScene extends Scene
	{

		public SpriteScene(Context context)
		{
			super(context);
		}

		@Override
		public void init()
		{
			// setup alpha blending
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
					GLES20.GL_ONE_MINUS_SRC_ALPHA);
			GLES20.glEnable(GLES20.GL_BLEND);

			try
			{
				// create pacman
				AnimatedSprite s = new AnimatedSprite(0.1f, 0.1f);
				s.setFrameRate(12);
				s.setColor(new AGEColor(1, 1, 0));

				// load in all of the textures that make pacman up
				for (int i = 1; i < 6; i++)
				{
					String res = "images/pacman/pacman" + i + ".png";
					s.AddFrame(TextureLoader.getTexture(res, getAssets()));
				}

				// set pacman as an entity and add it to the list
				Entity sprite = new Entity(s, new Geometry3f(0, 0.7f),
						new Geometry3f(0, -0.2f));
				addEntity(sprite);

				// load in blinky (red ghost)
				Texture t = TextureLoader.getTexture(
						"images/pacman/blinky.png", getAssets());
				SpriteMap m = new SpriteMap(t, 0.1f, 0.1f, 16, 16, 2);
				m.setFrameRate(12);

				// set blinky as an entity and add it to the list
				sprite = new Entity(m, new Geometry3f(0, 0.9f), new Geometry3f(
						0, -0.2f));
				addEntity(sprite);
			}
			catch (IOException e)
			{
				Log.e("ERROR", "Failed to load textures", e);
			}

			MM.lookAt(0, 0, 1, 0, 0, 0, 0, 1, 0);
		}

		@Override
		public void update(float delta)
		{
			// normal update
			super.update(delta);

			// make sure background is black
			GLES20.glClearColor(0, 0, 0, 1);

			// if an entity is below the screen, move them to the top
			final float height = 0.8f;
			for (Entity e : getEntities())
			{
				if (e.getY() < -height)
				{
					e.setY(height);
				}
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
	}
}
