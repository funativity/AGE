package age.poc;

import age.dataModels.Boat;
import age.dataModels.Coordinate;
import age.enums.SubState;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import funativity.age.opengl.AGEColor;
import funativity.age.state.GameState;
import funativity.age.state.layout.AGELinearGLView;

/**
 * This is the main activity once user is inside a specific Battleship game
 * @author vancer
 *
 */
public class MainActivity extends GameState
{
	private GridScene scene;

	// Ship moving.
	private Coordinate start;
	private Boat boatToMove;
	private AGEColor original = new AGEColor();
	private AGEColor selectColor = new AGEColor(1f, 1f, 0);

	private Button updateFireButton;
	private Button changeViewButton;

	private MainActivity activity = this;

	public String userID;
	public String gameID;

	@Override
	public void init()
	{
		// Set layout.
		AGELinearGLView ageGL = new AGELinearGLView(this);
		setLayout(ageGL);

		// Set scene.
		scene = new GridScene(this);
		ageGL.setScene(scene);

		Intent intent = this.getIntent();
		userID = intent.getExtras().getString("USER_ID");
		gameID = intent.getExtras().getString("GAME_ID");

		// Add new UI
		addButtons();
		updateUI();
	}

	/**
	 * This button serves three functions - Lock in placement of the ships
	 * initially - Lock in a shot - Query the game state
	 */
	private void addButtons()
	{
		RelativeLayout layout = new RelativeLayout(this);

		// Update, Fire, Place Ships button
		RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		updateFireButton = new Button(activity);
		updateFireButton.setLayoutParams(buttonParams);
		updateFireButton.setId(12345);

		// Switch View button
		buttonParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		buttonParams.addRule(RelativeLayout.RIGHT_OF, updateFireButton.getId());
		changeViewButton = new Button(this);
		changeViewButton.setLayoutParams(buttonParams);
		changeViewButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				glView.queueEvent(new Thread()
				{
					public void run()
					{
						scene.setGrid(!scene.isMyGrid());
						scene.resetCameraPosition();
						scene.resetCameraZoom();
					}
				});
			}
		});

		layout.addView(updateFireButton);
		layout.addView(changeViewButton);
		activity.getViewGroup().addView(layout);
	}

	/**
	 * updates the Android UI based on the state of the game This should be
	 * called whenever a state change occurs
	 */
	public void updateUI()
	{
		SubState state = scene.subState;

		if (state == null)
		{
			return;
		}

		switch (state)
		{
			case PlaceShips:
			{
				updateFireButton.setText("Place Ships?");
				updateFireButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// check placement of ships
						if (scene.isValidPlacing())
						{
							// submit and change state to Fire
							scene.saveBoatLocations();
							scene.subState = SubState.View;
							scene.gameData.isCurrentUsersTurn = false;
							updateUI();
						}
						else
						{
							Toast.makeText(MainActivity.this,
									"Invalide boat positions.",
									Toast.LENGTH_LONG).show();
						}
					}
				});
				break;
			}
			case Fire:
			{
				updateFireButton.setText("Fire!!!");
				updateFireButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if (scene.isMyGrid())
						{
							// Switch display to opponents grid
							scene.setGrid(false);
						}
						else
						{
							// hit/miss
							Boat boat = scene.getBoatAt(scene.target);
							if (boat != null)
							{
								scene.target.hit = true;
								boat.hit();
							}
							else
							{
								scene.target.hit = false;
							}

							// submit shot
							scene.submitShot();

							// change state to View
							scene.subState = SubState.View;
							scene.gameData.isCurrentUsersTurn = false;
							scene.setGrid(false);// this call will update the UI
						}
					}
				});
				break;
			}
			case View:
			{
				updateFireButton.setText("Refresh?");
				updateFireButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// Refresh scene
						scene.refresh();// calls updateUI
					}
				});
				break;
			}
			default:
			{
				break;
			}
		}

		if (scene.isMyGrid())
		{
			changeViewButton.setText("View Opponent's grid");
		}
		else
		{
			changeViewButton.setText("View your grid");
		}
	}

	/**
	 * Determines if this is the ship placing phase.
	 * 
	 * @return true if so, false if not
	 */
	public boolean isPlacingPhase()
	{
		return scene.subState == SubState.PlaceShips && scene.isMyGrid();
	}

	/**
	 * Determines if this is the firing phase.
	 * 
	 * @return true if so, false if not
	 */
	public boolean isFiringPhase()
	{
		return scene.subState == SubState.Fire && !scene.isMyGrid();
	}

	/**
	 * Targets a coordinate with a reticle. Use null to hide the reticle.
	 * 
	 * @param target
	 *            target coordinate
	 */
	public void setTarget(final Coordinate target)
	{
		glView.queueEvent(new Thread()
		{
			public void run()
			{
				scene.setTarget(target);
			}
		});
	}

	/**
	 * Move boat and targeting.
	 */
	@Override
	public boolean onSingleTapConfirmed(MotionEvent event)
	{
		if (isPlacingPhase())
		{
			Coordinate coord = scene.getTouchCoordinate(event);
			if (coord != null)
			{
				if (boatToMove == null)
				{
					// Step 1 of moving.
					Boat boat = scene.getBoatAt(coord);
					if (boat != null)
					{
						// Set the state.
						start = coord;
						boatToMove = boat;
						boat.getEntity().getDrawable().setColor(selectColor);
					}
				}
				else
				{
					// Step 2 of moving.
					int dx = coord.x - start.x;
					int dy = coord.y - start.y;
					boatToMove.translate(dx, dy);

					// Reset the state.
					boatToMove.getEntity().getDrawable().setColor(original);
					boatToMove = null;
					start = null;
				}
			}
		}
		else if (isFiringPhase())
		{
			Coordinate coord = scene.getTouchCoordinate(event);
			if (coord != null)
			{
				// Target the tile with a reticle.
				if (scene.isTargetable(coord))
				{
					setTarget(coord);
				}
			}
		}

		return true;
	}

	/**
	 * Rotate boat.
	 */
	@Override
	public boolean onDoubleTap(MotionEvent event)
	{
		// Prevent rotate while moving a ship.
		if (boatToMove != null)
		{
			return false;
		}

		if (isPlacingPhase())
		{
			Coordinate coord = scene.getTouchCoordinate(event);
			if (coord != null)
			{
				Boat boat = scene.getBoatAt(coord);
				if (boat != null)
				{
					// Rotate the boat head at the point of touch.
					boat.swapDirection();
					boat.moveHeadTo(coord);
				}
			}
		}

		return true;
	}

	/**
	 * Zoom.
	 */
	@Override
	public boolean onScale(ScaleGestureDetector detector)
	{
		float zoomScale = detector.getScaleFactor();
		scene.zoom(1 / zoomScale);
		return true;
	}

	/**
	 * Drag.
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY)
	{
		// Account for the projection scale to make it more useful/natural.
		float scrollScale = 0.02f * scene.getProjScale();

		float x = distanceX * scrollScale;
		float y = distanceY * scrollScale;
		scene.moveCameraBy(x, y);
		return true;
	}

	@Override
	public boolean onTouch(android.view.View v, MotionEvent event)
	{
		// Ignore touches in the portrait orientation-related extra space.
		float minY = (scene.screenHeight - scene.screenWidth) / 2;
		float maxY = scene.screenHeight - minY;
		if (event.getY() >= minY && event.getY() <= maxY)
		{
			scaleScanner.onTouchEvent(event);
			gestureScanner.onTouchEvent(event);
			return true;
		}
		return false;
	}

}