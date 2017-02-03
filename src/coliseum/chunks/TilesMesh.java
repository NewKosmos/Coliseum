package coliseum.chunks;

import flounder.maths.vectors.*;

import java.util.*;
import java.util.function.*;

public class TilesMesh {
	private Tile tile;
	private List<Vector3f> positions;

	private int tileModelMaxIndex;
	private int accumulator;
	private List<TileVertex> tileVertices;

	List<Float> verticesList = new ArrayList<>();
	List<Float> textureCoordsList = new ArrayList<>();
	List<Float> normalsList = new ArrayList<>();
	List<Float> tangentsList = new ArrayList<>();
	List<Integer> indicesList = new ArrayList<>();

	public TilesMesh(Tile tile, List<Vector3f> positions, int perviousAccumulator) {
		this.tile = tile;
		this.positions = positions;

		this.tileVertices = new ArrayList<>();

		// Finds the largest indices size in the tile model.
		for (int i = 0; i < tile.getModel().getMeshData().getIndices().length; i++) {
			tileModelMaxIndex = (tileModelMaxIndex < tile.getModel().getMeshData().getIndices()[i]) ? tile.getModel().getMeshData().getIndices()[i] : tileModelMaxIndex;
		}

		this.accumulator = 0;

		//
		float[] vertices = tile.getModel().getMeshData().getVertices();
		float[] textures = tile.getModel().getMeshData().getTextures();
		float[] normals = tile.getModel().getMeshData().getNormals();
		float[] tangents = tile.getModel().getMeshData().getTangents();
		int[] indices = tile.getModel().getMeshData().getIndices();

		for (int p = 0; p < positions.size(); p++) {
			int currentPosID = 0;

			for (int i = 0; i < vertices.length; i++) {
				float vertexOffset = (currentPosID == 0) ? positions.get(p).x : (currentPosID == 1) ? positions.get(p).y : positions.get(p).z;
				currentPosID = (currentPosID == 2) ? 0 : currentPosID + 1;
				verticesList.add(vertices[i] + (vertexOffset / 2.0f));
			}

			for (int i = 0; i < textures.length; i++) {
				textureCoordsList.add(textures[i]);
			}

			for (int i = 0; i < normals.length; i++) {
				normalsList.add(normals[i]);
			}

			for (int i = 0; i < tangents.length; i++) {
				tangentsList.add(tangents[i]);
			}

			for (int i = 0; i < indices.length; i++) {
				indicesList.add(indices[i] + perviousAccumulator + accumulator);
			}

			accumulator += tileModelMaxIndex + 1;
		}

		// Changed from array form into array tile vertex.
		/*for (int i = 0; i < positions.size(); i++) {
			for (int index : indices) {
				float vertex0 = vertices[index * 3] + positions.get(i).x;
				float vertex1 = vertices[index * 3 + 1] + positions.get(i).y;
				float vertex2 = vertices[index * 3 + 2] + positions.get(i).z;

				float texture0 = textures[index * 2];
				float texture1 = textures[index * 2 + 1];

				float normal0 = normals[index * 3];
				float normal1 = normals[index * 3 + 1];
				float normal2 = normals[index * 3 + 2];

				float tangent0 = tangents[index * 3];
				float tangent1 = tangents[index * 3 + 1];
				float tangent2 = tangents[index * 3 + 2];

				tileVertices.add(new TileVertex(
						index + ((tileModelMaxIndex + 1) * i),
						vertex0, vertex1, vertex2, texture0, texture1, normal0, normal1, normal2, tangent0, tangent1, tangent2
				));
			}
		}*/

		// Sorts the vertices.
		//	this.tileVertices = ArraySorting.quickSort(tileVertices);
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
	/*	Float[] result = new Float[tileVertices.size() * 3];

		for (int i = 0; i < tileVertices.size(); i++) {
			int index = tileVertices.get(i).index;
			TileVertex vertex = getTileVertex(index);
			result[i * 3] = vertex.position.x;
			result[i * 3 + 1] = vertex.position.y;
			result[i * 3 + 2] = vertex.position.z;
		}

		return result;*/
		Float[] result = new Float[verticesList.size()];
		for (int i = 0; i < verticesList.size(); i++) {
			result[i] = verticesList.get(i);
		}
		return result;
		//	Float[] result = new Float[tile.getModel().getMeshData().getVertices().length];
		//	for (int i = 0; i < result.length; i++) {
		//		result[i] = tile.getModel().getMeshData().getVertices()[i];
		//	}
		//	return result;
	}

