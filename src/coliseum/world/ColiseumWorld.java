package coliseum.world;

import coliseum.world.terrain.*;
import flounder.entities.*;
import flounder.framework.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.textures.*;
import flounder.visual.*;

public class ColiseumWorld extends IModule {
	private static final ColiseumWorld INSTANCE = new ColiseumWorld();
	public static final String PROFILE_TAB_NAME = "Coliseum World";

	private Fog fog;

	private Colour skyColourDay;
	private Colour skyColourNight;
	private Colour skyColour;
	private SinWaveDriver skyDriver;

	public ColiseumWorld() {
		super(ModuleUpdate.UPDATE_POST, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class, FlounderEntities.class);

		this.fog = new Fog(new Colour(), 0.003f, 2.0f, 0.0f, 50.0f);

		this.skyColourDay = new Colour(0.0f, 0.498f, 1.0f);
		this.skyColourNight = new Colour(0.01f, 0.01f, 0.01f);
		this.skyColour = new Colour(skyColourDay);
		this.skyDriver = new SinWaveDriver(0.0f, 1.0f, 60.0f);
	}

	@Override
	public void init() {
		for (int i = 0; i < 2; i++) {
			int shapesOnEdge = i;
			float r = 0;
			float g = -i;
			float b = i;
			generateChunk(Chunk.calculateXY(new Vector3f(r, g, b), Tile.SIDE_LENGTH * Chunk.CHUNK_RADIUS, null));

			for (int j = 0; j < Tile.SIDE_COUNT; j++) {
				if (j == Tile.SIDE_COUNT - 1) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + Chunk.GENERATE_DELTAS[j][0];
					g = g + Chunk.GENERATE_DELTAS[j][1];
					b = b + Chunk.GENERATE_DELTAS[j][2];
					generateChunk(Chunk.calculateXY(new Vector3f(r, g, b), Tile.SIDE_LENGTH * Chunk.CHUNK_RADIUS, null));
				}
			}
		}

		/*generateChunk(new Vector2f(0.0f, 0.0f));
		generateChunk(new Vector2f(10.392304f, 18.0f));
		generateChunk(new Vector2f(20.784609f, 0.0f));
		generateChunk(new Vector2f(10.392304f, -18.0f));
		generateChunk(new Vector2f(-10.392304f, -18.0f));
		generateChunk(new Vector2f(-20.784609f, 0.0f));
		generateChunk(new Vector2f(-10.392304f, 18.0f));*/
	}

	private void generateChunk(Vector2f position) {
		Chunk chunk = new Chunk(position);

		for (Tile tile : chunk.getTiles()) {
			float height = tile.equals(chunk.getTiles().get(0)) ? 2.0f : 0.0f; // (int) Maths.logRandom(1.0, 3.0); // tile.equals(chunk.getTiles().get(0))

			if (height >= 1.0f) {
				for (int h = 0; h < height; h++) {
					new TerrainStone(FlounderEntities.getEntities(), new Vector3f(tile.getPosition().x, (float) (2.0 * Math.sqrt(2.0)) * (h + 1), tile.getPosition().y), new Vector3f());
				}
			} else {
				new TerrainGrass(FlounderEntities.getEntities(), new Vector3f(tile.getPosition().x, 0.0f, tile.getPosition().y), new Vector3f());
			}
		}
	}

	@Override
	public void update() {
		Colour.interpolate(skyColourDay, skyColourNight, skyDriver.update(FlounderFramework.getDelta()), skyColour);
		fog.setFogColour(skyColour);
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

	@Override
	public IModule getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {

	}
}
