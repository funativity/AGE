package age.asteroids.scene;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import age.asteroids.Entity.Asteroid;
import age.asteroids.Entity.Bullet;
import age.asteroids.Entity.Player;
import age.asteroids.Entity.WorldBox;
import age.asteroids.enums.Audio;
import age.asteroids.state.GamePlayState;
import age.asteroids.state.MainMenuState;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import funativity.age.error.OversizedMeshException;
import funativity.age.error.TextureTooLargeException;
import funativity.age.opengl.AGEColor;
import funativity.age.opengl.MM;
import funativity.age.opengl.Mesh;
import funativity.age.opengl.meshloader.MeshLoader;
import funativity.age.opengl.shaders.SimpleLightingTechnique;
import funativity.age.state.Scene;
import funativity.age.textures.Texture;
import funativity.age.textures.TextureLoader;
import funativity.age.util.Geometry3f;

/**
 * The Level contains all logic regarding the gameplay.
 * 
 */
public class Level extends Scene
{
	public Player player;
	private int score;

	public int currentLevelNumber;

	public float width;
	public float height;
	public float depth;

	private static final float PRELEVEL_DELAY = 3;
	private static final float POSTLEVEL_DELAY = 3;
	private float preLevelDelay;
	private float postLevelDelay;
	public boolean inGame;

	public final GamePlayState state;

	public Level(Context context)
	{
		super(context);
		if (context instanceof GamePlayState)
		{
			state = (GamePlayState) context;
		}
		else
		{
			state = null;
		}
	}