	public Float[] getTextures() {
	/*	Float[] result = new Float[tileVertices.size() * 2];

		for (int i = 0; i < tileVertices.size(); i++) {
			int index = tileVertices.get(i).index;
			TileVertex vertex = getTileVertex(index);
			result[i * 2] = vertex.textures.x;
			result[i * 2 + 1] = vertex.textures.y;
		}

		return result;*/
		Float[] result = new Float[textureCoordsList.size()];
		for (int i = 0; i < textureCoordsList.size(); i++) {
			result[i] = textureCoordsList.get(i);
		}
		return result;
		//	Float[] result = new Float[tile.getModel().getMeshData().getTextures().length];
		//	for (int i = 0; i < result.length; i++) {
		//		result[i] = tile.getModel().getMeshData().getTextures()[i];
		//	}
		//	return result;
	}

	public Float[] getNormals() {
	/*	Float[] result = new Float[tileVertices.size() * 3];

		for (int i = 0; i < tileVertices.size(); i++) {
			int index = tileVertices.get(i).index;
			TileVertex vertex = getTileVertex(index);
			result[i * 3] = vertex.normals.x;
			result[i * 3 + 1] = vertex.normals.y;
			result[i * 3 + 2] = vertex.normals.z;
		}

		return result;*/
		Float[] result = new Float[normalsList.size()];
		for (int i = 0; i < normalsList.size(); i++) {
			result[i] = normalsList.get(i);
		}
		return result;
		//	Float[] result = new Float[tile.getModel().getMeshData().getNormals().length];
		//	for (int i = 0; i < result.length; i++) {
		//		result[i] = tile.getModel().getMeshData().getNormals()[i];
		//	}
		//	return result;
	}

	public Float[] getTangents() {
	/*	Float[] result = new Float[tileVertices.size() * 3];

		for (int i = 0; i < tileVertices.size(); i++) {
			int index = tileVertices.get(i).index;
			TileVertex vertex = getTileVertex(index);
			result[i * 3] = vertex.tangents.x;
			result[i * 3 + 1] = vertex.tangents.y;
			result[i * 3 + 2] = vertex.tangents.z;
		}

		return result;*/
		Float[] result = new Float[tangentsList.size()];
		for (int i = 0; i < tangentsList.size(); i++) {
			result[i] = tangentsList.get(i);
		}
		return result;
		//	Float[] result = new Float[tile.getModel().getMeshData().getTangents().length];
		//	for (int i = 0; i < result.length; i++) {
		//		result[i] = tile.getModel().getMeshData().getTangents()[i];
		//	}
		//	return result;
	}

	public Integer[] getIndices() {
	/*	Integer[] result = new Integer[tileVertices.size()];

		for (int i = 0; i < tileVertices.size(); i++) {
			result[i] = tileVertices.get(i).index;
		}

		return result;*/
		Integer[] result = new Integer[indicesList.size()];
		for (int i = 0; i < indicesList.size(); i++) {
			result[i] = indicesList.get(i);
		}
		return result;
		//	Integer[] result = new Integer[tile.getModel().getMeshData().getIndices().length];
		//	for (int i = 0; i < result.length; i++) {
		//		result[i] = tile.getModel().getMeshData().getIndices()[i];
		//	}
		//	return result;
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
