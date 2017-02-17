/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks;

import flounder.entities.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.noise.*;
import flounder.physics.bounding.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.chunks.meshing.*;
import kosmos.chunks.tiles.*;
import kosmos.entities.components.*;
import kosmos.entities.instances.*;

import java.util.*;

/**
 * A hexagonal chunk.
 * http://www.redblobgames.com/grids/hexagons/#range
 * http://stackoverflow.com/questions/2459402/hexagonal-grid-coordinates-to-pixel-coordinates
 */
public class Chunk extends Entity {
	protected static final float[][] GENERATE_DELTAS = new float[][]{{1.0f, 0.0f, -1.0f}, {0.0f, 1.0f, -1.0f}, {-1.0f, 1.0f, 0.0f}, {-1.0f, 0.0f, 1.0f}, {0.0f, -1.0f, 1.0f}, {1.0f, -1.0f, 0.0f}};

	public static final int HEXAGON_SIDE_COUNT = 6; // The number of sides for each figure (hexagon).
	public static final float HEXAGON_SIDE_LENGTH = 2.0f; //  Each tile can be broken into equilateral triangles with sides of length.

	public static final int CHUNK_RADIUS = 19; // The amount of tiles that make up the radius. 7-9 are the optimal chunk radius ranges.
	public static final float CHUNK_SCALE = 4.0f; // The model scale size used for each chunk.

	public static final float CHUNK_WORLD_SIZE = (float) Math.sqrt(3.0) * CHUNK_SCALE * CHUNK_RADIUS; // The overall world radius footprint per chunk.

	private Map<Tile, List<Vector3f>> tiles;
	private ChunkMesh chunkMesh;
	private boolean tilesChanged;
	private float darkness;

	public Chunk(ISpatialStructure<Entity> structure, Vector2f position, TextureObject texture) {
		super(structure, new Vector3f(position.x, 0.0f, position.y), new Vector3f());
		this.tiles = new HashMap<>();
		this.chunkMesh = new ChunkMesh(this);
		this.tilesChanged = true;
		this.darkness = 0.0f;

		new ComponentModel(this, null, CHUNK_SCALE, texture, 0);
		//	new ComponentCollider(this);
		//	new ComponentCollision(this);

		generate(this);
		FlounderLogger.log("Creating chunk at: " + position.x + ", " + position.y + ".");
	}

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
		PerlinNoise noise = new PerlinNoise(11);

		int height = (int) Math.abs(noise.noise2(position.x / 66.6f, position.y / 66.6f) * 10.0f);
		int generate = (int) Math.abs(noise.noise1((position.x + position.y) / 1000.0f) * 100.0f);
		float rotation = noise.noise1((position.x - position.y) / 66.0f) * 3600.0f;

		for (int i = 0; i < height; i++) {
			chunk.addTile(Tile.TILE_GRASS, new Vector3f(position.x, i * (float) Math.sqrt(2.0f), position.y));

			if (i == height - 1 && height > 0) {
				switch (generate) {
					case 1:
						new InstanceTreePine(FlounderEntities.getEntities(),
								new Vector3f(position.x * 0.5f * CHUNK_SCALE, (5.25f * 0.25f) + (i * (float) Math.sqrt(2.0f)) * 0.5f * CHUNK_SCALE, position.y * 0.5f * CHUNK_SCALE),
								new Vector3f(0.0f, rotation, 0.0f)
						);
						break;
					default:
						break;
				}
			}
		}
	}

	public void update(Vector3f playerPosition) {
		// Builds or rebulds this chunks mesh.
		if (tilesChanged || chunkMesh.getModel() == null) {
			chunkMesh.rebuild();
			tilesChanged = false;
		}

		// Updates the darkness of this chunk.
		if (playerPosition != null) {
			double distance = Math.sqrt(Math.pow(getPosition().x - playerPosition.x, 2.0) + Math.pow(getPosition().y - playerPosition.y, 2.0));

			if (distance >= 30.0) {
				darkness = 0.7f;
			} else {
				darkness = 0.0f;
			}
		}

		// Adds this mesh AABB to the bounding render pool.
		FlounderBounding.addShapeRender(chunkMesh.getAABB());
	}

	public Map<Tile, List<Vector3f>> getTiles() {
		return tiles;
	}

	public void addTile(Tile tile, Vector3f position) {
		if (tile == null && position == null) {
			return;
		}

		if (tiles.containsKey(tile)) {
			tiles.get(tile).add(position);
		} else {
			List<Vector3f> list = new ArrayList<>();
			list.add(position);
			tiles.put(tile, list);
		}

		tilesChanged = true;
	}

	public void removeTile(Vector3f position) {
		if (position == null) {
			return;
		}

		for (Tile tile : tiles.keySet()) {
			Iterator<Vector3f> iterator = tiles.get(tile).iterator();

			while (iterator.hasNext()) {
				Vector3f next = iterator.next();

				if (next.equals(position)) {
					iterator.remove();
					tilesChanged = true;
				}
			}
		}
	}

	public ChunkMesh getChunkMesh() {
		return chunkMesh;
	}

	public float getDarkness() {
		return darkness;
	}
}
