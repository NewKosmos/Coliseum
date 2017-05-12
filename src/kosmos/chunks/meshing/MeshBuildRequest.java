/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.meshing;

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
	private ModelObject modelBase;
	private List<Vector3f> positions;

	/**
	 * Loads chunk mesh data.
	 *
	 * @param chunkMesh The chunk mesh to load to.
	 * @param modelBase The model to be used as each tile in the mesh.
	 */
	public MeshBuildRequest(ChunkMesh chunkMesh, ModelObject modelBase) {
		this.chunkMesh = chunkMesh;
		this.modelBase = modelBase;

		// Generates all tile positions for this chunk. Needs to be run on main for some reason...
		this.positions = Chunk.generate(chunkMesh.chunk);
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
		if (!positions.isEmpty()) {
			// TODO: Break model into face objects and transform those, remove matching faces from world (never seen), take all faces left and mesh into the model.

			// Loads all tiles into a tile mesh with all positional instances within the chunk.
			for (int p = 0; p < positions.size(); p++) {
				for (int i = 0; i < modelBase.getIndices().length; i++) {
					int pointer = modelBase.getIndices()[i];

					int index = pointer + (modelBase.getIndices().length * p);
					float vertex0 = modelBase.getVertices()[pointer * 3] + (positions.get(p).x / 2.0f);
					float vertex1 = modelBase.getVertices()[pointer * 3 + 1] + (positions.get(p).y / 2.0f);
					float vertex2 = modelBase.getVertices()[pointer * 3 + 2] + (positions.get(p).z / 2.0f);
					float texture0 = modelBase.getTextures()[pointer * 2];
					float texture1 = modelBase.getTextures()[pointer * 2 + 1];
					float normal0 = modelBase.getNormals()[pointer * 3];
					float normal1 = modelBase.getNormals()[pointer * 3 + 1];
					float normal2 = modelBase.getNormals()[pointer * 3 + 2];
					float tangent0 = modelBase.getTangents()[pointer * 3];
					float tangent1 = modelBase.getTangents()[pointer * 3 + 1];
					float tangent2 = modelBase.getTangents()[pointer * 3 + 2];

					chunkMesh.minX = (vertex0 < chunkMesh.minX) ? vertex0 : chunkMesh.minX;
					chunkMesh.minY = (vertex1 < chunkMesh.minY) ? vertex1 : chunkMesh.minY;
					chunkMesh.minZ = (vertex2 < chunkMesh.minZ) ? vertex2 : chunkMesh.minZ;
					chunkMesh.maxX = (vertex0 > chunkMesh.maxX) ? vertex0 : chunkMesh.maxX;
					chunkMesh.maxY = (vertex1 > chunkMesh.maxY) ? vertex1 : chunkMesh.maxY;
					chunkMesh.maxZ = (vertex2 > chunkMesh.maxZ) ? vertex2 : chunkMesh.maxZ;

					TileVertex vertex = new TileVertex(index, vertex0, vertex1, vertex2, texture0, texture1, normal0, normal1, normal2, tangent0, tangent1, tangent2);
					vertices.add(vertex);
				}
			}

			// A constant radius ensures every chunk holds the same volume.
			chunkMesh.maxRadius = Chunk.CHUNK_WORLD_SIZE; // Maths.maxValue(maxX, maxY, maxZ, Math.abs(minX), Math.abs(minY), Math.abs(minZ));

			// Gets the resulting data stuff from the other stuff.
			float[] resultVertices = new float[vertices.size() * 3];
			float[] resultTextures = new float[vertices.size() * 2];
			float[] resultNormals = new float[vertices.size() * 3];
			float[] resultTangents = new float[vertices.size() * 3];
			int[] resultIndices = new int[vertices.size()];

			for (int i = 0; i < vertices.size(); i++) {
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

				resultIndices[i] = vertices.get(i).index;
			}

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
					return null; // new AABB(new Vector3f(chunkMesh.minX, chunkMesh.minY, chunkMesh.minZ), new Vector3f(chunkMesh.maxX, chunkMesh.maxY, chunkMesh.maxZ));
				}
			}).create();
		} else {
			// No model if no data can be loaded.
			chunkMesh.chunkModel = null;

			// Normal chunk size.
			chunkMesh.maxRadius = Chunk.CHUNK_WORLD_SIZE;
		}

		// Updates the chunks sphere.
		chunkMesh.chunk.getSphere().setRadius(1.0f);
		chunkMesh.chunk.getSphere().update(chunkMesh.chunk.getPosition(), null, chunkMesh.maxRadius, chunkMesh.chunk.getSphere());
	}
}
