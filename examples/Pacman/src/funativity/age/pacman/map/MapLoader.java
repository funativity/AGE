package funativity.age.pacman.map;

import funativity.age.opengl.AGEColor;
import funativity.age.pacman.GameScene;

public class MapLoader
{
	// ************************************************
	// Walls
	// ************************************************

	// flags/other
	public static final int B = 1 << 25;
	public static final int E = 0;
	// Vertical
	public static final int VE = 1 | B;
	public static final int VW = 2 | B;
	// Horizontal
	public static final int HN = 3 | B;
	public static final int HS = 4 | B;
	// Inside Corners
	public static final int INE = 5 | B;
	public static final int INW = 6 | B;
	public static final int ISE = 7 | B;
	public static final int ISW = 8 | B;
	// Outside Corners
	public static final int ONE = 9 | B;
	public static final int ONW = 10 | B;
	public static final int OSE = 11 | B;
	public static final int OSW = 12 | B;
	// Border Corners
	public static final int BNE = 13 | B;
	public static final int BNW = 14 | B;
	public static final int BSE = 15 | B;
	public static final int BSW = 16 | B;

	// ************************************************
	// Walkable/Other
	// ************************************************

	public static final int PAC = 1 << 0; // Normal pac
	public static final int SPC = 1 << 1; // Super pac

	public static final int GG = 1 << 2; // Ghost Gate
	public static final int GP = 1 << 3; // Ghost Pen. All tiles in the Ghost
											// Pen need this flag
	public static final int GD = 1 << 4; // Ghost Door. Points to the entrance
											// of the Ghost Pen

	public static final int GSB = 1 << 5; // Ghost Spawn blinky
	public static final int GSP = 1 << 6; // Ghost Spawn pinky
	public static final int GSI = 1 << 7; // Ghost Spawn inky
	public static final int GSC = 1 << 8; // Ghost Spawn clyde

	public static final int PS = 1 << 9; // Player Spawn
	public static final int FS = 1 << 10; // Fruit Spawn
	public static final int T = 1 << 11; // Tunnel

	public static final int RU = 1 << 21; // restrict up
	public static final int RL = 1 << 22; // restrict left
	public static final int RD = 1 << 23; // restrict down
	public static final int RR = 1 << 24; // restrict right

	// ************************************************
	// Combined
	// ************************************************
	public static final int ALL_RESTRICTIONS = RU | RL | RD | RR;
	public static final int ALL_SPAWNS = GSB | GSP | GSI | GSC | PS | FS;
	public static final int ALL_PACS = PAC | SPC;

	// ************************************************
	// Other
	// ************************************************
	//@formatter:off
	private static final AGEColor[] mapColors =
	{ 
		new AGEColor(0, 0, 1)
	};
	private static final AGEColor[] mapAltColors =
	{ 
		new AGEColor(0, 1, 0.871f)
	};
	private static final AGEColor[] pacColors =
	{ 
		new AGEColor(1, 1, 0)
	};
	//@formatter:on

	// ************************************************
	// Class implementation
	// ************************************************

	private MapLoader()
	{

	}

	public static Map getMap(GameScene scene, int index, int levelNumber)
	{
		Map rtn = null;

		switch (index)
		{
			case 0:
			default:
				rtn = getFirstMap(scene, levelNumber);
				break;
		}

		return rtn;
	}

	public static AGEColor getMapColor(int levelNumber)
	{
		return mapColors[levelNumber % mapColors.length];
	}

	public static AGEColor getAltMapColor(int levelNumber)
	{
		return mapAltColors[levelNumber % mapColors.length];
	}

	public static AGEColor getPacColor(int levelNumber)
	{
		return pacColors[levelNumber % mapColors.length];
	}

