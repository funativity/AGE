package sprint2_poc.poc;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import funativity.age.collision.CollisionListener;
import funativity.age.collision.CollisionManager;
import funativity.age.collision.CollisionResult;
import funativity.age.collision.CollisionSphere;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.Entity;
import funativity.age.opengl.MM;
import funativity.age.opengl.primitive.TriangleFan;
import funativity.age.state.GameState;
import funativity.age.state.Scene;
import funativity.age.state.layout.AGELinearGLView;
import funativity.age.util.Geometry3f;

public class CollisionActivity extends GameState
{
	@Override
	public void init()
	{
		// OpenGL underneath.
		AGELinearGLView ageGL = new AGELinearGLView(this);
		setLayout(ageGL);
		CollisionScene scene = new CollisionScene(this);
		ageGL.setScene(scene);
	}

	private class CollisionScene extends Scene
	{
		public CollisionScene(Context context)
		{
			super(context);
		}

		@Override
		public void init()
		{
			// Some shared variables.
			float radius = 0.2f;
			float position = 0.5f;
			float speed = 0.2f;
			float acceleration = 0.1f;
			CollisionManager collisionManager = getCollisionManager();

			// Circle 1.
			// Make the circle.
			TriangleFan circle1 = new TriangleFan(radius, 50);
			AGEColor color1 = new AGEColor(0, 0, 1);
			circle1.setColor(color1);

			// Make the circle's location, velocity, and acceleration.
			Geometry3f position1 = new Geometry3f(position, position);
			Geometry3f velocity1 = new Geometry3f(-speed, -speed);
			Geometry3f accel1 = new Geometry3f(-acceleration, -acceleration);

			// Add the circle to the scene.
			Entity entity1 = new Entity(circle1, position1, velocity1, accel1);
			addEntity(entity1);

			// Set the circle's collision.
			CollisionListener listener1 = CollisionResult
					.getSwapVelocityListener();
			CollisionSphere collision1 = new CollisionSphere(entity1,
					listener1, radius);
			collisionManager.addChild(collision1, 0);

			// Circle 2.
			// Make the circle.
			TriangleFan circle2 = new TriangleFan(radius, 50);
			AGEColor color2 = new AGEColor(0, 1, 0);
			circle2.setColor(color2);

			// Make the circle's location, velocity, and acceleration.
			Geometry3f position2 = new Geometry3f(-position, -position);
			Geometry3f velocity2 = new Geometry3f(speed, speed);
			Geometry3f accel2 = new Geometry3f(acceleration, acceleration);

			// Add the circle to the scene.
			Entity entity2 = new Entity(circle2, position2, velocity2, accel2);
			addEntity(entity2);

			// Set the circle's collision.
			CollisionListener listener2 = CollisionResult
					.getSwapVelocityListener();
			CollisionSphere collision2 = new CollisionSphere(entity2,
					listener2, radius);
			collisionManager.addChild(collision2, 0);
		}

		@Override
		public void loadResources()
		{

		}

		@Override
		public void update(float delta)
		{
			GLES20.glClearColor(1f, 0.5f, 0.2f, 1f);

			super.update(delta);
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