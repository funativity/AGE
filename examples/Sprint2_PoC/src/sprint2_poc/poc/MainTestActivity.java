package sprint2_poc.poc;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import funativity.age.state.GameState;
import funativity.age.state.layout.AGELinearLayout;

public class MainTestActivity extends GameState
{
	private static final LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
			400, 90);

	@Override
	public void init()
	{

		AGELinearLayout ageLL = new AGELinearLayout(this);

		this.setLayout(ageLL);
		((LinearLayout) this.getViewGroup()).setGravity(Gravity.CENTER);

		// Text View
		TextView tv = new TextView(this);
		tv.setText("Welcome to the AGE Proof of Concept!");
		tv.setTextSize(50);
		tv.setGravity(Gravity.CENTER);

		this.getViewGroup().addView(tv);

		addStateButton("Overlay State", OverlayActivity.class);
		addStateButton("Sprite State", SpriteActivity.class);
		addStateButton("Box State", BoxActivity.class);
		addStateButton("Shape State", ShapeActivity.class);
		addStateButton("Collision State", CollisionActivity.class);
		addStateButton("Multi-Collision State", MultiCollisionActivity.class);
		addStateButton("MeshLoader State", MeshLoaderActivity.class);
		addStateButton("Mesh Animation State", AnimatedActivity.class);

	}

	private void addStateButton(String buttonText, final Class<?> classType)
	{
		Button button = new Button(this);
		button.setText(buttonText);
		button.setLayoutParams(buttonParams);
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Log.i("my app", "hey maybe we are doing something");
				Intent intent = new Intent(MainTestActivity.this, classType);
				startActivity(intent);
			}
		});
		this.getViewGroup().addView(button);
	}

}
