package coliseum.world;

import flounder.entities.*;
import flounder.framework.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.textures.*;

public class ColiseumWorld extends IModule {
	private static final ColiseumWorld INSTANCE = new ColiseumWorld();
	public static final String PROFILE_TAB_NAME = "Coliseum World";

	private Fog fog;

	public ColiseumWorld() {
		super(ModuleUpdate.UPDATE_POST, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class, FlounderEntities.class);

		this.fog = new Fog(new Colour(1.0f, 1.0f, 1.0f), 0.003f, 2.0f, 0.0f, 50.0f);
	}

	@Override
	public void init() {
		for (int i = 0; i < 2; i++) {
			int shapesOnEdge = i;
			float r = 0;
			float g = -i;
			float b = i;

			generateChunk(r, g, b);

			for (int j = 0; j < 6; j++) {
				if (j == 5) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + Chunk.GENERATE_DELTAS[j][0];
					g = g + Chunk.GENERATE_DELTAS[j][1];
					b = b + Chunk.GENERATE_DELTAS[j][2];
					generateChunk(r, g, b);
				}
			}
		}
	}

	private void generateChunk(float r, float g, float b) {
		float y = (3.0f / 2.0f) * Tile.SIDE_LENGTH * Chunk.CHUNK_RADIUS * b;
		float x = (float) Math.sqrt(3.0f) * Tile.SIDE_LENGTH * Chunk.CHUNK_RADIUS * ((b / 2.0f) + r);

		//for (int i = 0; i < 1; i ++) {
		//	for (int j = 0; j < 1; j ++) {
			//	float x = i * (Chunk.CHUNK_RADIUS - 0.5f) * Tile.SIDE_LENGTH;
			//	float y = j * (Chunk.CHUNK_RADIUS - 0.5f) * Tile.SIDE_LENGTH;
				Chunk chunk = new Chunk(new Vector2f(x, y));

				for (Tile tile : chunk.getTiles()) {
					new EntityTerrain(FlounderEntities.getEntities(), new Vector3f(tile.getPosition().x, 0.0f, tile.getPosition().y), new Vector3f());

				//	if (Math.random() > 0.75) {
					if (tile.equals(chunk.getTiles().get(0))) {
						new EntityTerrain(FlounderEntities.getEntities(), new Vector3f(tile.getPosition().x, (float) (2.0 * Math.sqrt(2.0)), tile.getPosition().y), new Vector3f());
					}
				//	}
				}
		//	}
		//}
	}

	@Override
	public void update() {

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

	@Override
	public IModule getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {

	}
}
