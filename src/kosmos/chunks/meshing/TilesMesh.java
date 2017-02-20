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
import kosmos.chunks.tiles.*;

import java.util.*;
import java.util.function.*;

public class TilesMesh {
	private List<TileVertex> tileVertices;
	private int accumulator;

	public TilesMesh(Tile tile, List<Vector3f> positions, int previousAccumulator) {
		this.tileVertices = new ArrayList<>();
		this.accumulator = 0;

		//
		for (int p = 0; p < positions.size(); p++) {
			for (int id = 0; id < tile.getModel().getIndices().length; id++) {
				int pointer = tile.getModel().getIndices()[id];
				int i = id + previousAccumulator + accumulator;
				float v0 = tile.getModel().getVertices()[pointer * 3] + (positions.get(p).x / 2.0f);
				float v1 = tile.getModel().getVertices()[pointer * 3 + 1] + (positions.get(p).y / 2.0f);
				float v2 = tile.getModel().getVertices()[pointer * 3 + 2] + (positions.get(p).z / 2.0f);
				float t0 = tile.getModel().getTextures()[pointer * 2];
				float t1 = tile.getModel().getTextures()[pointer * 2 + 1];
				float n0 = tile.getModel().getNormals()[pointer * 3];
				float n1 = tile.getModel().getNormals()[pointer * 3 + 1];
				float n2 = tile.getModel().getNormals()[pointer * 3 + 2];
				float ta0 = tile.getModel().getTangents()[pointer * 3];
				float ta1 = tile.getModel().getTangents()[pointer * 3 + 1];
				float ta2 = tile.getModel().getTangents()[pointer * 3 + 2];
				tileVertices.add(new TileVertex(i, v0, v1, v2, t0, t1, n0, n1, n2, ta0, ta1, ta2));
			}

			accumulator += tile.getModel().getIndices().length;
		}

		Iterator<TileVertex> it = tileVertices.iterator();

		while (it.hasNext()) {
			TileVertex vertex = it.next();
		}
	}

	public TileVertex getTileVertex(int index) {
		for (TileVertex tileVertex : tileVertices) {
			if (tileVertex.index == index) {
				return tileVertex;
			}
		}

		return null;
	}

	public Float[] getVertices() {
		Float[] result = new Float[tileVertices.size() * 3];

		for (int i = 0; i < tileVertices.size(); i++) {
			result[i * 3] = tileVertices.get(i).position.x;
			result[i * 3 + 1] = tileVertices.get(i).position.y;
			result[i * 3 + 2] = tileVertices.get(i).position.z;
		}

		return result;
	}

	public Float[] getTextures() {
		Float[] result = new Float[tileVertices.size() * 2];

		for (int i = 0; i < tileVertices.size(); i++) {
			result[i * 2] = tileVertices.get(i).textures.x;
			result[i * 2 + 1] = tileVertices.get(i).textures.y;
		}

		return result;
	}

	public Float[] getNormals() {
		Float[] result = new Float[tileVertices.size() * 3];

		for (int i = 0; i < tileVertices.size(); i++) {
			result[i * 3] = tileVertices.get(i).normals.x;
			result[i * 3 + 1] = tileVertices.get(i).normals.y;
			result[i * 3 + 2] = tileVertices.get(i).normals.z;
		}

		return result;
	}

	public Float[] getTangents() {
		Float[] result = new Float[tileVertices.size() * 3];

		for (int i = 0; i < tileVertices.size(); i++) {
			result[i * 3] = tileVertices.get(i).tangents.x;
			result[i * 3 + 1] = tileVertices.get(i).tangents.y;
			result[i * 3 + 2] = tileVertices.get(i).tangents.z;
		}

		return result;
	}

	public Integer[] getIndices() {
		Integer[] result = new Integer[tileVertices.size()];

		for (int i = 0; i < tileVertices.size(); i++) {
			result[i] = tileVertices.get(i).index;
		}

		return result;
	}

	public int getAccumulator() {
		return accumulator;
	}

	public static float[] mergeF(List<TilesMesh> tiles, Function<TilesMesh, Float[]> pass) {
		Float[][] input = new Float[tiles.size()][];
		int accumulator = 0;

		for (int i = 0; i < input.length; i++) {
			input[i] = pass.apply(tiles.get(i));
			accumulator += input[i].length;
		}

		float[] result = new float[accumulator];
		accumulator = 0;

		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				result[accumulator] = input[i][j];
				accumulator++;
			}
		}

		return result;
	}

	public static int[] mergeI(List<TilesMesh> tiles, Function<TilesMesh, Integer[]> pass) {
		Integer[][] input = new Integer[tiles.size()][];
		int accumulator = 0;

		for (int i = 0; i < tiles.size(); i++) {
			input[i] = pass.apply(tiles.get(i));
			accumulator += input[i].length;
		}

		int[] result = new int[accumulator];
		accumulator = 0;

		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				result[accumulator] = input[i][j];
				accumulator++;
			}
		}

		return result;
	}

	public class TileVertex implements Comparable<TileVertex> {
		protected final int index;
		protected final Vector3f position;
		protected final Vector2f textures;
		protected final Vector3f normals;
		protected final Vector3f tangents;

		TileVertex(int index, float vertex0, float vertex1, float vertex2, float texture0, float texture1,
		           float normal0, float normal1, float normal2, float tangent0, float tangent1, float tangent2) {
			this.index = index;
			this.position = new Vector3f(vertex0, vertex1, vertex2);
			this.textures = new Vector2f(texture0, texture1);
			this.normals = new Vector3f(normal0, normal1, normal2);
			this.tangents = new Vector3f(tangent0, tangent1, tangent2);
		}

		@Override
		public int compareTo(TileVertex tileVertex) {
			return ((Integer) index).compareTo(tileVertex.index);
		}
	}
}
