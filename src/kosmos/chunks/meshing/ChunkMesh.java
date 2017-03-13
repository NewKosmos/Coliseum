/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.meshing;

import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import kosmos.chunks.*;
import kosmos.entities.components.*;

import java.util.*;

public class ChunkMesh {
	private Chunk chunk;

	private ModelObject chunkModel;

	protected float minX, minY, minZ;
	protected float maxX, maxY, maxZ;
	protected float maxRadius;

	private List<TileVertex> vertices;

	public ChunkMesh(Chunk chunk) {
		this.chunk = chunk;

		this.chunkModel = null;

		this.vertices = new ArrayList<>();
	}

	public synchronized void rebuild(List<Vector3f> positions, ModelObject model) {
		// Makes sure all chunk, model, and biome info is good.
		if (chunk == null || chunk.getBiome() == null || model == null || !model.isLoaded()) {
			return;
		}

		// Removes the old chunk model.
		if (chunkModel != null) {
			chunkModel.delete();
			chunkModel = null;
		}

		// Prepares a set of data to write into.
		if (vertices == null) {
			vertices = new ArrayList<>();
		} else {
			vertices.clear();
		}

		// Loads all tiles into a tile mesh with all positional instances within the chunk.
		for (int p = 0; p < positions.size(); p++) {
			for (int i = 0; i < model.getIndices().length; i++) {
				int pointer = model.getIndices()[i];

				int index = pointer + (model.getIndices().length * p);
				float vertex0 = model.getVertices()[pointer * 3] + (positions.get(p).x / 2.0f);
				float vertex1 = model.getVertices()[pointer * 3 + 1] + (positions.get(p).y / 2.0f);
				float vertex2 = model.getVertices()[pointer * 3 + 2] + (positions.get(p).z / 2.0f);
				float texture0 = model.getTextures()[pointer * 2];
				float texture1 = model.getTextures()[pointer * 2 + 1];
				float normal0 = model.getNormals()[pointer * 3];
				float normal1 = model.getNormals()[pointer * 3 + 1];
				float normal2 = model.getNormals()[pointer * 3 + 2];
				float tangent0 = model.getTangents()[pointer * 3];
				float tangent1 = model.getTangents()[pointer * 3 + 1];
				float tangent2 = model.getTangents()[pointer * 3 + 2];

				minX = (vertex0 < minX) ? vertex0 : minX;
				minY = (vertex1 < minY) ? vertex1 : minY;
				minZ = (vertex2 < minZ) ? vertex2 : minZ;
				maxX = (vertex0 > maxX) ? vertex0 : maxX;
				maxY = (vertex1 > maxY) ? vertex1 : maxY;
				maxZ = (vertex2 > maxZ) ? vertex2 : maxZ;

				TileVertex vertex = new TileVertex(index, vertex0, vertex1, vertex2, texture0, texture1, normal0, normal1, normal2, tangent0, tangent1, tangent2);
				vertices.add(vertex);
			}
		}

		// A constant radius ensures every chunk holds the same volume.
		this.maxRadius = Chunk.CHUNK_WORLD_SIZE; // Maths.maxValue(maxX, maxY, maxZ, Math.abs(minX), Math.abs(minY), Math.abs(minZ));

		// Then all model data is used to create a manual model loader, a hull is not generated and materials are baked into the textures. he model is then loaded into a object and OpenGL.
		this.chunkModel = ModelFactory.newBuilder().setManual(new ModelLoadManual("chunk" + chunk.getPosition().x + "p" + chunk.getPosition().z) {
			@Override
			public float[] getVertices() {
				float[] result = new float[vertices.size() * 3];

				for (int i = 0; i < vertices.size(); i++) {
					result[vertices.get(i).index * 3] = vertices.get(i).vertex0;
					result[vertices.get(i).index * 3 + 1] = vertices.get(i).vertex1;
					result[vertices.get(i).index * 3 + 2] = vertices.get(i).vertex2;
				}

				return result;
			}

			@Override
			public float[] getTextureCoords() {
				float[] result = new float[vertices.size() * 2];

				for (int i = 0; i < vertices.size(); i++) {
					result[vertices.get(i).index * 2] = vertices.get(i).texture0;
					result[vertices.get(i).index * 2 + 1] = vertices.get(i).texture1;
				}

				return result;
			}

			@Override
			public float[] getNormals() {
				float[] result = new float[vertices.size() * 3];

				for (int i = 0; i < vertices.size(); i++) {
					result[vertices.get(i).index * 3] = vertices.get(i).normal0;
					result[vertices.get(i).index * 3 + 1] = vertices.get(i).normal1;
					result[vertices.get(i).index * 3 + 2] = vertices.get(i).normal2;
				}

				return result;
			}

			@Override
			public float[] getTangents() {
				float[] result = new float[vertices.size() * 3];

				for (int i = 0; i < vertices.size(); i++) {
					result[vertices.get(i).index * 3] = vertices.get(i).tangent0;
					result[vertices.get(i).index * 3 + 1] = vertices.get(i).tangent1;
					result[vertices.get(i).index * 3 + 2] = vertices.get(i).tangent2;
				}

				return result;
			}

			@Override
			public int[] getIndices() {
				int[] result = new int[vertices.size()];

				for (int i = 0; i < vertices.size(); i++) {
					result[i] = vertices.get(i).index;
				}

				return result;
			}

			@Override
			public boolean isSmoothShading() {
				return false;
			}

			@Override
			public AABB getAABB() {
				return new AABB(new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ));
			}

			@Override
			public QuickHull getHull() {
				return null; // hull
			}
		}).create();

		// The chunks model component is also updated.
		ComponentModel componentModel = (ComponentModel) chunk.getComponent(ComponentModel.ID);

		if (componentModel != null) {
			componentModel.setModel(chunkModel);
		} else {
			FlounderLogger.error(chunk + " does not have a model component! Model cannot be set.");
		}

		// Updates the chunks sphere.
		chunk.getSphere().setRadius(1.0f);
		Sphere.recalculate(chunk.getSphere(), chunk.getPosition(), maxRadius, chunk.getSphere());

		// Updates the chunks AABB.
		chunk.getAABB().getMinExtents().set(minX, minY - 3.0f, minZ);
		chunk.getAABB().getMaxExtents().set(maxX, maxY + 7.0f, maxZ);
		AABB.recalculate(chunk.getAABB(), chunk.getPosition(), chunk.getRotation(), 1.0f, chunk.getAABB());

		// Removes vertex data after some time.
		new java.util.Timer().schedule(
				new java.util.TimerTask() {
					@Override
					public void run() {
						if (vertices != null) {
							vertices.clear();
							vertices = null;
						}
					}
				},
				2500
		);
	}

	public ModelObject getModel() {
		return chunkModel;
	}

	public void delete() {
		if (vertices != null) {
			vertices.clear();
			vertices = null;
		}

		if (chunkModel != null) {
			chunkModel.delete();
			chunkModel = null;
		}
	}
}
