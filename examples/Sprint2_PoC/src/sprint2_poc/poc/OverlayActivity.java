package sprint2_poc.poc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import com.amazonaws.services.dynamodb.model.AttributeValue;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import funativity.age.databases.DynamoDBManager;
import funativity.age.opengl.Entity;
import funativity.age.opengl.MM;
import funativity.age.opengl.primitive.TriangleFan;
import funativity.age.opengl.primitive.Triangle;
import funativity.age.state.GameState;
import funativity.age.state.Scene;
import funativity.age.state.layout.AGELinearGLView;
import funativity.age.util.Geometry3f;

public class OverlayActivity extends GameState
{

	private int numberOfCirclesAdded = 0;

	@Override
	public void init()
	{
		AGELinearGLView ageGL = new AGELinearGLView(this);

		this.setLayout(ageGL);

		final MyScene scene = new MyScene(this);

		ageGL.setScene(scene);

		TextView tv = new TextView(this);
		tv.setTextColor(Color.WHITE);
		tv.setText("This shows that we can overlay the OpenGL with Android UI.");

		Button button = new Button(this);
		button.setBackgroundColor(Color.YELLOW);
		button.setText("Add Circle...");
		button.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent e)
			{
				if (e.getAction() == MotionEvent.ACTION_DOWN)
				{
					v.setBackgroundColor(Color.MAGENTA);
				}
				else if (e.getAction() == MotionEvent.ACTION_UP)
				{
					v.setBackgroundColor(Color.YELLOW);
				}
				return false;
			}
		});

		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				glView.queueEvent(new Thread()
				{
					public void run()
					{
						scene.addCircle();
						numberOfCirclesAdded++;
					}
				});
			}
		});

		this.getViewGroup().addView(tv);
		this.getViewGroup().addView(button);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		// Save the number of circles we created
		DynamoDBManager mgr = null;
		try
		{
			mgr = new DynamoDBManager("/AwsCredentials.properties");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int totalCircles = Integer.parseInt(mgr.getData("Circles", "Total")
				.getN()) + numberOfCirclesAdded;
		AttributeValue tempValue = new AttributeValue().withN(String
				.valueOf(totalCircles));
		Map<String, AttributeValue> values = new HashMap<String, AttributeValue>();
		values.put("Total", tempValue);
		mgr.updateData(values, "Circles");

	}

	private class MyScene extends Scene
	{

		Entity triangle = null;

		public MyScene(Context context)
		{
			super(context);
		}

		public void addCircle()
		{
			Entity e = new Entity(new TriangleFan(0.25f, 50), new Geometry3f(
					-0.6f, 1f), new Geometry3f(0, -.4f));
			addEntity(e);

		}

		@Override
		public void init()
		{
			triangle = new Entity(new Triangle(0.5f, 0.5f));
			addEntity(triangle);

			MM.lookAt(0, 0, 5, 0, 0, 0, 0, 1, 0);

		}

		@Override
		public void update(float delta)
		{
			super.update(delta);

			final float height = 1.5f;
			for (final Entity e : getEntities())
			{
				if (e.getY() < -height)
				{
					glView.queueRemoveEntity(e);
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
