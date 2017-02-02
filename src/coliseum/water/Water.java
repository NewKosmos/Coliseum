package coliseum.water;

import flounder.loaders.*;
import flounder.processing.*;
import flounder.processing.opengl.*;

/**
 * Represents the physical mesh for all the water at a certain height in the scene.
 */
public class Water {
	public static final float WAVE_SPEED = 48.0f;
	public static final float WAVELENGTH = 12.0f;
	public static final float AMPLITUDE = 1.20f;

	public static final float SQUARE_SIZE = 4.0f;
	public static final float HEIGHT = -1.0f;

	public static final int VERTEX_COUNT = 41;

	private int vao;
	private int vertexCount;
	private float height;
	private boolean loaded;

	/**
	 * Generates a new water mesh.
	 *
	 * @param height The height of the water plane.
	 */
	public Water(float height) {
		this.height = height;
		this.loaded = false;
		generateMesh();
	}

	/**
	 * Generates the water mesh and loads it to a VAO.
	 */
	private void generateMesh() {
		final float[] vertices = WaterMeshGenerator.generateVertices();
		vertexCount = vertices.length / 3;
		FlounderProcessors.sendRequest(new RequestOpenGL() {
			@Override
			public void executeRequestGL() {
				vao = FlounderLoader.createInterleavedVAO(vertices, 3);
				loaded = true;
			}
		});
	}

	/**
	 * @return The average height of the water plane.
	 */
	public float getHeight() {
		return height;
	}

	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * @return The VAO's ID.
	 */
	protected int getVao() {
		return vao;
	}

	/**
	 * @return The number of vertices stored in the VAO.
	 */
	protected int getVertexCount() {
		return vertexCount;
	}

	public void delete() {
		FlounderProcessors.sendRequest(new RequestOpenGL() {
			@Override
			public void executeRequestGL() {
				loaded = false;
				FlounderLoader.deleteVAOFromCache(vao);
			}
		});
	}
}
