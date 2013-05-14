package age.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import age.dataModels.Boat;
import age.dataModels.Coordinate;
import age.dataModels.GameData;
import age.dataModels.GameOverview;
import age.dataModels.User;
import android.os.AsyncTask;

import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.ComparisonOperator;
import com.amazonaws.services.dynamodb.model.Condition;
import com.amazonaws.services.dynamodb.model.ScanRequest;
import com.amazonaws.services.dynamodb.model.ScanResult;

import funativity.age.databases.DynamoDBManager;

/**
 * The data access class for the battleship game.
 * 
 * @author binisha
 * 
 */
public class BattleShipDB
{
	private DynamoDBManager db;

	public BattleShipDB()
	{
		try
		{
			db = new DynamoDBManager("/AwsCredentials.properties");
		}
		catch (IOException e)
		{
			// Uh oh
		}
	}

	/**
	 * Create a new user based on the username
	 * 
	 * @param username
	 *            The username the user registered with
	 * @return True if it was added successfully
	 */
	public boolean createUser(String username)
	{
		if (db.getUserAsMap(username).size() == 0)
		{
			Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("primaryKey", new AttributeValue().withS(username));
			db.overwriteUser(key);
			return true;
		}
		return false;
	}

	public List<User> getAllUsers()
	{

		ScanRequest scan = new ScanRequest().withTableName("User");

		// List<String> attributesToGet = new ArrayList<String>();
		// attributesToGet.add("primaryKey");
		// scan.setAttributesToGet(attributesToGet);
		//
		// Condition condition = new Condition();
		// condition.setComparisonOperator(ComparisonOperator.NOT_NULL);
		// List<AttributeValue> valuesToFind = new ArrayList<AttributeValue>();
		// valuesToFind.add(new AttributeValue().withS("gibberish"));
		// condition.setAttributeValueList(valuesToFind);
		//
		// Map<String, Condition> filter = new HashMap<String, Condition>();
		// filter.put("primaryKey", condition);
		// scan.setScanFilter(filter);

		ScanResult result = null;
		try
		{
			result = new ScanTableAsync().execute(scan).get();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}

		List<User> users = new ArrayList<User>();
		for (Map<String, AttributeValue> entry : result.getItems())
		{
			User newUser = new User(entry.get("primaryKey").getS());
			users.add(newUser);
		}

		return users;
	}

	/**
	 * This method will get all the other users on the game
	 * 
	 * @param currentUserID
	 *            The userID of the player currently in the app
	 * @return A list of all users that are available to play
	 */
	public List<User> getOtherUsers(String currentUserID)
	{
		ScanRequest scan = new ScanRequest().withTableName("User");
		List<String> attributesToGet = new ArrayList<String>();
		attributesToGet.add("primaryKey");
		scan.setAttributesToGet(attributesToGet);

		Condition condition = new Condition();
		condition.setComparisonOperator(ComparisonOperator.NOT_CONTAINS);
		List<AttributeValue> valuesToFind = new ArrayList<AttributeValue>();
		valuesToFind.add(new AttributeValue().withS(currentUserID));
		condition.setAttributeValueList(valuesToFind);

		Map<String, Condition> filter = new HashMap<String, Condition>();
		filter.put("primaryKey", condition);
		scan.setScanFilter(filter);

		ScanResult result = null;
		try
		{
			result = new ScanTableAsync().execute(scan).get();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}

		List<User> users = new ArrayList<User>();
		for (Map<String, AttributeValue> entry : result.getItems())
		{
			User newUser = new User(entry.get("primaryKey").getS());
			users.add(newUser);
		}

		return users;
	}

