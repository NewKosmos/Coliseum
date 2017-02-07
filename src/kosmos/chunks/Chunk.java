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
import flounder.physics.bounding.*;
import flounder.space.*;
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
	private Map<Tile, List<Vector3f>> tiles;
	private ChunkMesh chunkMesh;
	private boolean tilesChanged;
	private float darkness;

	public Chunk(ISpatialStructure<Entity> structure, Vector2f position) {
		super(structure, new Vector3f(position.x, 0.0f, position.y), new Vector3f());
		this.tiles = new HashMap<>();
		this.chunkMesh = new ChunkMesh(this);
		this.tilesChanged = true;
		this.darkness = 0.0f;

		new ComponentModel(this, null, ChunkGenerator.CHUNK_SCALE, (position.x == 0 && position.y == 0) ? Tile.TILE_STONE.getTexture() : Tile.TILE_SNOW.getTexture(), 0);
		//	new ComponentCollider(this);
		//	new ComponentCollision(this);

		ChunkGenerator.generate(this);
		FlounderLogger.log("Chunk[ " + position.x + ", " + position.y + " ]");
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

	public static Vector3f hexagonSpace(Vector2f position, float length, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		destination.x = 0.0f;
		destination.y = 0.0f;
		destination.z = 0.0f;
		return destination;
	}

	public static Vector2f worldSpace2D(Vector3f position, float length, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		destination.x = position.x * length;
		destination.y = position.y * length;
		return destination;
	}
}
