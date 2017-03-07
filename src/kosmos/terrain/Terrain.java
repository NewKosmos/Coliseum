package kosmos.terrain;

import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.chunks.*;
import kosmos.entities.components.*;
import kosmos.world.*;

/**
 * Represents a terrain object/
 */
public class Terrain extends Entity {
	public static final float TERRAIN_SIZE = 200.0f;
	public static final int TERRAIN_VERTEX_COUNT = 270;

	private float[][] heights;
	private ModelObject model;

	/**
	 * Creates a new game terrain that the player and engine.entities can interact on.
	 *
	 * @param gridX The position on the x grid.
	 * @param gridZ The position on the z grid.
	 */
	public Terrain(ISpatialStructure<Entity> structure, float gridX, float gridZ) {
		super(structure, new Vector3f((gridX * TERRAIN_SIZE) - (TERRAIN_SIZE / 2.0f), 0.0f, (gridZ * TERRAIN_SIZE) - (TERRAIN_SIZE / 2.0f)), new Vector3f());
		generateTerrain();
	}

	private void generateTerrain() {
		heights = new float[TERRAIN_VERTEX_COUNT][TERRAIN_VERTEX_COUNT];

		int count = TERRAIN_VERTEX_COUNT * TERRAIN_VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (TERRAIN_VERTEX_COUNT - 1) * (TERRAIN_VERTEX_COUNT - 1)];
		int vertexPointer = 0;

		for (int i = 0; i < TERRAIN_VERTEX_COUNT; i++) {
			for (int j = 0; j < TERRAIN_VERTEX_COUNT; j++) {
				vertices[vertexPointer * 3] = j / ((float) TERRAIN_VERTEX_COUNT - 1) * TERRAIN_SIZE;
				float height = getHeightFromMap(j, i);
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = i / ((float) TERRAIN_VERTEX_COUNT - 1) * TERRAIN_SIZE;
				Vector3f normal = calculateMapNormal(j, i);
				normals[vertexPointer * 3] = normal.getX();
				normals[vertexPointer * 3 + 1] = normal.getY();
				normals[vertexPointer * 3 + 2] = normal.getZ();
				textureCoords[vertexPointer * 2] = j / ((float) TERRAIN_VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = i / ((float) TERRAIN_VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}

		int pointer = 0;

		for (int gz = 0; gz < TERRAIN_VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < TERRAIN_VERTEX_COUNT - 1; gx++) {
				int topLeft = gz * TERRAIN_VERTEX_COUNT + gx;
				int topRight = topLeft + 1;
				int bottomLeft = (gz + 1) * TERRAIN_VERTEX_COUNT + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}

		model = ModelFactory.newBuilder().setManual(new ModelLoadManual("terrain" + getPosition().x + "p" + getPosition().z) {
			@Override
			public float[] getVertices() {
				return vertices;
			}

			@Override
			public float[] getTextureCoords() {
				return textureCoords;
			}

			@Override
			public float[] getNormals() {
				return normals;
			}

			@Override
			public float[] getTangents() {
				return null;
			}

			@Override
			public int[] getIndices() {
				return indices;
			}

			@Override
			public boolean isSmoothShading() {
				return false;
			}

			@Override
			public AABB getAABB() {
				return new AABB(new Vector3f(getPosition().getX(), getPosition().getY(), getPosition().getZ()), new Vector3f(getPosition().getX() + TERRAIN_SIZE, getPosition().getY() + TERRAIN_SIZE, getPosition().getZ() + TERRAIN_SIZE));
			}

			@Override
			public QuickHull getHull() {
				return null;
			}
		}).create();

		new ComponentModel(this, 1.0f, model, TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "entities", "bushBerry", "bush.png")).clampEdges().create(), 0);
		new ComponentSurface(this, 1.0f, 0.0f, false, false);
		new ComponentCollider(this);
		new ComponentCollision(this);
	}

	private float getHeightFromMap(int x, int z) {
		return (int) Math.abs(KosmosWorld.getNoise().noise2((x + getPosition().x) / 88.8f, (z + getPosition().z) / 88.8f) * 9.81f);
	}

	private Vector3f calculateMapNormal(int x, int y) {
		float heightL = getHeightFromMap(x - 1, y);
		float heightR = getHeightFromMap(x + 1, y);
		float heightD = getHeightFromMap(x, y - 1);
		float heightU = getHeightFromMap(x, y + 1);
		Vector3f normal = new Vector3f(heightL - heightR, 2.0f, heightD - heightU);
		normal.normalize();
		return normal;
	}

	/**
	 * Gets the height of the terrain from a world coordinate.
	 *
	 * @param worldX World coordinate in the X.
	 * @param worldZ World coordinate in the Z.
	 *
	 * @return Returns the height at that spot.
	 */
	public float getHeightWorld(float worldX, float worldZ) {
		float terrainX = worldX - super.getPosition().getX();
		float terrainZ = worldZ - super.getPosition().getZ();
		float gridSquareSize = TERRAIN_SIZE / (heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

		if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
			return 0;
		}

		float xCoord = terrainX % gridSquareSize / gridSquareSize;
		float zCoord = terrainZ % gridSquareSize / gridSquareSize;
		float result;

		if (xCoord <= 1 - zCoord) {
			result = Vector3f.baryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			result = Vector3f.baryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}

		return result;
	}

	/**
	 * @return Gets the actual model behind the terrain.
	 */
	public ModelObject getModel() {
		return model;
	}

	@Override
	public AABB getBounding() {
		return model != null ? model.getAABB() : null;
	}

	public void delete() {
		model.delete();
		model = null;
	}
}