	/**
	 * This will get a list of all games where it is the current user's turn
	 * 
	 * @param currentUserID
	 *            The userID of the current user
	 * @return A list of all games where it is the user's turn
	 */
	public List<GameOverview> getActiveGames(String currentUserID)
	{
		ScanRequest scan = new ScanRequest().withTableName("Data");
		List<String> attributesToGet = new ArrayList<String>();
		attributesToGet.add("turn");
		attributesToGet.add("primaryKey");

		scan.setAttributesToGet(attributesToGet);
		Condition condition = new Condition();

		List<AttributeValue> valuesToFind = new ArrayList<AttributeValue>();
		valuesToFind.add(new AttributeValue().withS("" + currentUserID));
		condition.setAttributeValueList(valuesToFind);
		condition.setComparisonOperator(ComparisonOperator.CONTAINS);

		Map<String, Condition> filter = new HashMap<String, Condition>();
		filter.put("players", condition);
		scan.setScanFilter(filter);

		ScanResult result = null;
		try
		{
			result = new ScanTableAsync().execute(scan).get();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}

		List<GameOverview> games = new ArrayList<GameOverview>();

		for (Map<String, AttributeValue> entry : result.getItems())
		{
			if (entry.get("turn").getS().equals(currentUserID))
			{
				if (entry.get("primaryKey") != null)
				{
					String gameID = entry.get("primaryKey").getS();
					games.add(new GameOverview(gameID, getEnemyUserID(gameID,
							currentUserID)));
				}
			}
		}

		return games;
	}

	/**
	 * Creates a new game and returns the gameID of the created game
	 * 
	 * @param currentUserID
	 *            The currentUserID
	 * @param enemyUserID
	 *            The userID of the enemy
	 * @return The new gameID
	 */
	public String createGame(String currentUserID, String enemyUserID)
	{
		String gameID = currentUserID + "&" + enemyUserID;
		Map<String, AttributeValue> dataMap = new HashMap<String, AttributeValue>();
		dataMap.put("primaryKey", new AttributeValue().withS(gameID));
		dataMap.put("DataID", new AttributeValue().withS(gameID));

		List<String> players = new ArrayList<String>();
		players.add("" + currentUserID);
		players.add("" + enemyUserID);
		dataMap.put("turn", new AttributeValue().withS("" + currentUserID));
		dataMap.put("players", new AttributeValue().withSS(players));
		db.overwriteData(dataMap);
		return gameID;
	}

	/**
	 * This will retrieve the GameData with the lists of shots and boats
	 * 
	 * @param gameID
	 *            The gameID being played
	 * @param currentUserID
	 *            The userID of the current player
	 * @return A GameData object or null if the gameID or userID are invalid
	 */
	@SuppressWarnings("unchecked")
	public GameData getGameData(String gameID, String currentUserID)
	{
		Map<String, AttributeValue> values = db.getDataAsMap(gameID);
		String enemyUserID = getEnemyUserID(gameID, currentUserID);

		// Initialize GameData object and start turn off to be false
		GameData gameData = new GameData();
		gameData.isCurrentUsersTurn = false;

		AttributeValue enemyBoats = values.get(enemyUserID + "_boats");
		if (enemyBoats != null)
		{
			gameData.enemyBoats = (List<Boat>) deserializeObject(enemyBoats
					.getB());
		}
		else
		{
			gameData.enemyBoats = new ArrayList<Boat>();
		}

		AttributeValue enemyShots = values.get(enemyUserID + "_shots");
		if (enemyShots != null)
		{
			gameData.enemyShots = (List<Coordinate>) deserializeObject(enemyShots
					.getB());
		}
		else
		{
			gameData.enemyShots = new ArrayList<Coordinate>();
		}

		AttributeValue userBoats = values.get(currentUserID + "_boats");
		if (userBoats != null)
		{
			gameData.userBoats = (List<Boat>) deserializeObject(userBoats
					.getB());
		}
		else
		{
			gameData.userBoats = new ArrayList<Boat>();
		}

		AttributeValue userShots = values.get(currentUserID + "_shots");
		if (userShots != null)
		{
			gameData.userShots = (List<Coordinate>) deserializeObject(userShots
					.getB());
		}
		else
		{
			gameData.userShots = new ArrayList<Coordinate>();
		}

		if (values.get("turn").getS().equals(currentUserID))
		{
			gameData.isCurrentUsersTurn = true;
		}

		return gameData;
	}