	@Override
	public void init()
	{
		Texture asteroidTex = null;
		Texture shipTex = null;
		Texture bulletTex = null;

		try
		{
			asteroidTex = TextureLoader.getTexture("model/asteroid_color.jpg",
					state.getAssets());
			shipTex = TextureLoader.getTexture("model/ship_color.jpg",
					state.getAssets());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TextureTooLargeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AGEColor white = new AGEColor(1f, 1f, 1f);

		Bullet.mesh = initMesh("model/bullet.obj", new AGEColor(0, 1, 0),
				bulletTex);

		Player.mesh = initMesh("model/ship.obj", white, shipTex);
		player = new Player();

		Asteroid.mesh_size1 = initMesh("model/asteroid_size1.obj", white,
				asteroidTex);

		Asteroid.mesh_size2 = initMesh("model/asteroid_size2.obj", white,
				asteroidTex);

		Asteroid.mesh_size3 = initMesh("model/asteroid_size3.obj", white,
				asteroidTex);

		Asteroid.mesh_size4 = initMesh("model/asteroid_size4.obj", white,
				asteroidTex);

		Asteroid.mesh_size5 = initMesh("model/asteroid_size5.obj", white,
				asteroidTex);

		Asteroid.mesh_size6 = initMesh("model/asteroid_size6.obj", white,
				asteroidTex);

		// setup depth test
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		// setup culling
		GLES20.glFrontFace(GLES20.GL_CCW);
		GLES20.glCullFace(GLES20.GL_BACK);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
	}

	/**
	 * Set the game to specified level number. Clears all entities off of the
	 * scene. Then adds the player to the scene as well as some number of
	 * asteroids. Calling this method will initialize the level.
	 * 
	 * @param levelNumber
	 */
	public void setupLevel(int levelNumber)
	{
		state.setStatusLabel("Level: " + levelNumber);

		removeAllEntities();
		getCollisionManager().removeAllChildren(0);

		currentLevelNumber = levelNumber;
		preLevelDelay = PRELEVEL_DELAY;

		setLevelSize(currentLevelNumber);

		ArrayList<Integer> asteroidInfo = getLevelAsteroidSizes(currentLevelNumber);
		Asteroid.count = asteroidInfo.size();
		for (int i = 0; i < Asteroid.count; i++)
		{
			Asteroid a = new Asteroid(asteroidInfo.get(i));
			a.setRandomLocation(width, height, depth, 10);
			a.setRandomVelocity(1f, 5f);
			a.addToLevel(this);
		}

		// move player back to center
		player.reset();
		player.addToLevel(this);

		try
		{
			WorldBox wb = new WorldBox(width, height, depth);
			wb.addToLevel(this);
			((Mesh) wb.getDrawable()).setTexture(TextureLoader.getTexture(
					"images/blueBox.png", getAssets()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void update(float delta)
	{
		if (currentLevelNumber <= 0)
		{
			setupLevel(1);
			state.play(Audio.MUSIC, false);
		}
		if ((Asteroid.count <= 0 || !player.isAlive()) && postLevelDelay <= 0)
		{
			postLevelDelay = POSTLEVEL_DELAY;
			state.setStatusLabel(player.isAlive() ? "Level Complete!"
					: "Game Over!");
			if (!player.isAlive())
			{
				state.stop(Audio.MUSIC);
				state.play(Audio.DIE, false);
			}
		}
		if (preLevelDelay > 0)
		{
			preLevelDelay -= delta;
			delta = 0;

			if (preLevelDelay <= 0)
				state.setStatusLabel("");
		}
		else if (postLevelDelay > 0)
		{
			postLevelDelay -= delta;
			if (postLevelDelay <= 0)
			{
				if (player.isAlive())
				{
					setupLevel(currentLevelNumber + 1);
				}
				else
				{
					Intent intent = new Intent(state, MainMenuState.class);
					state.startActivity(intent);
				}
			}
			delta = 0;
		}

		inGame = delta > 0;

		// only do normal update if there is not a delay timer
		super.update(delta);

	}

	private float[] geomToArray(Geometry3f geom)
	{
		return new float[] { geom.getX(), geom.getY(), geom.getZ() };
	}

	@Override
	public void render()
	{
		Geometry3f direction = new Geometry3f(-3, -3, 0);
		AGEColor color = new AGEColor(1f, 1f, 1f);
		SimpleLightingTechnique.getTechnique().getShaderProgram().useProgram();
		SimpleLightingTechnique.getTechnique().setDirectionalLight(color, 0.3f,
				1.8f, direction);

		// move the camera to the location of the player
		float[] eye = geomToArray(player.getCameraPos());
		float[] at = geomToArray(Geometry3f.add(player.getPosition(),
				player.getFacingDirection()));
		float[] up = geomToArray(player.getVertical());
		MM.lookAt(eye, up, at);

		// normal rendering
		super.render();
	}

	@Override
	public void loadResources()
	{
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		super.onSurfaceChanged(gl, width, height);
		MM.perspective(60, (float) width / height, 0.1f, 500);
	}

	/**
	 * If the player is allowed, this method will make the player shoot directly
	 * in front of them
	 */
	public void shoot()
	{
		if (player != null && inGame)
		{
			player.shoot();
			state.play(Audio.ATTACK, true);
		}
	}

	public void addToScore(int addScore)
	{
		if ((score += addScore) < 0)
		{
			score = 0;
		}
		if (state != null)
		{
			state.setScoreLabel(score);
		}
	}

	private static ArrayList<Integer> getLevelAsteroidSizes(int level)
	{
		final int squared = level * level;
		final int cubed = squared * level;
		final int quad = squared * squared;

		ArrayList<Integer> sizes = new ArrayList<Integer>();
		for (int i = quad + 2; i >= 0; i--)
		{
			while (i >= 70)
			{
				for (int q = i / 70; q > 0; q--)
				{
					sizes.add(5);
				}
				i -= (cubed + 1);
			}
			while (i >= 30)
			{
				sizes.add(4);
				i -= cubed;
			}
			while (i >= 14)
			{
				sizes.add(3);
				i -= squared;
			}
			while (i >= 4)
			{
				sizes.add(2);
				i -= level;
			}

			sizes.add(1);

		}
		return sizes;
	}

	private void setLevelSize(int level)
	{
		// could do more here, but just make the size of the level 25x25x25
		width = height = depth = 25;
	}

	private Mesh initMesh(String fileName, AGEColor color, Texture tex)
	{
		try
		{
			InputStream is = MeshLoader.openAsset(fileName, getAssets());
			Mesh mesh = MeshLoader.loadMesh(MeshLoader.FileType.OBJ_FILE, is);

			if (tex != null)
			{
				mesh.setTexture(tex);
			}
			else
			{
				mesh.setColor(color);
			}

			mesh.setTechnique(SimpleLightingTechnique.getTechnique());
			return mesh;
		}
		catch (OversizedMeshException e1)
		{
		}

		return null;
	}
}
