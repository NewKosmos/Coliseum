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
	private int tileModelMaxIndex;
	private int accumulator;
	private List<TileVertex> tileVertices;

	private List<Float> verticesList = new ArrayList<>();
	private List<Float> texturesList = new ArrayList<>();
	private List<Float> normalsList = new ArrayList<>();
	private List<Float> tangentsList = new ArrayList<>();
	private List<Integer> indicesList = new ArrayList<>();

	public TilesMesh(Tile tile, List<Vector3f> positions, int previousAccumulator) {
		this.tileVertices = new ArrayList<>();

		// Finds the largest indices size in the tile model.
		for (int i = 0; i < tile.getModel().getIndices().length; i++) {
			tileModelMaxIndex = (tileModelMaxIndex < tile.getModel().getIndices()[i]) ? tile.getModel().getIndices()[i] : tileModelMaxIndex;
		}

		this.accumulator = 0;

		//
		float[] vertices = tile.getModel().getVertices();
		float[] textures = tile.getModel().getTextures();
		float[] normals = tile.getModel().getNormals();
		float[] tangents = tile.getModel().getTangents();
		int[] indices = tile.getModel().getIndices();

		for (int p = 0; p < positions.size(); p++) {
			int currentPosID = 0;

			for (int i = 0; i < vertices.length; i++) {
				float vertexOffset = (currentPosID == 0) ? positions.get(p).x : (currentPosID == 1) ? positions.get(p).y : positions.get(p).z;
				currentPosID = (currentPosID == 2) ? 0 : currentPosID + 1;
				verticesList.add(vertices[i] + (vertexOffset / 2.0f));
			}

			for (int i = 0; i < textures.length; i++) {
				texturesList.add(textures[i]);
			}

			for (int i = 0; i < normals.length; i++) {
				normalsList.add(normals[i]);
			}

			for (int i = 0; i < tangents.length; i++) {
				tangentsList.add(tangents[i]);
			}

			for (int i = 0; i < indices.length; i++) {
				indicesList.add(indices[i] + previousAccumulator + accumulator);
			}

			accumulator += tileModelMaxIndex + 1;
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
		Float[] result = new Float[verticesList.size()];

		for (int i = 0; i < verticesList.size(); i++) {
			result[i] = verticesList.get(i);
		}

		return result;
	}

	public Float[] getTextures() {
		Float[] result = new Float[texturesList.size()];

		for (int i = 0; i < texturesList.size(); i++) {
			result[i] = texturesList.get(i);
		}

		return result;
	}

	public Float[] getNormals() {
		Float[] result = new Float[normalsList.size()];

		for (int i = 0; i < normalsList.size(); i++) {
			result[i] = normalsList.get(i);
		}

		return result;
	}

	public Float[] getTangents() {
		Float[] result = new Float[tangentsList.size()];

		for (int i = 0; i < tangentsList.size(); i++) {
			result[i] = tangentsList.get(i);
		}

		return result;
	}

	public Integer[] getIndices() {
		Integer[] result = new Integer[indicesList.size()];

		for (int i = 0; i < indicesList.size(); i++) {
			result[i] = indicesList.get(i);
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
