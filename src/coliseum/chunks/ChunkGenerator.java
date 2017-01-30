package coliseum.chunks;

import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.textures.*;

public class ChunkGenerator {
	protected static final float[][] GENERATE_DELTAS = new float[][]{{1.0f, 0.0f, -1.0f}, {0.0f, 1.0f, -1.0f}, {-1.0f, 1.0f, 0.0f}, {-1.0f, 0.0f, 1.0f}, {0.0f, -1.0f, 1.0f}, {1.0f, -1.0f, 0.0f}};

	protected static final int HEXAGON_SIDE_COUNT = 6; // The number of sides for each figure (hexagon).
	protected static final float HEXAGON_SIDE_LENGTH = 2.0f; //  Each tile can be broken into equilateral triangles with sides of length.

	protected static final int CHUNK_RADIUS = 9; // The amount of tiles that make up the radius. 7-9 are the optimal chunk radius ranges.

	protected static void generate(Chunk chunk) {
		for (int i = 0; i < CHUNK_RADIUS; i++) {
			int shapesOnEdge = i;
			float r = 0;
			float g = -i;
			float b = i;
			generateTile(chunk, Vector2f.add(chunk.getPosition(), ChunkMaths.calculateXY(new Vector3f(r, g, b), HEXAGON_SIDE_LENGTH, null), null));

			for (int j = 0; j < HEXAGON_SIDE_COUNT; j++) {
				if (j == HEXAGON_SIDE_COUNT - 1) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + GENERATE_DELTAS[j][0];
					g = g + GENERATE_DELTAS[j][1];
					b = b + GENERATE_DELTAS[j][2];
					generateTile(chunk, Vector2f.add(chunk.getPosition(), ChunkMaths.calculateXY(new Vector3f(r, g, b), HEXAGON_SIDE_LENGTH, null), null));
				}
			}
		}
	}

	private static final Model TESTING_MODEL = Model.newModel(new MyFile(MyFile.RES_FOLDER, "terrains", "stone", "stone.obj")).create();
	private static final Texture TESTING_TEXTURE = Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "stone", "stone.png")).clampEdges().create();

	protected static void generateTile(Chunk chunk, Vector2f position) {
		/*float chance = Maths.randomInRange(0.0f, 4.0f);
		float height = Math.random() > 0.85 ? (2.0f * (float) Math.sqrt(2.0f)) : 0.0f;

		if (Math.abs(position.x) < 5 && Math.abs(position.y) < 5) {
			chance = 0.0f;
			height = 0.0f;
		}

		if (chance > 3.0f) {
			chunk.addTile(new TerrainWater(FlounderEntities.getEntities(), new Vector3f(position.x, 0.0f, position.y), new Vector3f(), chunk));
			height = 0.0f;
		} else {
			if (height > 0.0f) {
				chunk.addTile(new TerrainStone(FlounderEntities.getEntities(), new Vector3f(position.x, 0.0f, position.y), new Vector3f(), chunk));
			}

			if (chance > 2.0f) {
				chunk.addTile(new TerrainSand(FlounderEntities.getEntities(), new Vector3f(position.x, height, position.y), new Vector3f(), chunk));
			} else if (chance > 1.0f) {
				chunk.addTile(new TerrainStone(FlounderEntities.getEntities(), new Vector3f(position.x, height, position.y), new Vector3f(), chunk));
			} else if (chance >= 0.0f) {
				chunk.addTile(new TerrainGrass(FlounderEntities.getEntities(), new Vector3f(position.x, height, position.y), new Vector3f(), chunk));
			}
		}*/

		chunk.addTile(new Tile(new Vector3f(position.x, 0.0f, position.y), TESTING_MODEL, TESTING_TEXTURE));

		//if (Math.random() > 0.98) {
		//	chunk.addTile(new TerrainRockGem(FlounderEntities.getEntities(), new Vector3f(position.x, height, position.y), new Vector3f(), chunk));
		//}
	}
}
