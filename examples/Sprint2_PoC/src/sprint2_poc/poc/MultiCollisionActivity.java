package sprint2_poc.poc;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import funativity.age.collision.CollisionAAB;
import funativity.age.collision.CollisionListener;
import funativity.age.collision.CollisionShape;
import funativity.age.collision.CollisionSphere;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.Entity;
import funativity.age.opengl.MM;
import funativity.age.opengl.Mesh;
import funativity.age.opengl.primitive.Rectangle;
import funativity.age.opengl.primitive.TriangleFan;
import funativity.age.state.GameState;
import funativity.age.state.Scene;
import funativity.age.state.layout.AGELinearGLView;

public class MultiCollisionActivity extends GameState
{
	@Override
	public void init()
	{

		AGELinearGLView ageGL = new AGELinearGLView(this);
		setLayout(ageGL);
		scene scene = new scene(this);
		ageGL.setScene(scene);
	}

	private static class scene extends Scene
	{
		private static final Random rand = new Random();
		private static final float MAX_SPEED = 7;
		private static final float MIN_SPEED = 0.5f;

		private static final int RECTANGLE_COUNT = 5;
		private static final int CIRCLE_COUNT = 5;

		private static final float SCREEN_SIZE = 20;

		private float width;
		private float height;

		public scene(Context context)
		{
			super(context);
		}

		@Override
		public void init()
		{
			for (int i = 0; i < RECTANGLE_COUNT; i++)
			{
				createRectangle(getRandomSize(), getRandomSize(),
						getRandomColor());
			}

			for (int i = 0; i < CIRCLE_COUNT; i++)
			{
				createCircle(getRandomSize(), getRandomColor());
			}
		}

		@Override
		public void loadResources()
		{

		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			// setup 2D screen
			float ratio = (float) width / height;
			this.width = SCREEN_SIZE * ratio;
			this.height = SCREEN_SIZE;

			float hw = this.width / 2f;
			float hh = this.height / 2f;

			MM.ortho(-hw, hw, hh, -hh);

			// normal screen chane update
			super.onSurfaceChanged(gl, width, height);
		}

		private void createCircle(float radius, AGEColor color)
		{
			Mesh m = new TriangleFan(radius, 20);
			m.setColor(color);

			// create entity using mesh, and add it to scene
			Entity e = getSpecialEntity(m);
			addEntity(e);

			// create collision shape w/ entity, and add it to collisionManager
			CollisionShape c = new CollisionSphere(e, getCollisionListener(),
					radius);
			getCollisionManager().addChild(c, 0);
		}

		private void createRectangle(float width, float height, AGEColor color)
		{
			// create mesh
			Mesh m = new Rectangle(width, height);
			m.setColor(color);

			// create entity using mesh, and add it to scene
			Entity e = getSpecialEntity(m);
			addEntity(e);

			// create collision shape w/ entity, and add it to collisionManager
			CollisionShape c = new CollisionAAB(e, getCollisionListener(),
					width, height);
			getCollisionManager().addChild(c, 0);

		}

		private Entity getSpecialEntity(Mesh m)
		{
			Entity e = new Entity(m)
			{
				@Override
				public void update(Scene scene, float delta)
				{
					// normal updates
					super.update(scene, delta);

					// if we hit a wall bounce back
					if ((this.getX() > width / 2f && this.getDX() > 0)
							|| (this.getX() < -width / 2f && this.getDX() < 0))
					{
						this.setDX(-this.getDX());
					}

					if ((this.getY() > height / 2f && this.getDY() > 0)
							|| (this.getY() < -height / 2f && this.getDY() < 0))
					{
						this.setDY(-this.getDY());
					}
				}
			};

			// get bounds of starting entities, cannot be too close to the edges
			// (uses screen size, because sometimes onSurfaceChanged isnt called
			// yet, so height and width are still 0.
			float w = SCREEN_SIZE * 0.6f;
			float h = SCREEN_SIZE * 0.8f;

			// place this entity in a random spot on the screen
			e.setX((rand.nextFloat() - 0.5f) * w);
			e.setY((rand.nextFloat() - 0.5f) * h);

			// set the speed of this entity to a random value that is at least
			// faster than MIN_SPEED
			do
			{
				e.setDX((rand.nextFloat() - 0.5f) * MAX_SPEED);
				e.setDY((rand.nextFloat() - 0.5f) * MAX_SPEED);
			}
			while (e.getDX() * e.getDX() + e.getDY() * e.getDY() < MIN_SPEED
					* MIN_SPEED);

			return e;
		}

		private float getRandomSize()
		{
			final float MIN_SIZE = 0.5f;
			final float MAX_SIZE = 2;
			return rand.nextFloat() * (MAX_SIZE - MIN_SIZE) + MIN_SIZE;
		}

		private AGEColor getRandomColor()
		{
			float r, g, b;

			// ensure the random color is bright enough
			do
			{
				r = rand.nextFloat();
				g = rand.nextFloat();
				b = rand.nextFloat();
			}
			while (r * r + g * g + b * b < 0.25f);

			return new AGEColor(r, g, b);
		}

		private CollisionListener getCollisionListener()
		{
			return new CollisionListener()
			{
				@Override
				public boolean isCollide(CollisionShape shape1,
						CollisionShape shape2, float delta)
				{
					return shape1.isIntersect(shape2);
				}

				@Override
				public void onCollide(CollisionShape shape1,
						CollisionShape shape2, float delta)
				{
					Entity e1 = shape1.getEntity();
					Entity e2 = shape2.getEntity();

					float dx = e1.getX() - e2.getX();
					float dy = e1.getY() - e2.getY();

					// make sure the 2 objects go away from each other
					if (dx < dy)
					{
						if (dy < 0)
						{
							e1.setDY(-abs(e1.getDY()));
							e2.setDY(abs(e2.getDY()));
						}
						else
						{
							e1.setDY(abs(e1.getDY()));
							e2.setDY(-abs(e2.getDY()));
						}
					}
					else
					{
						if (dx < 0)
						{
							e1.setDX(-abs(e1.getDX()));
							e2.setDX(abs(e2.getDX()));
						}
						else
						{
							e1.setDX(abs(e1.getDX()));
							e2.setDX(-abs(e2.getDX()));
						}
					}
				}

				private float abs(float x)
				{
					// Faster implementation than Math.abs(). Does not check as
					// many cases though.
					return x < 0 ? -x : x;
				}
			};
		}
	}// end scene
}// end activity
