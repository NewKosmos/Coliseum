/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks;

import flounder.maths.vectors.*;
import kosmos.chunks.tiles.*;

public class ChunkGenerator {
	protected static final float[][] GENERATE_DELTAS = new float[][]{{1.0f, 0.0f, -1.0f}, {0.0f, 1.0f, -1.0f}, {-1.0f, 1.0f, 0.0f}, {-1.0f, 0.0f, 1.0f}, {0.0f, -1.0f, 1.0f}, {1.0f, -1.0f, 0.0f}};

	public static final int HEXAGON_SIDE_COUNT = 6; // The number of sides for each figure (hexagon).
	public static final float HEXAGON_SIDE_LENGTH = 2.0f; //  Each tile can be broken into equilateral triangles with sides of length.

	public static final int CHUNK_RADIUS = 4; // The amount of tiles that make up the radius. 7-9 are the optimal chunk radius ranges.
	public static final float CHUNK_SCALE = 2.0f; // The model scale size used for each chunk.

	public static final float CHUNK_WORLD_SIZE = ChunkGenerator.CHUNK_SCALE * (float) Math.sqrt(3.0) * (ChunkGenerator.CHUNK_RADIUS - 0.5f); // The overall world radius footprint per chunk.

	protected static void generate(Chunk chunk) {
		for (int i = 0; i < CHUNK_RADIUS; i++) {
			int shapesOnEdge = i;
			float r = 0;
			float g = -i;
			float b = i;
			generateTile(chunk, Vector2f.add(chunk.getPosition().toVector2f(), Tile.worldSpace2D(new Vector3f(r, g, b), HEXAGON_SIDE_LENGTH, null), null));

			for (int j = 0; j < HEXAGON_SIDE_COUNT; j++) {
				if (j == HEXAGON_SIDE_COUNT - 1) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + GENERATE_DELTAS[j][0];
					g = g + GENERATE_DELTAS[j][1];
					b = b + GENERATE_DELTAS[j][2];
					generateTile(chunk, Vector2f.add(chunk.getPosition().toVector2f(), Tile.worldSpace2D(new Vector3f(r, g, b), HEXAGON_SIDE_LENGTH, null), null));
				}
			}
		}
	}

	protected static void generateTile(Chunk chunk, Vector2f position) {
		/*float chance = (float) Math.random() * 4.0f;
		float height = Math.random() > 0.35 ? -(ChunkGenerator.CHUNK_SCALE * (float) Math.sqrt(2.0f)) : 0.0f;

		if (Math.abs(position.x) < 5 && Math.abs(position.y) < 5) {
			chance = 0.0f;
			height = 0.0f;
		}

		{
			//if (height > 0.0f) {
			//	chunk.addTile(Tile.TILE_STONE, new Vector3f(position.x, 0.0f, position.y));
			//}

			if (chance > 2.0f) {
				chunk.addTile(Tile.TILE_SAND, new Vector3f(position.x, height, position.y));
			} else if (chance > 1.0f) {
				chunk.addTile(Tile.TILE_STONE, new Vector3f(position.x, height, position.y));
			} else if (chance >= 0.0f) {
				chunk.addTile(Tile.TILE_GRASS, new Vector3f(position.x, height, position.y));
			}
		}*/

		chunk.addTile(Tile.TILE_GRASS, new Vector3f(position.x, 0.0f, position.y));

		//if (Math.random() > 0.98) {
		//	chunk.addTile(Tile.TILE_ROCK_GEM, new Vector3f(position.x, height, position.y));
		//}
	}
}
