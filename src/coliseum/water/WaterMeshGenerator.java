package coliseum.water;

import flounder.maths.vectors.*;

import java.util.*;

public class WaterMeshGenerator {

	/**
	 * Generates the {@code float[]} of vertex data which will be loaded to the water's VAO.
	 * The array contains the vertex x and z positions as well as an encoded indication of which corner of its triangle each vertex lies in.
	 *
	 * @return The {@code float[]} of vertex data to be loaded to a VAO.
	 */
	protected static float[] generateVertices() {
		List<Float> vertices = new ArrayList<>();

		for (int col = 0; col < Water.VERTEX_COUNT - 1; col++) {
			for (int row = 0; row < Water.VERTEX_COUNT - 1; row++) {
				int topLeft = (row * Water.VERTEX_COUNT) + col;
				int topRight = topLeft + 1;
				int bottomLeft = ((row + 1) * Water.VERTEX_COUNT) + col;
				int bottomRight = bottomLeft + 1;

				if (row % 2 == 0) {
					storeQuad1(vertices, topLeft, topRight, bottomLeft, bottomRight, col % 2 == 0);
				} else {
					storeQuad2(vertices, topLeft, topRight, bottomLeft, bottomRight, col % 2 == 0);
				}
			}
		}

		return floatListToArray(vertices);
	}

	private static void storeQuad1(List<Float> vertices, int topLeft, int topRight, int bottomLeft, int bottomRight, boolean mixed) {
		storeVertex(vertices, topLeft, new Vector2f(0.0f, 1.0f), mixed ? new Vector2f(1.0f, 0.0f) : new Vector2f(1.0f, 1.0f));
		storeVertex(vertices, bottomLeft, mixed ? new Vector2f(1.0f, -1.0f) : new Vector2f(1.0f, 0.0f), new Vector2f(0.0f, -1.0f));

		if (mixed) {
			storeVertex(vertices, topRight, new Vector2f(-1.0f, 0.0f), new Vector2f(-1.0f, 1.0f));
		} else {
			storeVertex(vertices, bottomRight, new Vector2f(-1.0f, -1.0f), new Vector2f(-1.0f, 0.0f));
		}

		storeVertex(vertices, bottomRight, new Vector2f(0.0f, -1.0f), mixed ? new Vector2f(-1.0f, 0.0f) : new Vector2f(-1.0f, -1.0f));
		storeVertex(vertices, topRight, mixed ? new Vector2f(-1.0f, 1.0f) : new Vector2f(-1.0f, 0.0f), new Vector2f(0.0f, 1.0f));

		if (mixed) {
			storeVertex(vertices, bottomLeft, new Vector2f(1.0f, 0.0f), new Vector2f(1.0f, -1.0f));
		} else {
			storeVertex(vertices, topLeft, new Vector2f(1.0f, 1.0f), new Vector2f(1.0f, 0.0f));
		}
	}

	private static void storeQuad2(List<Float> vertices, int topLeft, int topRight, int bottomLeft, int bottomRight, boolean mixed) {
		storeVertex(vertices, topRight, new Vector2f(-1.0f, 0.0f), mixed ? new Vector2f(0.0f, 1.0f) : new Vector2f(-1.0f, 1.0f));
		storeVertex(vertices, topLeft, mixed ? new Vector2f(1.0f, 1.0f) : new Vector2f(0.0f, 1.0f), new Vector2f(1.0f, 0.0f));

		if (mixed) {
			storeVertex(vertices, bottomRight, new Vector2f(0.0f, -1.0f), new Vector2f(-1.0f, -1.0f));
		} else {
			storeVertex(vertices, bottomLeft, new Vector2f(1.0f, -1.0f), new Vector2f(0.0f, -1.0f));
		}

		storeVertex(vertices, bottomLeft, new Vector2f(1.0f, 0.0f), mixed ? new Vector2f(0.0f, -1.0f) : new Vector2f(1.0f, -1.0f));
		storeVertex(vertices, bottomRight, mixed ? new Vector2f(-1.0f, -1.0f) : new Vector2f(0.0f, -1.0f), new Vector2f(-1.0f, 0.0f));

		if (mixed) {
			storeVertex(vertices, topLeft, new Vector2f(0.0f, 1.0f), new Vector2f(1.0f, 1.0f));
		} else {
			storeVertex(vertices, topRight, new Vector2f(-1.0f, 1.0f), new Vector2f(0.0f, 1.0f));
		}
	}

	private static void storeVertex(List<Float> vertices, int index, Vector2f otherPoint1, Vector2f otherPoint2) {
		int gridX = index % Water.VERTEX_COUNT;
		int gridZ = index / Water.VERTEX_COUNT;
		float x = gridX * Water.SQUARE_SIZE;
		float z = gridZ * Water.SQUARE_SIZE;
		vertices.add(x);
		vertices.add(z);
		vertices.add(encode(otherPoint1.x, otherPoint1.y, otherPoint2.x, otherPoint2.y));
	}

	/**
	 * Encodes the position of 2 vertices in a triangle (relative to the other vertex) into a single float.
	 *
	 * @param x Relative x position of first other vertex.
	 * @param z Relative z position of first other vertex.
	 * @param x2 Relative x position of second other vertex.
	 * @param z2 Relative z position of second other vertex.
	 *
	 * @return The encoded float.
	 */
	private static float encode(float x, float z, float x2, float z2) {
		float p3 = (x + 1.0f) * 27.0f;
		float p2 = (z + 1.0f) * 9.0f;
		float p1 = (x2 + 1.0f) * 3.0f;
		float p0 = (z2 + 1.0f) * 1.0f;
		return p0 + p1 + p2 + p3;
	}

	private static float[] floatListToArray(List<Float> floatList) {
		float[] array = new float[floatList.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = floatList.get(i);
		}

		return array;
	}
}
