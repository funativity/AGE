package sprint2_poc.poc;

import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.opengl.GLES20;
import android.view.View;
import funativity.age.canvas.CanvasShape;
import funativity.age.canvas.Screen;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.Entity;
import funativity.age.opengl.MM;
import funativity.age.opengl.primitive.TriangleFan;
import funativity.age.state.GameState;
import funativity.age.state.Scene;
import funativity.age.state.layout.AGELinearGLView;
import funativity.age.util.Geometry3f;

public class ShapeActivity extends GameState
{
	@Override
	public void init()
	{
		// OpenGL underneath.
		AGELinearGLView ageGL = new AGELinearGLView(this);
		setLayout(ageGL);
		ShapeScene scene = new ShapeScene(this);
		ageGL.setScene(scene);

		// Canvas on top.
		getViewGroup().addView(new DrawableView(this));
	}

	private class DrawableView extends View
	{
		private CanvasShape shape;

		public DrawableView(Context context)
		{
			super(context);
		}

		protected void onDraw(Canvas canvas)
		{
			shape.draw(canvas);
		}

		// The oldw and oldh parameters give wrong results for some reason.
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{
			// Awkward, but needs to be done.
			Screen.calculateScreenSize(getContext());

			// Get the old screen size.
			int oldWidth = Screen.getScreenWidth();
			int oldHeight = Screen.getScreenHeight();

			// Update the screen size references now that we have old.
			Screen.setScreenWidth(w);
			Screen.setScreenHeight(h);

			if (shape == null)
			{
				// Create the shape.
				int shapeWidth = 100;
				int shapeHeight = 100;
				shape = new CanvasShape(CanvasShape.Type.ELLIPSE, shapeWidth,
						shapeHeight);

				// Give it a sick effect.
				LinearGradient gradient = new LinearGradient(0, 0, shapeWidth,
						shapeHeight, Color.RED, Color.GREEN,
						Shader.TileMode.CLAMP);
				shape.getPaint().setShader(gradient);
			}
			else
			{
				// Scale the shape for portrait/landscape.
				float widthScale = w / (float) oldWidth;
				float heightScale = h / (float) oldHeight;
				shape.scale(widthScale, heightScale);
			}
		}
	}

	private class ShapeScene extends Scene
	{
		public ShapeScene(Context context)
		{
			super(context);
		}

		@Override
		public void init()
		{

		}

		@Override
		public void loadResources()
		{

		}

		@Override
		public void update(float delta)
		{
			super.update(delta);

			addShape();
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			GLES20.glClearColor(1f, 0.5f, 0.2f, 1f);
			MM.perspective(45f, (float) width / height, 0.05f, 10);
			MM.lookAt(0, 0, 4, 0, 0, 0, 0, 1, 0);

			super.onSurfaceChanged(gl, width, height);
		}

		private void addShape()
		{
			Random random = new Random();

			int segments = random.nextInt(17) + 3;

			TriangleFan shape = new TriangleFan(segments);
			AGEColor color = new AGEColor(random.nextFloat(),
					random.nextFloat(), random.nextFloat());
			shape.setColor(color);

			Entity entity = new Entity(shape, new Geometry3f(), new Geometry3f(
					random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1,
					random.nextFloat() * 2 - 1));

			addEntity(entity);

			limitEntities(200);
		}

		// TODO: seems like it doesn't remove from memory.
		private void limitEntities(int size)
		{
			List<Entity> entities = getEntities();
			if (entities.size() > size)
			{
				this.removeEntity(entities.get(0));
			}
		}
	}
}