	/**
	 * This will set the boats for the specified user in the specified game
	 * 
	 * @param boats
	 *            The list of the players boats
	 * @param gameID
	 *            The gameID to save the boats for
	 * @param playerID
	 *            The playerID of the owner of the boats
	 */
	public void setBoats(List<Boat> boats, String gameID, String playerID)
	{
		ByteBuffer bytebuff = serializeObject(boats);
		bytebuff.rewind();

		Map<String, AttributeValue> values = new HashMap<String, AttributeValue>();
		values.put("turn", new AttributeValue().withS(getEnemyUserID(gameID, playerID)));
		values.put(playerID + "_boats", new AttributeValue().withB(bytebuff));

		db.updateData(values, gameID);
	}

	/**
	 * This will add a shot that missed all boats
	 * 
	 * @param shots
	 *            The list of shots by the player
	 * @param gameID
	 *            The gameID for the game being played
	 * @param playerID
	 *            The playerID of the player who shot
	 */
	public void addMissedShot(List<Coordinate> shots, String gameID,
			String playerID)
	{
		String enemiesPlayerID = getEnemyUserID(gameID, playerID);

		ByteBuffer bytebuff = serializeObject(shots);
		bytebuff.rewind();

		Map<String, AttributeValue> values = new HashMap<String, AttributeValue>();
		values.put("turn", new AttributeValue().withS(enemiesPlayerID));
		values.put(playerID + "_shots", new AttributeValue().withB(bytebuff));

		db.updateData(values, gameID);
	}

	/**
	 * This will add a shot that hit an enemy boat
	 * 
	 * @param shots
	 *            The list of shots fired by the player
	 * @param gameID
	 *            The gameID for the game being played
	 * @param playerID
	 *            The playerID of the player who shot
	 * @param enemiesBoats
	 *            The list of the enemies boats
	 */
	public void addHitShot(List<Coordinate> shots, String gameID,
			String playerID, List<Boat> enemiesBoats)
	{
		String enemiesPlayerID = getEnemyUserID(gameID, playerID);

		ByteBuffer enemyBuff = serializeObject(enemiesBoats);
		enemyBuff.rewind();

		ByteBuffer shotbuff = serializeObject(shots);
		shotbuff.rewind();

		Map<String, AttributeValue> values = new HashMap<String, AttributeValue>();
		values.put(enemiesPlayerID + "_boats",
				new AttributeValue().withB(enemyBuff));
		values.put("turn", new AttributeValue().withS(enemiesPlayerID));
		values.put(playerID + "_shots", new AttributeValue().withB(shotbuff));

		db.updateData(values, gameID);
	}

	/**
	 * This will end a game so that it is not returned in the active games list
	 * for the player
	 * 
	 * @param gameID
	 *            The gameID to end
	 */
	public void endGame(String gameID)
	{
		db.deleteData(gameID);
	}

	private String getEnemyUserID(String gameID, String playerID)
	{
		String enemyUserID = "";
		String[] strings = gameID.split("&");
		if (strings[0].equals(playerID))
		{
			enemyUserID = strings[1];
		}
		else
		{
			enemyUserID = strings[0];
		}
		return enemyUserID;
	}

	private ByteBuffer serializeObject(Object obj)
	{
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutputStream oStream = null;
		ByteBuffer buffer = null;
		try
		{
			oStream = new ObjectOutputStream(bStream);
			oStream.writeObject(obj);
			byte[] byteVal = bStream.toByteArray();
			buffer = ByteBuffer.wrap(byteVal);
			buffer.put(byteVal);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return buffer;
	}

	private Object deserializeObject(ByteBuffer buffer)
	{
		InputStream inputStream = new ByteArrayInputStream(buffer.array());
		Object returnObject = null;
		try
		{
			ObjectInputStream stream = new ObjectInputStream(inputStream);
			returnObject = stream.readObject();
		}
		catch (OptionalDataException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return returnObject;
	}

	/**
	 * This class will asynchronously scan the database
	 * 
	 * @author binisha
	 * 
	 */
	private class ScanTableAsync extends
			AsyncTask<ScanRequest, Void, ScanResult>
	{

		@Override
		protected ScanResult doInBackground(ScanRequest... arg0)
		{
			return db.getAmazonDynamoDB().scan(arg0[0]);
		}
	}

}
