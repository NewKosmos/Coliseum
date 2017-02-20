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
import flounder.maths.vectors.*;
import flounder.physics.*;
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
	protected static final float[][] TILE_DELTAS = new float[][]{{1.0f, 0.0f, -1.0f}, {0.0f, 1.0f, -1.0f}, {-1.0f, 1.0f, 0.0f}, {-1.0f, 0.0f, 1.0f}, {0.0f, -1.0f, 1.0f}, {1.0f, -1.0f, 0.0f}};
	protected static final float[][] CHUNK_DELTAS = new float[][]{{9.5f, 7.0f}, {-0.5f, 13.0f}, {-10.0f, 6.0f}, {-9.5f, -7.0f}, {0.5f, -13.0f}, {10.0f, -6.0f}};

	public static final double HEXAGON_SIDE_LENGTH = 2.0; //  Each tile can be broken into equilateral triangles with sides of length.

	public static final int CHUNK_RADIUS = 7; // The amount of tiles that make up the radius. 7-9 are the optimal chunk radius ranges.

	private ISpatialStructure<Entity> entities;

	private List<Chunk> childrenChunks;
	private Map<Tile, List<Vector3f>> tiles;
	private ChunkMesh chunkMesh;
	private boolean tilesChanged;
	private float darkness;

	public Chunk(ISpatialStructure<Entity> structure, Vector3f position, TextureObject texture) {
		super(structure, position, new Vector3f());
		this.entities = new StructureBasic<>();

		this.childrenChunks = new ArrayList<>();
		this.tiles = new HashMap<>();
		this.chunkMesh = new ChunkMesh(this);
		this.tilesChanged = true;
		this.darkness = 0.0f;

		ComponentModel componentModel = new ComponentModel(this, null, 1.0f, texture, 0);
		ComponentCollider componentCollider = new ComponentCollider(this);
		ComponentCollision componentCollision = new ComponentCollision(this);

		generate(this);
		FlounderLogger.log("Creating chunk at: " + position.x + ", " + position.z + ".");
	}

	protected void createChunksAround() {
		for (int i = 0; i < 6; i++) {
			// These three variables find the positioning for chunks around the parent.
			float x = this.getPosition().x + (CHUNK_DELTAS[i][0] * (float) Math.sqrt(3.0));
			float z = this.getPosition().z + (CHUNK_DELTAS[i][1] * 1.5f);
			Vector3f p = new Vector3f(x, 0.0f, z);
			boolean chunkExists = false;

			for (Entity entity : KosmosChunks.getChunks().getAll(new ArrayList<>())) {
				Chunk chunk = (Chunk) entity;

				if (chunk.getPosition().equals(p)) {
					chunkExists = true;
				}
			}

			if (!chunkExists) {
				Chunk chunk = new Chunk(KosmosChunks.getChunks(), p, Tile.TILE_GRASS.getTexture());
				childrenChunks.add(chunk);
				KosmosChunks.getChunks().add(chunk);
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
		boolean generate = (KosmosChunks.getNoise().noise1((worldPos.x + worldPos.y) / 11.0f) * 20.0f) > 1.0f;
		int genID = (int) (KosmosChunks.getNoise().noise1((worldPos.y - worldPos.x) / 11.0f) * 200.0f);
		float rotation = KosmosChunks.getNoise().noise1((worldPos.x - worldPos.y) / 66.6f) * 3600.0f;

		for (int i = 0; i < height; i++) {
			chunk.addTile(Tile.TILE_GRASS, new Vector3f(position.x, i * (float) Math.sqrt(2.0f), position.y));

			if (generate && i == height - 1 && height > 0) {
				switch (genID) {
					case 1:
						new InstanceTreePine(chunk.entities,
								new Vector3f(
										chunk.getPosition().x + (float) (position.x * 0.5),
										(float) ((1.5 * 0.25) + (i * Math.sqrt(2.0)) * 0.5),
										chunk.getPosition().z + (float) (position.y * 0.5)
								),
								new Vector3f(0.0f, rotation, 0.0f)
						);
						break;
					case 2:
						new InstanceTree1(chunk.entities,
								new Vector3f(
										chunk.getPosition().x + (float) (position.x * 0.5),
										(float) ((2.0 * 0.25) + (i * Math.sqrt(2.0)) * 0.5),
										chunk.getPosition().z + (float) (position.y * 0.5)
								),
								new Vector3f(0.0f, rotation, 0.0f)
						);
						break;
					case 3:
						new InstanceTree3(chunk.entities,
								new Vector3f(
										chunk.getPosition().x + (float) (position.x * 0.5),
										(float) ((2.5 * 0.25) + (i * Math.sqrt(2.0)) * 0.5),
										chunk.getPosition().z + (float) (position.y * 0.5)
								),
								new Vector3f(0.0f, rotation, 0.0f)
						);
						break;
					case 4:
						new InstanceBush(chunk.entities,
								new Vector3f(
										chunk.getPosition().x + (float) (position.x * 0.5),
										(float) ((2.5 * 0.25) + (i * Math.sqrt(2.0)) * 0.5),
										chunk.getPosition().z + (float) (position.y * 0.5)
								),
								new Vector3f(0.0f, rotation, 0.0f)
						);
						break;
					default:
						break;
				}
			}
		}
		//chunk.addTile(Tile.TILE_GRASS, new Vector3f(position.x, 0.0f, position.y));
	}

	public void update(Vector3f playerPosition) {
		// Builds or rebulds this chunks mesh.
		if (tilesChanged || chunkMesh.getModel() == null) {
			chunkMesh.rebuild();
			tilesChanged = false;
		}

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

	public ISpatialStructure<Entity> getEntities() {
		return entities;
	}

	public List<Chunk> getChildrenChunks() {
		return childrenChunks;
	}

	public ChunkMesh getChunkMesh() {
		return chunkMesh;
	}

	public float getDarkness() {
		return darkness;
	}

	public boolean isLoaded() {
		return chunkMesh.getModel() != null && chunkMesh.getModel().isLoaded();
	}

	@Override
	public IBounding getBounding() {
		return chunkMesh.getAABB();
	}

	public void delete() {
		tiles.clear();
		chunkMesh.delete();
	}
}
