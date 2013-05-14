package age.dataModels;

import java.util.ArrayList;
import java.util.List;

/**
 * The class that holds the list of boats and shots for both players
 * @author binisha
 *
 */
public class GameData
{

	public boolean isCurrentUsersTurn;
	
	/**
	 * The list of the user's boats
	 */
	public List<Boat> userBoats;
	
	/**
	 * The list of the user's previous shots
	 */
	public List<Coordinate> userShots;
	
	/**
	 * The list of the enemies boats
	 */
	public List<Boat> enemyBoats;
	
	/**
	 * The list of the enemies previous shots
	 */
	public List<Coordinate> enemyShots;
		
	/**
	 * A base constructor that is NOT recommended for use
	 */
	public GameData(){
		userBoats = new ArrayList<Boat>();
		userShots = new ArrayList<Coordinate>();
		enemyBoats = new ArrayList<Boat>();
		enemyShots = new ArrayList<Coordinate>();
	}
	
	/**
	 * The recommended constructor for the GameData object
	 * @param userBoats The list of the user's boats
	 * @param userShots The list of the user's previous shots
	 * @param enemyBoats The list of the enemies boats
	 * @param enemyShots The list of the enemies previous shots
	 */
	public GameData(List<Boat> userBoats, List<Coordinate> userShots, List<Boat> enemyBoats, List<Coordinate> enemyShots, boolean isCurrentUsersTurn){
		this.userBoats = userBoats;
		this.userShots = userShots;
		this.enemyBoats = enemyBoats;
		this.enemyShots = enemyShots;
		this.isCurrentUsersTurn = isCurrentUsersTurn;
	}
}
