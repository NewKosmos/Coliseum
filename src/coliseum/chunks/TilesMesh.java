package coliseum.chunks;

import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.vectors.*;

import java.util.*;
import java.util.function.*;

public class TilesMesh {
	private Tile tile;
	private List<Vector3f> positions;

	private int tileModelMaxIndex;
	private List<TileVertex> tileVertices;

	public TilesMesh(Tile tile, List<Vector3f> positions) {
		this.tile = tile;
		this.positions = positions;

		this.tileVertices = new ArrayList<>();

		// Finds the largest indices size in the tile model.
		for (int i = 0; i < tile.getModel().getMeshData().getIndices().length; i++) {
			tileModelMaxIndex = (tileModelMaxIndex < tile.getModel().getMeshData().getIndices()[i]) ? tile.getModel().getMeshData().getIndices()[i] : tileModelMaxIndex;
		}

		FlounderLogger.log("Max Index: " + tileModelMaxIndex);

		// Changed from array form into array tile vertex.
		for (int i = 0; i < positions.size(); i++) {
			for (int j = 0; j < tile.getModel().getMeshData().getIndices().length; j++) {
				int index = tile.getModel().getMeshData().getIndices()[j];

				float vertex0 = tile.getModel().getMeshData().getVertices()[index * 3] + positions.get(i).x;
				float vertex1 = tile.getModel().getMeshData().getVertices()[index * 3 + 1] + positions.get(i).y;
				float vertex2 = tile.getModel().getMeshData().getVertices()[index * 3 + 2] + positions.get(i).z;

				float texture0 = tile.getModel().getMeshData().getTextures()[index * 2];
				float texture1 = tile.getModel().getMeshData().getTextures()[index * 2 + 1];

				float normal0 = tile.getModel().getMeshData().getNormals()[index * 3];
				float normal1 = tile.getModel().getMeshData().getNormals()[index * 3 + 1];
				float normal2 = tile.getModel().getMeshData().getNormals()[index * 3 + 2];

				float tangent0 = tile.getModel().getMeshData().getTangents()[index * 3];
				float tangent1 = tile.getModel().getMeshData().getTangents()[index * 3 + 1];
				float tangent2 = tile.getModel().getMeshData().getTangents()[index * 3 + 2];

				tileVertices.add(new TileVertex(
						index + ((tileModelMaxIndex + 1) * j),
						vertex0, vertex1, vertex2, texture0, texture1, normal0, normal1, normal2, tangent0, tangent1, tangent2
				));
			}
		}

		// Sorts the vertices.
		// this.tileVertices = ArraySorting.quickSort(tileVertices);
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

		TileVertex (int index, float vertex0, float vertex1, float vertex2, float texture0, float texture1,
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
