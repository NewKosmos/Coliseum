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
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.space.*;
import kosmos.chunks.biomes.*;
import kosmos.chunks.meshing.*;
import kosmos.chunks.tiles.*;
import kosmos.entities.components.*;

import java.util.*;

/**
 * A hexagonal chunk.
 * http://www.redblobgames.com/grids/hexagons/#range
 * http://stackoverflow.com/questions/2459402/hexagonal-grid-coordinates-to-pixel-coordinates
 */
public class Chunk extends Entity {
	protected static final float[][] TILE_DELTAS = new float[][]{{1.0f, 0.0f, -1.0f}, {0.0f, 1.0f, -1.0f}, {-1.0f, 1.0f, 0.0f}, {-1.0f, 0.0f, 1.0f}, {0.0f, -1.0f, 1.0f}, {1.0f, -1.0f, 0.0f}};
	protected static final double[][] CHUNK_DELTAS = new double[][]{{9.5, 7.0}, {-0.5, 13.0}, {-10.0, 6.0}, {-9.5, -7.0}, {0.5, -13.0}, {10.0, -6.0}};

	public static final double HEXAGON_SIDE_LENGTH = 2.0; //  Each tile can be broken into equilateral triangles with sides of length.

	public static final int CHUNK_RADIUS = 7; // The amount of tiles that make up the radius. 7-9 are the optimal chunk radius ranges.

	private ISpatialStructure<Entity> entities;

	private List<Chunk> childrenChunks;
	private IBiome.Biomes biome;
	private ChunkMesh chunkMesh;

	private List<Vector3f> tiles;
	private boolean tilesChanged;
	private float darkness;

	public Chunk(ISpatialStructure<Entity> structure, Vector3f position) {
		super(structure, position, new Vector3f());
		this.entities = new StructureBasic<>();

		float biomeID = Math.abs(KosmosChunks.getNoise().noise1((position.x + position.z) / 100.0f)) * 3.0f * IBiome.Biomes.values().length;
		biomeID = Maths.clamp((int) biomeID, 0.0f, IBiome.Biomes.values().length - 1);
		FlounderLogger.error("BiomeID: " + biomeID);

		this.childrenChunks = new ArrayList<>();
		this.biome = IBiome.Biomes.values()[(int) biomeID]; // IBiome.Biomes.random();
		this.chunkMesh = new ChunkMesh(this);

		this.tiles = new ArrayList<>();
		this.tilesChanged = true;
		this.darkness = 0.0f;

		ComponentModel componentModel = new ComponentModel(this, null, 1.0f, biome.getBiome().getMainTile().getTexture(), 0);
		ComponentCollider componentCollider = new ComponentCollider(this);
		ComponentCollision componentCollision = new ComponentCollision(this);

		generate(this);
		FlounderLogger.log("Creating chunk at: " + position.x + ", " + position.z + ".");
	}

	protected void createChunksAround() {
		if (childrenChunks.size() == 6) {
			return;
		}

		for (int i = 0; i < 6; i++) {
			// These three variables find the positioning for chunks around the parent.
			float x = this.getPosition().x + (float) (CHUNK_DELTAS[i][0] * Math.sqrt(3.0));
			float z = this.getPosition().z + (float) (CHUNK_DELTAS[i][1] * 1.5f);
			Vector3f p = new Vector3f(x, 0.0f, z);
			Chunk duplicate = null;

			for (Entity entity : KosmosChunks.getChunks().getAll()) {
				Chunk chunk = (Chunk) entity;

				if (p.equals(chunk.getPosition())) {
					duplicate = chunk;
				}
			}

			if (duplicate == null) {
				Chunk chunk = new Chunk(KosmosChunks.getChunks(), p);
				childrenChunks.add(chunk);
				KosmosChunks.getChunks().add(chunk);
			} else {
				childrenChunks.add(duplicate);
			}
		}
	}

	protected static void generate(Chunk chunk) {
		for (int i = 0; i < CHUNK_RADIUS; i++) {
			int shapesOnEdge = i;
			float r = 0.0f;
			float g = -i;
			float b = i;
			generateTile(chunk, Tile.worldSpace2D(new Vector3f(r, g, b), HEXAGON_SIDE_LENGTH, null));

			for (int j = 0; j < 6; j++) {
				if (j == 5) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + TILE_DELTAS[j][0];
					g = g + TILE_DELTAS[j][1];
					b = b + TILE_DELTAS[j][2];
					generateTile(chunk, Tile.worldSpace2D(new Vector3f(r, g, b), HEXAGON_SIDE_LENGTH, null));
				}
			}
		}
	}

	protected static void generateTile(Chunk chunk, Vector2f position) {
		Vector2f worldPos = new Vector2f(position.x + (chunk.getPosition().x * 2.0f), position.y + (chunk.getPosition().z * 2.0f));
		int height = (int) Math.abs(KosmosChunks.getNoise().noise2(
				worldPos.x / 66.6f,
				worldPos.y / 66.6f
		) * 10.0f); // (int) worldPos.length() / 7;

		for (int i = 0; i < height; i++) {
			chunk.addTile(new Vector3f(position.x, i * (float) Math.sqrt(2.0f), position.y));

			if (i == height - 1 && height > 0) {
				if (!((KosmosChunks.getNoise().noise1((worldPos.x + worldPos.y) / 11.0f) * 20.0f) > 1.0f)) {
					chunk.biome.getBiome().generateEntity(chunk, worldPos, position, i);
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

		Iterator it = childrenChunks.iterator();

		while (it.hasNext()) {
			Chunk child = (Chunk) it.next();

			if (!KosmosChunks.getChunks().contains(child)) {
				it.remove();
			}
		}

		//FlounderLogger.log(getBounding());

		// Updates the darkness of this chunk.
		/*if (playerPosition != null) {
			double distance = Math.sqrt(Math.pow(getPosition().x - playerPosition.x, 2.0) + Math.pow(getPosition().y - playerPosition.y, 2.0));

			if (distance >= 30.0) {
				darkness = 0.7f;
			} else {
				darkness = 0.0f;
			}
		}*/

		// Adds this mesh AABB to the bounding render pool.
		//FlounderBounding.addShapeRender(chunkMesh.getAABB());
		FlounderBounding.addShapeRender(chunkMesh.getSphere());
	}

	public List<Vector3f> getTiles() {
		return tiles;
	}

	public void addTile(Vector3f position) {
		if (position == null) {
			return;
		}

		tiles.add(position);
		tilesChanged = true;
	}

	public void removeTile(Vector3f position) {
		if (position == null) {
			return;
		}

		tiles.remove(position);
		tilesChanged = true;
	}

	public ISpatialStructure<Entity> getEntities() {
		return entities;
	}

	public List<Chunk> getChildrenChunks() {
		return childrenChunks;
	}

	public ChunkMesh getChunkMesh() {
		return chunkMesh;
	}

	public IBiome.Biomes getBiome() {
		return biome;
	}

	public float getDarkness() {
		return darkness;
	}

	public boolean isLoaded() {
		return chunkMesh.getModel() != null && chunkMesh.getModel().isLoaded();
	}

	@Override
	public IBounding getBounding() {
		return chunkMesh.getSphere();
	}

	public void delete() {
		for (Entity entity : entities.getAll()) {
			entity.forceRemove(false);
		}

		entities.clear();
		tiles.clear();
		chunkMesh.delete();
	}
}
