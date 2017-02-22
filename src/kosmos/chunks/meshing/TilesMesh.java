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
import kosmos.chunks.*;
import kosmos.chunks.tiles.*;

import java.util.*;

public class TilesMesh {
	private List<TileVertex> tileVertices;
	protected float minX, minY, minZ;
	protected float maxX, maxY, maxZ;
	protected float maxRadius;

	protected TilesMesh(Tile tile, List<Vector3f> positions) {
		this.tileVertices = new ArrayList<>();

		int duplicates = 0;

		for (int p = 0; p < positions.size(); p++) {
			for (int id = 0; id < tile.getModel().getIndices().length; id++) {
				int pointer = tile.getModel().getIndices()[id];

				int index = id + (tile.getModel().getIndices().length * p);
				float vertex0 = tile.getModel().getVertices()[pointer * 3] + (positions.get(p).x / 2.0f);
				float vertex1 = tile.getModel().getVertices()[pointer * 3 + 1] + (positions.get(p).y / 2.0f);
				float vertex2 = tile.getModel().getVertices()[pointer * 3 + 2] + (positions.get(p).z / 2.0f);
				float texture0 = tile.getModel().getTextures()[pointer * 2];
				float texture1 = tile.getModel().getTextures()[pointer * 2 + 1];
				float normal0 = tile.getModel().getNormals()[pointer * 3];
				float normal1 = tile.getModel().getNormals()[pointer * 3 + 1];
				float normal2 = tile.getModel().getNormals()[pointer * 3 + 2];
				float tangent0 = tile.getModel().getTangents()[pointer * 3];
				float tangent1 = tile.getModel().getTangents()[pointer * 3 + 1];
				float tangent2 = tile.getModel().getTangents()[pointer * 3 + 2];

				minX = (vertex0 < minX) ? vertex0 : minX;
				minY = (vertex1 < minY) ? vertex1 : minY;
				minZ = (vertex2 < minZ) ? vertex2 : minZ;
				maxX = (vertex0 > maxX) ? vertex0 : maxX;
				maxY = (vertex1 > maxY) ? vertex1 : maxY;
				maxZ = (vertex2 > maxZ) ? vertex2 : maxZ;

				TileVertex vertex = new TileVertex(index, vertex0, vertex1, vertex2, texture0, texture1, normal0, normal1, normal2, tangent0, tangent1, tangent2);
				tileVertices.add(vertex);
			}
		}

		// A constant radius ensures every chunk holds the same volume.
		this.maxRadius = Chunk.CHUNK_WORLD_SIZE; // Maths.maxValue(maxX, maxY, maxZ, Math.abs(minX), Math.abs(minY), Math.abs(minZ));

		// Logs how many vertices and indices are in the chunk model.
		FlounderLogger.log("Vertices = " + (tileVertices.size() * 3) + ", Indices = " + tileVertices.size() + ", Duplicates = " + duplicates);
	}

	protected TileVertex getTileVertex(int index) {
		for (TileVertex tileVertex : tileVertices) {
			if (tileVertex.index == index) {
				return tileVertex;
			}
		}

		return null;
	}

	protected float[] getVertices() {
		float[] result = new float[tileVertices.size() * 3];

		for (int i = 0; i < tileVertices.size(); i++) {
			if (!tileVertices.get(i).duplicate) {
				result[i * 3] = tileVertices.get(i).vertex0;
				result[i * 3 + 1] = tileVertices.get(i).vertex1;
				result[i * 3 + 2] = tileVertices.get(i).vertex2;
			}
		}

		return result;
	}

	protected float[] getTextures() {
		float[] result = new float[tileVertices.size() * 2];

		for (int i = 0; i < tileVertices.size(); i++) {
			if (!tileVertices.get(i).duplicate) {
				result[i * 2] = tileVertices.get(i).texture0;
				result[i * 2 + 1] = tileVertices.get(i).texture1;
			}
		}

		return result;
	}

	protected float[] getNormals() {
		float[] result = new float[tileVertices.size() * 3];

		for (int i = 0; i < tileVertices.size(); i++) {
			if (!tileVertices.get(i).duplicate) {
				result[i * 3] = tileVertices.get(i).normal0;
				result[i * 3 + 1] = tileVertices.get(i).normal1;
				result[i * 3 + 2] = tileVertices.get(i).normal2;
			}
		}

		return result;
	}

	protected float[] getTangents() {
		float[] result = new float[tileVertices.size() * 3];

		for (int i = 0; i < tileVertices.size(); i++) {
			if (!tileVertices.get(i).duplicate) {
				result[i * 3] = tileVertices.get(i).tangent0;
				result[i * 3 + 1] = tileVertices.get(i).tangent1;
				result[i * 3 + 2] = tileVertices.get(i).tangent2;
			}
		}

		return result;
	}

	protected int[] getIndices() {
		int[] result = new int[tileVertices.size()];

		for (int i = 0; i < tileVertices.size(); i++) {
			result[i] = tileVertices.get(i).index;
		}

		return result;
	}
}
