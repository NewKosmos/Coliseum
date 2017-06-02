/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.meshing;

import flounder.entities.components.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.processing.resource.*;
import kosmos.chunks.*;

import java.util.*;

/**
 * A class that can load chunk mesh data.
 */
public class MeshBuildRequest implements RequestResource {
	private ChunkMesh chunkMesh;
	private Map<Vector3f, Boolean[]> chunkData;

	/**
	 * Loads chunk mesh data.
	 *
	 * @param chunkMesh The chunk mesh to load to.
	 * @param chunkData The chunk position and hexagon model map.
	 */
	public MeshBuildRequest(ChunkMesh chunkMesh, Map<Vector3f, Boolean[]> chunkData) {
		this.chunkMesh = chunkMesh;
		this.chunkData = chunkData;
	}

	@Override
	public void executeRequestResource() {
		// Removes the old chunk model.
		if (chunkMesh.chunkModel != null) {
			chunkMesh.chunkModel.delete();
			chunkMesh.chunkModel = null;
		}

		// The array to store all chunk vertices into.
		List<TileVertex> vertices = new ArrayList<>();

		// Only create the model if there is stuff to build from.
		if (!chunkData.isEmpty()) {
			int indexOffset = 0;

			// Loads all tiles into a tile mesh with all positional instances within the chunk.
			for (Vector3f tile : chunkData.keySet()) {
				Boolean[] models = chunkData.get(tile);

				for (int m = 0; m < models.length; m++) {
					if (models[m]) {
						ModelObject model = KosmosChunks.get().getHexagons()[m];

						for (int i = 0; i < model.getIndices().length; i++) {
							int index = model.getIndices()[i];
							float vertex0 = model.getVertices()[index * 3] + (tile.x / 2.0f);
							float vertex1 = model.getVertices()[index * 3 + 1] + (tile.y / 2.0f);
							float vertex2 = model.getVertices()[index * 3 + 2] + (tile.z / 2.0f);
							float texture0 = model.getTextures()[index * 2];
							float texture1 = model.getTextures()[index * 2 + 1];
							float normal0 = model.getNormals()[index * 3];
							float normal1 = model.getNormals()[index * 3 + 1];
							float normal2 = model.getNormals()[index * 3 + 2];
							float tangent0 = model.getTangents()[index * 3];
							float tangent1 = model.getTangents()[index * 3 + 1];
							float tangent2 = model.getTangents()[index * 3 + 2];

							TileVertex vertex = new TileVertex(indexOffset, vertex0, vertex1, vertex2, texture0, texture1, normal0, normal1, normal2, tangent0, tangent1, tangent2, null);

							for (TileVertex v : vertices) {
								if (v.equals(vertex) && v.duplicate == null) {
									vertex.duplicate = v;
								}
							}

							vertices.add(vertex);

							// Offset the running non duplicate index total.
							if (vertex.duplicate == null) {
								indexOffset++;
							}

							// Update the mesh bounds.
							chunkMesh.minX = (vertex0 < chunkMesh.minX) ? vertex0 : chunkMesh.minX;
							chunkMesh.minY = (vertex1 < chunkMesh.minY) ? vertex1 : chunkMesh.minY;
							chunkMesh.minZ = (vertex2 < chunkMesh.minZ) ? vertex2 : chunkMesh.minZ;
							chunkMesh.maxX = (vertex0 > chunkMesh.maxX) ? vertex0 : chunkMesh.maxX;
							chunkMesh.maxY = (vertex1 > chunkMesh.maxY) ? vertex1 : chunkMesh.maxY;
							chunkMesh.maxZ = (vertex2 > chunkMesh.maxZ) ? vertex2 : chunkMesh.maxZ;
						}
					}
				}
			}

			// A constant radius ensures every chunk holds the same volume.
			chunkMesh.maxRadius = Chunk.CHUNK_WORLD_SIZE; // Maths.maxValue(maxX, maxY, maxZ, Math.abs(minX), Math.abs(minY), Math.abs(minZ));

			// Gets the resulting data stuff from the other stuff.
			float[] resultVertices = new float[indexOffset * 3];
			float[] resultTextures = new float[indexOffset * 2];
			float[] resultNormals = new float[indexOffset * 3];
			float[] resultTangents = new float[indexOffset * 3];
			int[] resultIndices = new int[vertices.size()];

			for (int i = 0; i < vertices.size(); i++) {
				if (vertices.get(i).duplicate == null) {
					resultIndices[i] = vertices.get(i).index;

					resultVertices[vertices.get(i).index * 3] = vertices.get(i).vertex0;
					resultVertices[vertices.get(i).index * 3 + 1] = vertices.get(i).vertex1;
					resultVertices[vertices.get(i).index * 3 + 2] = vertices.get(i).vertex2;

					resultTextures[vertices.get(i).index * 2] = vertices.get(i).texture0;
					resultTextures[vertices.get(i).index * 2 + 1] = vertices.get(i).texture1;

					resultNormals[vertices.get(i).index * 3] = vertices.get(i).normal0;
					resultNormals[vertices.get(i).index * 3 + 1] = vertices.get(i).normal1;
					resultNormals[vertices.get(i).index * 3 + 2] = vertices.get(i).normal2;

					resultTangents[vertices.get(i).index * 3] = vertices.get(i).tangent0;
					resultTangents[vertices.get(i).index * 3 + 1] = vertices.get(i).tangent1;
					resultTangents[vertices.get(i).index * 3 + 2] = vertices.get(i).tangent2;
				} else {
					resultIndices[i] = vertices.get(i).duplicate.index;
				}
			}

			// Delete crap.
			vertices.clear();

			// Then all model data is used to create a manual model loader, a hull is not generated and materials are baked into the textures. he model is then loaded into a object and OpenGL.
			chunkMesh.chunkModel = ModelFactory.newBuilder().setManual(new ModelLoadManual("chunk" + chunkMesh.chunk.getPosition().x + "u" + chunkMesh.chunk.getPosition().z) {
				@Override
				public float[] getVertices() {
					return resultVertices;
				}

				@Override
				public float[] getTextures() {
					return resultTextures;
				}

				@Override
				public float[] getNormals() {
					return resultNormals;
				}

				@Override
				public float[] getTangents() {
					return resultTangents;
				}

				@Override
				public int[] getIndices() {
					return resultIndices;
				}

				@Override
				public boolean isSmoothShading() {
					return false;
				}

				@Override
				public AABB getAABB() {
					return new AABB(new Vector3f(chunkMesh.minX, chunkMesh.minY, chunkMesh.minZ), new Vector3f(chunkMesh.maxX, chunkMesh.maxY + 50, chunkMesh.maxZ));
				}
			}).create();
			chunkMesh.chunk.setLoaded(true);
		} else {
			// No model if no data can be loaded.
			chunkMesh.chunkModel = null;
			chunkMesh.chunk.setLoaded(true);

			// Normal chunk size.
			chunkMesh.maxRadius = Chunk.CHUNK_WORLD_SIZE;
		}

		if (chunkMesh.chunkModel != null) {
			// The chunks model component is also updated.
			ComponentModel componentModel = (ComponentModel) chunkMesh.chunk.getComponent(ComponentModel.class);

			if (componentModel != null) {
				componentModel.setModel(chunkMesh.chunkModel);
				componentModel.setRenderCollider(false);
			} else {
				FlounderLogger.get().error(chunkMesh.chunk + " does not have a model component! Model cannot be set.");
			}
		}
	}
}
