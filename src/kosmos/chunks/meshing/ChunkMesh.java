/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.meshing;

import flounder.framework.*;
import flounder.models.*;
import flounder.physics.*;
import kosmos.chunks.*;
import kosmos.chunks.tiles.*;
import kosmos.entities.components.*;

import java.util.*;

public class ChunkMesh {
	private Chunk chunk;
	private ModelObject model;
	private AABB aabb;

	public ChunkMesh(Chunk chunk) {
		this.chunk = chunk;
		this.model = null;
		this.aabb = null;
	}

	public void rebuild() {
		// Removes old flounder.models and AABBs.
		if (model != null) {
			model.delete();
		}

		model = null;
		aabb = null;

		// Makes sure all tile flounder.models have been loaded, and have data.
		for (Tile tile : chunk.getTiles().keySet()) {
			if (tile.getModel() == null || !tile.getModel().isLoaded()) {
				return;
			}
		}

		// Loads all tiles into a tile mesh with all positional instances within the chunk.
		List<TilesMesh> tilesMeshes = new ArrayList<>();
		int previousAccumulator = 0;

		for (Tile tile : chunk.getTiles().keySet()) {
			TilesMesh tilesMesh = new TilesMesh(tile, chunk.getTiles().get(tile), previousAccumulator);
			previousAccumulator += tilesMesh.getAccumulator();
			tilesMeshes.add(tilesMesh);
		}

		// Takes all tile mesh data and appends the Number arrays together to create data for the chunk mesh.
		float[] vertices = TilesMesh.mergeF(tilesMeshes, TilesMesh::getVertices);
		float[] textures = TilesMesh.mergeF(tilesMeshes, TilesMesh::getTextures);
		float[] normals = TilesMesh.mergeF(tilesMeshes, TilesMesh::getNormals);
		float[] tangents = TilesMesh.mergeF(tilesMeshes, TilesMesh::getTangents);
		int[] indices = TilesMesh.mergeI(tilesMeshes, TilesMesh::getIndices);

		// Calculates new AABB bounds from the minimum and maximum vertex vector component positions.
		AABB modelAABB = new AABB();
		int currentPosID = 0;

		for (int i = 0; i < vertices.length; i++) {
			switch (currentPosID) {
				case (0):
					if (vertices[i] > modelAABB.getMaxExtents().x) {
						modelAABB.getMaxExtents().x = vertices[i];
					} else if (vertices[i] < modelAABB.getMinExtents().x) {
						modelAABB.getMinExtents().x = vertices[i];
					}

					break;
				case (1):
					if (vertices[i] > modelAABB.getMaxExtents().y) {
						modelAABB.getMaxExtents().y = vertices[i];
					} else if (vertices[i] < modelAABB.getMinExtents().y) {
						modelAABB.getMinExtents().y = vertices[i];
					}

					break;
				case (2):
					if (vertices[i] > modelAABB.getMaxExtents().z) {
						modelAABB.getMaxExtents().z = vertices[i];
					} else if (vertices[i] < modelAABB.getMinExtents().z) {
						modelAABB.getMinExtents().z = vertices[i];
					}

					break;
			}

			// Updates the current pos ID, this is used to keep track of what position component is looked at (0=X, 1=Y, 2=Z).
			currentPosID++;

			if (currentPosID > 2) {
				currentPosID = 0;
			}
		}

		// Makes sure the chunk takes sufficient space.
		modelAABB.getMinExtents().y = -3.0f;
		modelAABB.getMaxExtents().y = 7.0f;

		// Logs how many vertices and indices are in the chunk model.
		//	FlounderLogger.log("Vertices = " + (vertices.length / 3) + ", Indices = " + indices.length);

		// Then all model data is used to create a manual model loader, a hull is not generated and materials are baked into the textures.
		// The model is then loaded into a object and OpenGL.
		this.model = ModelFactory.newBuilder().setManual(new ModelLoadManual("chunk" + chunk.getPosition().x + "p" + chunk.getPosition().z + "t" + (int) Framework.getTimeSec()) {
			@Override
			public float[] getVertices() {
				return vertices;
			}

			@Override
			public float[] getTextureCoords() {
				return textures;
			}

			@Override
			public float[] getNormals() {
				return normals;
			}

			@Override
			public float[] getTangents() {
				return tangents;
			}

			@Override
			public int[] getIndices() {
				return indices;
			}

			@Override
			public boolean isSmoothShading() {
				return false;
			}

			@Override
			public AABB getAABB() {
				return modelAABB;
			}

			@Override
			public QuickHull getHull() {
				return null;
			}
		}).create();

		// The chunks model component is also updated.
		ComponentModel componentModel = (ComponentModel) chunk.getComponent(ComponentModel.ID);
		componentModel.setModel(model);
	}

	public Chunk getChunk() {
		return chunk;
	}

	public ModelObject getModel() {
		return model;
	}

	public AABB getAABB() {
		if (aabb == null && model != null && model.isLoaded()) {
			this.aabb = new AABB();
			AABB.recalculate(model.getAABB(), chunk.getPosition(), chunk.getRotation(), 1.0f, aabb);
		}

		return aabb;
	}

	public void delete() {
		if (model != null) {
			model.delete();
		}
	}
}
