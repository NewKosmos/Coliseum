package coliseum.world;

import flounder.camera.*;
import flounder.entities.*;
import flounder.framework.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.textures.*;

import java.util.*;

public class ColiseumWorld extends IModule {
	private static final ColiseumWorld INSTANCE = new ColiseumWorld();
	public static final String PROFILE_TAB_NAME = "Coliseum World";

	private Fog fog;
	private SkyCycle skyCycle;
	private List<Chunk> chunks;

	public ColiseumWorld() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class, FlounderEntities.class);

		this.fog = new Fog(new Colour(), 0.003f, 2.0f, 0.0f, 50.0f);
		this.skyCycle = new SkyCycle();
		this.chunks = new ArrayList<>();
	}

	@Override
	public void init() {
		for (int i = 0; i < 1; i++) {
			int shapesOnEdge = i;
			float r = 0;
			float g = -i;
			float b = i;
			chunks.add(new Chunk(Chunk.calculateXY(new Vector3f(r, g, b), Chunk.HEXAGON_SIDE_LENGTH * Chunk.CHUNK_RADIUS, null)));

			for (int j = 0; j < Chunk.HEXAGON_SIDE_COUNT; j++) {
				if (j == Chunk.HEXAGON_SIDE_COUNT - 1) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + Chunk.GENERATE_DELTAS[j][0];
					g = g + Chunk.GENERATE_DELTAS[j][1];
					b = b + Chunk.GENERATE_DELTAS[j][2];
					chunks.add(new Chunk(Chunk.calculateXY(new Vector3f(r, g, b), Chunk.HEXAGON_SIDE_LENGTH * Chunk.CHUNK_RADIUS, null)));
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
		skyCycle.update();
		fog.setFogColour(skyCycle.getSkyColour());

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

	public static SkyCycle getSkyCycle() {
		return INSTANCE.skyCycle;
	}

	@Override
	public IModule getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {

	}
}
