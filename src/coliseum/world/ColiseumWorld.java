package coliseum.world;

import flounder.camera.*;
import flounder.entities.*;
import flounder.framework.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.textures.*;
import flounder.visual.*;

import java.util.*;

public class ColiseumWorld extends IModule {
	public static final float[][] GENERATE_DELTAS = new float[][]{{1.0f, 0.0f, -1.0f}, {0.0f, 1.0f, -1.0f}, {-1.0f, 1.0f, 0.0f}, {-1.0f, 0.0f, 1.0f}, {0.0f, -1.0f, 1.0f}, {1.0f, -1.0f, 0.0f}};

	private static final ColiseumWorld INSTANCE = new ColiseumWorld();
	public static final String PROFILE_TAB_NAME = "Coliseum World";

	private Fog fog;

	private List<Chunk> chunks;

	private float dayFactor;
	private Colour skyColourDay;
	private Colour skyColourNight;
	private Colour skyColour;
	private SinWaveDriver skyDriver;

	public ColiseumWorld() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class, FlounderEntities.class);

		this.fog = new Fog(new Colour(), 0.003f, 2.0f, 0.0f, 50.0f);

		this.chunks = new ArrayList<>();

		this.dayFactor = 0.0f;
		this.skyColourDay = new Colour(0.0f, 0.498f, 1.0f);
		this.skyColourNight = new Colour(0.01f, 0.01f, 0.01f);
		this.skyColour = new Colour(skyColourDay);
		this.skyDriver = new SinWaveDriver(0.0f, 100.0f, 60.0f);
	}

	@Override
	public void init() {
		// Chunk.HEXAGON_SIDE_LENGTH * Chunk.CHUNK_RADIUS
		final float TESTING = 10.0f;

		for (int i = 0; i < 1; i++) {
			int shapesOnEdge = i;
			float r = 0;
			float g = -i;
			float b = i;
			chunks.add(new Chunk(Chunk.calculateXY(new Vector3f(r, g, b), TESTING, null)));

			for (int j = 0; j < Chunk.HEXAGON_SIDE_COUNT; j++) {
				if (j == Chunk.HEXAGON_SIDE_COUNT - 1) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + GENERATE_DELTAS[j][0];
					g = g + GENERATE_DELTAS[j][1];
					b = b + GENERATE_DELTAS[j][2];
					chunks.add(new Chunk(Chunk.calculateXY(new Vector3f(r, g, b), TESTING, null)));
				}
			}
		}

		/*chunks.add(new Chunk(new Vector2f(0.0f, 0.0f)));
		chunks.add(new Chunk(new Vector2f(10.392304f, 18.0f)));
		chunks.add(new Chunk(new Vector2f(20.784609f, 0.0f)));
		chunks.add(new Chunk(new Vector2f(10.392304f, -18.0f)));
		chunks.add(new Chunk(new Vector2f(-10.392304f, -18.0f)));
		chunks.add(new Chunk(new Vector2f(-20.784609f, 0.0f)));
		chunks.add(new Chunk(new Vector2f(-10.392304f, 18.0f)));*/
	}

	@Override
	public void update() {
		dayFactor = (float) (skyDriver.update(FlounderFramework.getDelta()) / 100.0);
		Colour.interpolate(skyColourDay, skyColourNight, dayFactor, skyColour);
		fog.setFogColour(skyColour);

		for (Chunk chunk : chunks) {
			if (FlounderCamera.getPlayer() != null) {
				chunk.update(FlounderCamera.getPlayer().getPosition());
			} else {
				chunk.update(null);
			}
		}
	}

	@Override
	public void profile() {

	}

	public static Fog getFog() {
		return INSTANCE.fog;
	}

	public static void addFog(Fog fog) {
		INSTANCE.fog = fog;
	}

	public static Colour getSkyColour() {
		return INSTANCE.skyColour;
	}

	public static float getDayFactor() {
		return INSTANCE.dayFactor;
	}

	@Override
	public IModule getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {

	}
}
