package age.dataModels;

/**
 * This class contains the minimum data for a game overview
 * @author binisha
 *
 */
public class GameOverview
{
	/**
	 * The ID of the game
	 */
	public String gameID;
	
	/**
	 * The name of the opponent
	 */
	public String enemyName;
	
	/**
	 * The base constructor for a GameOverview
	 * @param gameID The gameID of the game
	 * @param enemyName The name of the opponent
	 */
	public GameOverview(String gameID, String enemyName){
		this.gameID = gameID;
		this.enemyName = enemyName;
	}
}