	private static Map getFirstMap(GameScene scene, int levelNumber)
	{
		final int width = 28;
		final int height = 31;

		//@formatter:off
		final int[] tiles = 
		{			
			BNW, HN, HN, HN, HN, HN, HN, HN, HN, HN, HN, HN, HN, ONE, ONW, HN, HN, HN, HN, HN, HN, HN, HN, HN, HN, HN, HN, BNE,
			VW, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, VE, VW, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, VE,
			VW, PAC, ISE, HS, HS, ISW, PAC, ISE, HS, HS, HS, ISW, PAC, VE, VW, PAC, ISE, HS, HS, HS, ISW, PAC, ISE, HS, HS, ISW, PAC, VE,
			VW, SPC, VE, B, B, VW, PAC, VE, B, B, B, VW, PAC, VE, VW, PAC, VE, B, B, B, VW, PAC, VE, B, B, VW, SPC, VE,
			VW, PAC, INE, HN, HN, INW, PAC, INE, HN, HN, HN, INW, PAC, INE, INW, PAC, INE, HN, HN, HN, INW, PAC, INE, HN, HN, INW, PAC, VE,
			VW, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, VE,
			VW, PAC, ISE, HS, HS, ISW, PAC, ISE, ISW, PAC, ISE, HS, HS, HS, HS, HS, HS, ISW, PAC, ISE, ISW, PAC, ISE, HS, HS, ISW, PAC, VE,
			VW, PAC, INE, HN, HN, INW, PAC, VE, VW, PAC, INE, HN, HN, ONE, ONW, HN, HN, INW, PAC, VE, VW, PAC, INE, HN, HN, INW, PAC, VE,
			VW, PAC, PAC, PAC, PAC, PAC, PAC, VE, VW, PAC, PAC, PAC, PAC, VE, VW, PAC, PAC, PAC, PAC, VE, VW, PAC, PAC, PAC, PAC, PAC, PAC, VE,
			BSW, HS, HS, HS, HS, ISW, PAC, VE, OSW, HS, HS, ISW, E, VE, VW, E, ISE, HS, HS, OSE, VW, PAC, ISE, HS, HS, HS, HS, BSE,
			E, E, E, E, E, VW, PAC, VE, ONW, HN, HN, INW, E, INE, INW, E, INE, HN, HN, ONE, VW, PAC, VE, E, E, E, E, E,
			E, E, E, E, E, VW, PAC, VE, VW, E, E, E, RU, GSB | GD | RR, GD | RR, RU, E, E, E, VE, VW, PAC, VE, E, E, E, E, E,
			E, E, E, E, E, VW, PAC, VE, VW, E, ISE, HS, HS, GG, GG, HS, HS, ISW, E, VE, VW, PAC, VE, E, E, E, E, E,
			HN, HN, HN, HN, HN, INW, PAC, INE, INW, E, VE, GP, GP, GP, GP, GP, GP, VW, E, INE, INW, PAC, INE, HN, HN, HN, HN, HN,
			T, T, T, T, T, T, PAC, E, E, E, VE, GP | GSI, GP, GP | GSP, GP, GP | GSC, GP, VW, E, E, E, PAC, T, T, T, T, T, T,
			HS, HS, HS, HS, HS, ISW, PAC, ISE, ISW, E, VE, GP, GP, GP, GP, GP, GP, VW, E, ISE, ISW, PAC, ISE, HS, HS, HS, HS, HS,
			E, E, E, E, E, VW, PAC, VE, VW, E, INE, HN, HN, HN, HN, HN, HN, INW, E, VE, VW, PAC, VE, E, E, E, E, E,
			E, E, E, E, E, VW, PAC, VE, VW, E, E, E, E, FS, E, E, E, E, E, VE, VW, PAC, VE, E, E, E, E, E,
			E, E, E, E, E, VW, PAC, VE, VW, E, ISE, HS, HS, HS, HS, HS, HS, ISW, E, VE, VW, PAC, VE, E, E, E, E, E,
			BNW, HN, HN, HN, HN, INW, PAC, INE, INW, E, INE, HN, HN, ONE, ONW, HN, HN, INW, E, INE, INW, PAC, INE, HN, HN, HN, HN, BNE,
			VW, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, VE, VW, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, VE,
			VW, PAC, ISE, HS, HS, ISW, PAC, ISE, HS, HS, HS, ISW, PAC, VE, VW, PAC, ISE, HS, HS, HS, ISW, PAC, ISE, HS, HS, ISW, PAC, VE,
			VW, PAC, INE, HN, ONE, VW, PAC, INE, HN, HN, HN, INW, PAC, INE, INW, PAC, INE, HN, HN, HN, INW, PAC, VE, ONW, HN, INW, PAC, VE,
			VW, SPC, PAC, PAC, VE, VW, PAC, PAC, PAC, PAC, PAC, PAC, PAC | RU, PS, E, PAC | RU, PAC, PAC, PAC, PAC, PAC, PAC, VE, VW, PAC, PAC, SPC, VE,
			OSW, HS, ISW, PAC, VE, VW, PAC, ISE, ISW, PAC, ISE, HS, HS, HS, HS, HS, HS, ISW, PAC, ISE, ISW, PAC, VE, VW, PAC, ISE, HS, OSE,
			ONW, HN, INW, PAC, INE, INW, PAC, VE, VW, PAC, INE, HN, HN, ONE, ONW, HN, HN, INW, PAC, VE, VW, PAC, INE, INW, PAC, INE, HN, ONE,
			VW, PAC, PAC, PAC, PAC, PAC, PAC, VE, VW, PAC, PAC, PAC, PAC, VE, VW, PAC, PAC, PAC, PAC, VE, VW, PAC, PAC, PAC, PAC, PAC, PAC, VE,
			VW, PAC, ISE, HS, HS, HS, HS, OSE, OSW, HS, HS, ISW, PAC, VE, VW, PAC, ISE, HS, HS, OSE, OSW, HS, HS, HS, HS, ISW, PAC, VE,
			VW, PAC, INE, HN, HN, HN, HN, HN, HN, HN, HN, INW, PAC, INE, INW, PAC, INE, HN, HN, HN, HN, HN, HN, HN, HN, INW, PAC, VE,
			VW, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, PAC, VE,
			BSW, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, HS, BSE,
		};		
		//@formatter:on

		final int[][] convertedTiles = new int[width][height];

		// this map was made before double arrays were used. Convert the old
		// format to the new one.
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				// inverted y
				int iy = height - 1 - y;

				convertedTiles[x][y] = tiles[iy * width + x];
			}
		}

		Map m = new Map(scene, convertedTiles, levelNumber);

		// set the scatter locations (these can be off of the map, so they cant
		// be stored normally in the array
		m.setScatterBlinky(width - 3, height + 2);
		m.setScatterPinky(2, height + 2);
		m.setScatterInky(width - 1, -1);
		m.setScatterClyde(0, -1);

		return m;
	}

}
