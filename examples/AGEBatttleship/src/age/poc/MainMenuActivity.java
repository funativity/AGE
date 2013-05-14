package age.poc;

import java.util.List;
import age.dataModels.GameOverview;
import age.dataModels.User;
import age.database.BattleShipDB;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import funativity.age.state.GameState;
import funativity.age.state.layout.AGELinearLayout;

/**
 * This class is for the main start up activity that allows the user to choose what game to play
 * @author vancer
 *
 */
public class MainMenuActivity extends GameState
{
	private BattleShipDB dataBase;

	private List<User> allUsers;
	private List<GameOverview> allGames;
	private String userID = null;
	private String gameID = null;

	private Spinner listView;

	@Override
	public void init()
	{
		dataBase = new BattleShipDB();

		AGELinearLayout ageLL = new AGELinearLayout(this);
		setLayout(ageLL);
		((LinearLayout) this.getViewGroup()).setGravity(Gravity.CENTER);

		addUserSelectSpinner();
		addNewGameButton();
		addGameSelectSpinner();
		addGameStartButton();
	}

	/**
	 * Add a spinner that allows you to choose who you play as
	 */
	private void addUserSelectSpinner()
	{
		// Add TextView
		TextView label = new TextView(this);
		label.setText("Select a user to impersonate!");
		label.setTextSize(20);
		label.setPadding(0, 20, 0, 0);
		label.setBackgroundColor(Color.CYAN);
		this.getViewGroup().addView(label);

		// Add spinner
		Spinner spinner = new Spinner(this);
		allUsers = dataBase.getAllUsers();

		String[] users = new String[allUsers.size()];
		for (int i = 0; i < allUsers.size(); i++)
		{
			users[i] = allUsers.get(i).name;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, users);
		spinner.setAdapter(adapter);

		spinner.setPrompt("Choose a User!");

		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id)
			{
				// position of selected = position in array
				userID = allUsers.get(position).name;
				updateGameList();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView)
			{
				userID = null;
			}
		});

		this.getViewGroup().addView(spinner);

	}

	/**
	 * Add the new game button
	 */
	private void addNewGameButton()
	{
		final Button button = new Button(this);
		button.setText("New Game");
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(button
						.getContext());
				builder.setTitle("Select challenger");

				final String[] users = new String[allUsers.size()];
				for (int i = 0; i < allUsers.size(); i++)
				{
					users[i] = allUsers.get(i).name;
				}

				builder.setItems(users, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int item)
					{
						dataBase.createGame(userID, users[item]);
						updateGameList();
					}
				}).show();
			}
		});
		this.getViewGroup().addView(button);
	}

	/**
	 * Add the spinner that shows all available games with other users to the screen
	 */
	private void addGameSelectSpinner()
	{
		// Add TextView
		TextView label = new TextView(this);
		label.setText("Select a game to win!");
		label.setTextSize(20);
		label.setPadding(0, 20, 0, 0);
		label.setBackgroundColor(Color.CYAN);
		this.getViewGroup().addView(label);

		// Add Spinner
		Spinner spinner = new Spinner(this);

		listView = spinner;

		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id)
			{
				
				// position of selected = position in array
				gameID = allGames.get(position).gameID;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView)
			{
				gameID = null;
			}
		});

		this.getViewGroup().addView(spinner);
	}

	/**
	 * This will update the list of games that are available
	 */
	private void updateGameList()
	{
		if (userID != null)
		{
			allGames = dataBase.getActiveGames(userID);
		}

		if (allGames != null)
		{
			String[] games = new String[allGames.size()];
			for (int i = 0; i < allGames.size(); i++)
			{
				GameOverview game = allGames.get(i);
				games[i] = "Game " + (i + 1) + " against " + game.enemyName;
			}
			

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.select_dialog_item, games);
			listView.setAdapter(adapter);
		}
	}

	/**
	 * Add a start game button after a game has been selected
	 */
	private void addGameStartButton()
	{
		final Button button = new Button(this);
		button.setText("Destroy those toy boats!!!");
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (gameID != null)
				{
					Intent intent = new Intent(button.getContext(),
							MainActivity.class);
					intent.putExtra("USER_ID", userID);
					intent.putExtra("GAME_ID", gameID);

					startActivity(intent);
				}
			}
		});

		this.getViewGroup().addView(button);
	}
}
