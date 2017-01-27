package coliseum.world;

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
	private static final ColiseumWorld INSTANCE = new ColiseumWorld();
	public static final String PROFILE_TAB_NAME = "Coliseum World";

	private Fog fog;

	private List<Chunk> chunks;

	private Colour skyColourDay;
	private Colour skyColourNight;
	private Colour skyColour;
	private SinWaveDriver skyDriver;

	public ColiseumWorld() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class, FlounderEntities.class);

		this.fog = new Fog(new Colour(), 0.003f, 2.0f, 0.0f, 50.0f);

		this.chunks = new ArrayList<>();

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
			chunks.add(new Chunk(Chunk.calculateXY(new Vector3f(r, g, b), Chunk.SIDE_LENGTH * Chunk.CHUNK_RADIUS, null)));

			for (int j = 0; j < Chunk.SIDE_COUNT; j++) {
				if (j == Chunk.SIDE_COUNT - 1) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + Chunk.GENERATE_DELTAS[j][0];
					g = g + Chunk.GENERATE_DELTAS[j][1];
					b = b + Chunk.GENERATE_DELTAS[j][2];
					chunks.add(new Chunk(Chunk.calculateXY(new Vector3f(r, g, b), Chunk.SIDE_LENGTH * Chunk.CHUNK_RADIUS, null)));
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
		Colour.interpolate(skyColourDay, skyColourNight, skyDriver.update(FlounderFramework.getDelta()), skyColour);
		fog.setFogColour(skyColour);

		for (Chunk chunk : chunks) {
			chunk.update(null);
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

	@Override
	public IModule getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {

	}
}
