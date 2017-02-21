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
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import kosmos.chunks.*;
import kosmos.entities.components.*;

import java.util.*;

public class ChunkMesh {
	private Chunk chunk;

	private List<TileVertex> vertices;
	private ModelObject model;
	private Sphere sphere;
	private AABB aabb;

	public ChunkMesh(Chunk chunk) {
		this.chunk = chunk;

		this.vertices = null;
		this.model = null;
		this.aabb = null;
	}

	public void rebuild() {
		// Removes old flounder.models and AABBs.
		if (model != null) {
			model.delete();
		}

		vertices = new ArrayList<>();
		model = null;
		sphere = null;
		aabb = null;

		// Makes sure all tile flounder.models have been loaded, and have data.
		if (!chunk.getBiome().getBiome().getMainTile().getModel().isLoaded()) {
			return;
		}

		// Loads all tiles into a tile mesh with all positional instances within the chunk.
		TilesMesh tilesMesh = new TilesMesh(chunk.getBiome().getBiome().getMainTile(), chunk.getTiles());

		// Creates a sphere.
		this.sphere = new Sphere();
		Sphere.recalculate(sphere, chunk.getPosition(), tilesMesh.maxRadius, sphere);

		// Creates a AABB.
		this.aabb = new AABB(new Vector3f(tilesMesh.minX, tilesMesh.minY - 3.0f, tilesMesh.minZ), new Vector3f(tilesMesh.maxX, tilesMesh.maxY + 7.0f, tilesMesh.maxZ));
		AABB.recalculate(aabb, chunk.getPosition(), chunk.getRotation(), 1.0f, aabb);

		// Then all model data is used to create a manual model loader, a hull is not generated and materials are baked into the textures. he model is then loaded into a object and OpenGL.
		this.model = ModelFactory.newBuilder().setManual(new ModelLoadManual("chunk" + chunk.getPosition().x + "p" + chunk.getPosition().z + "t" + (int) Framework.getTimeSec()) {
			@Override
			public float[] getVertices() {
				return tilesMesh.getVertices();
			}

			@Override
			public float[] getTextureCoords() {
				return tilesMesh.getTextures();
			}

			@Override
			public float[] getNormals() {
				return tilesMesh.getNormals();
			}

			@Override
			public float[] getTangents() {
				return tilesMesh.getTangents();
			}

			@Override
			public int[] getIndices() {
				return tilesMesh.getIndices();
			}

			@Override
			public boolean isSmoothShading() {
				return false;
			}

			@Override
			public AABB getAABB() {
				return aabb;
			}

			@Override
			public QuickHull getHull() {
				return null;
			}
		}).create();

		// The chunks model component is also updated.
		ComponentModel componentModel = (ComponentModel) chunk.getComponent(ComponentModel.ID);

		if (componentModel != null) {
			componentModel.setModel(model);
		} else {
			FlounderLogger.error(chunk + " does not have a model component! Model cannot be set.");
		}

		// Clears unneeded data.
		if (vertices != null) {
			vertices.clear();
			vertices = null;
		}
	}

	public ModelObject getModel() {
		return model;
	}

	public Sphere getSphere() {
		return sphere;
	}

	public AABB getAABB() {
		return aabb;
	}

	public void delete() {
		if (model != null) {
			model.delete();
			model = null;
			sphere = null;
			aabb = null;
		}
	}
}
