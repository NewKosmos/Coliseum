package coliseum.water;

import flounder.loaders.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.processing.*;
import flounder.processing.opengl.*;

/**
 * Represents the physical mesh for all the water at a certain height in the scene.
 */
public class Water {
	protected static final float WAVE_SPEED = 12.0f;
	protected static final float WAVE_LENGTH = 5.0f;
	protected static final float AMPLITUDE = 0.50f;

	protected static final float SQUARE_SIZE = 2.0f;
	protected static final int VERTEX_COUNT = 31;

	private int vao;
	private int vertexCount;
	private boolean loaded;

	private Colour colour;

	private Vector3f position;
	private Vector3f rotation;
	private float scale;
	private Matrix4f modelMatrix;

	/**
	 * Generates a new water mesh.
	 *
	 * @param position The position of the water plane.
	 * @param rotation The rotation of the water plane.
	 * @param scale The scale of the water plane.
	 */
	public Water(Vector3f position, Vector3f rotation, float scale) {
		this.vao = 0;
		this.vertexCount = 0;
		this.loaded = false;

		this.colour = new Colour(61, 174, 255, true);

		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.modelMatrix = new Matrix4f();

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

	public boolean isLoaded() {
		return loaded;
	}

	public Colour getColour() {
		return colour;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public Matrix4f getModelMatrix() {
		modelMatrix.setIdentity();
		Matrix4f.transformationMatrix(position, rotation, scale, modelMatrix);
		return modelMatrix;
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
