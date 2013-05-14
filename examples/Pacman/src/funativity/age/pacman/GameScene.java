package funativity.age.pacman;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.MM;
import funativity.age.pacman.entities.Collidable;
import funativity.age.pacman.entities.Fruit;
import funativity.age.pacman.entities.Ghost;
import funativity.age.pacman.entities.Joystick;
import funativity.age.pacman.entities.Pacman;
import funativity.age.pacman.entities.Score;
import funativity.age.pacman.map.Map;
import funativity.age.pacman.map.MapLoader;
import funativity.age.state.Scene;
import funativity.age.util.Geometry3f;

public class GameScene extends Scene
{
	private static final float CAMERA_DISTNANCE = 35;
	private static final float BORDERX = 10f;
	private static final float BORDERY = 14;

	// entities
	private static Pacman pacman;
	private static Ghost blinky;
	private static Ghost pinky;
	private static Ghost inky;
	private static Ghost clyde;

	private static Joystick joystick;
	private static Pacman life;
	private static Fruit fruit;

	private final InGameActivity activity;

	private static Map map;
	private int mapNumber = 0;

	private int score = 0;
	private int lives = 3;

	private int screenWidth;
	private int screenHeight;

	private float flickerTime = 0;

	public GameScene(InGameActivity activity)
	{
		super(activity);

		this.activity = activity;
	}

	public void loadNextMap()
	{
		map = MapLoader.getMap(this, 0, mapNumber++);
	}

	@Override
	public void init()
	{
		// setup alpha blending
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BLEND);

		// init textures
		Ghost.setup(getAssets());
		Pacman.setup(getAssets());
		Joystick.setup(getAssets());
		Fruit.setup(getAssets());
		Score.setup(getAssets());

		blinky = new Ghost(Ghost.Ghosts.BLINKY);
		getCollisionManager().addChild(new Collidable(blinky), 0);
		addEntity(blinky);

		pinky = new Ghost(Ghost.Ghosts.PINKY);
		getCollisionManager().addChild(new Collidable(pinky), 0);
		addEntity(pinky);

		inky = new Ghost(Ghost.Ghosts.INKY);
		getCollisionManager().addChild(new Collidable(inky), 0);
		addEntity(inky);

		clyde = new Ghost(Ghost.Ghosts.CLYDE);
		getCollisionManager().addChild(new Collidable(clyde), 0);
		addEntity(clyde);

		final AGEColor yellow = new AGEColor(1, 1, 0);
		pacman = new Pacman(yellow);
		getCollisionManager().addChild(new Collidable(pacman), 0);
		addEntity(pacman);

		fruit = new Fruit();
		getCollisionManager().addChild(new Collidable(fruit), 0);

		life = new Pacman(yellow);

		final AGEColor lightBlue = new AGEColor(0, 0.58f, 1);
		joystick = new Joystick(lightBlue);

		loadNextMap();
	}

	public int addToScore(int score)
	{
		// check if player gets a free life
		if (this.score < 10000 && this.score + score >= 10000)
			addToLives(1);

		// add score
		this.score += score;

		// update GUI
		activity.updateScore(this.score);
		return this.score;
	}

	public int addToLives(int lives)
	{
		this.lives += lives;
		activity.updateLives(this.lives);
		return this.lives;
	}

	@Override
	public void render()
	{
		final float width = screenWidth;
		final float height = screenHeight * 0.8f;
		MM.perspective(45f, width / height, 0.05f, 50);

		final int iHeight = (int) height;
		final int iWidth = (int) width;
		final int heightOffset = screenHeight - iHeight;
		GLES20.glViewport(0, heightOffset, iWidth, iHeight);

		float x = pacman.getX();
		float y = pacman.getY();

		if (y < BORDERY)
			y = BORDERY;
		else if (y > map.getHeight() - BORDERY - 1)
			y = map.getHeight() - BORDERY - 1;

		if (x < BORDERX)
			x = BORDERX;
		else if (x > map.getWidth() - BORDERX - 1)
			x = map.getWidth() - BORDERX - 1;

		MM.lookAt(x, y, CAMERA_DISTNANCE, x, y, 0, 0, 1, 0);

		if (map.render())
			super.render();

		GLES20.glViewport(iWidth / 3, 0, iWidth, iHeight);

		x -= 8;
		y -= 10.5f;

		joystick.setPosition(new Geometry3f(x, y));
		joystick.update(null, 0);
		joystick.render();

		x += 4.5;
		life.update((Scene) null, 0);
		for (int i = 0; flickerTime < 0.5f && i < lives || i < lives - 1; i++)
		{
			life.setPosition(new Geometry3f(x + 2 * i, y));
			life.render();
		}
	}

	@Override
	public void update(float delta)
	{
		if (lives <= 0 && map.getPauseTime() <= 0)
		{
			activity.finish();
		}

		if (map.getPauseTime() > 0)
			flickerTime = 0;
		else
		{
			flickerTime -= delta;
			if (flickerTime < 0)
				flickerTime = 1;
		}

		if (!map.update(delta))
			delta = 0;

		super.update(delta);
	}

	public Map getMap()
	{
		return map;
	}

	public Pacman getPacman()
	{
		return pacman;
	}

	public Ghost getBlinky()
	{
		return blinky;
	}

	public Ghost getPinky()
	{
		return pinky;
	}

	public Ghost getInky()
	{
		return inky;
	}

	public Ghost getClyde()
	{
		return clyde;
	}

	public Joystick getJoystick()
	{
		return joystick;
	}

	public void removeFruit()
	{
		removeEntity(fruit);
		fruit.clearDuration();
	}

	public void addFruit(int x, int y, float duration, Fruit.Fruits fruitType)
	{
		fruit.setDuration(duration);
		fruit.setFruit(fruitType);
		fruit.setPosition(new Geometry3f(x + 0.5f, y));

		addEntity(fruit);
	}

	public void showScore(float x, float y, int scoreIndex)
	{
		Score score = new Score(x, y, scoreIndex);
		addEntity(score);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		super.onSurfaceChanged(gl, width, height);
		screenWidth = width;
		screenHeight = height;
	}

	@Override
	public void loadResources()
	{

	}

	public static Map getCurrentMap()
	{
		return map;
	}
}