package coliseum.world;

import coliseum.world.terrain.*;
import flounder.entities.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.physics.bounding.*;

import java.util.*;

/**
 * A hexagonal chunk.
 * http://www.redblobgames.com/grids/hexagons/#range
 * http://stackoverflow.com/questions/2459402/hexagonal-grid-coordinates-to-pixel-coordinates
 */
public class Chunk {
	public static final float[][] GENERATE_DELTAS = new float[][]{{1.0f, 0.0f, -1.0f}, {0.0f, 1.0f, -1.0f}, {-1.0f, 1.0f, 0.0f}, {-1.0f, 0.0f, 1.0f}, {0.0f, -1.0f, 1.0f}, {1.0f, -1.0f, 0.0f}};

	public static final int HEXAGON_SIDE_COUNT = 6; // The number of sides for each figure (hexagon).
	public static final float HEXAGON_SIDE_LENGTH = 2.0f; //  Each tile can be broken into equilateral triangles with sides of length.

	public static final int CHUNK_RADIUS = 9; // The amount of tiles that make up the radius. 7-9 are the optimal chunk radius ranges.

	private Vector2f position;
	private List<Entity> tiles;
	private boolean tilesChanged;
	private AABB aabb;

	private float darkness;

	public Chunk(Vector2f position) {
		this.position = position;
		this.tiles = new ArrayList<>();
		this.tilesChanged = true;
		this.aabb = new AABB();

		this.darkness = 0.0f;

		generate();
		FlounderLogger.log("Chunk[ " + position.x + ", " + position.y + " ]: Size = " + tiles.size());
	}

	private void generate() {
		for (int i = 0; i < CHUNK_RADIUS; i++) {
			int shapesOnEdge = i;
			float r = 0;
			float g = -i;
			float b = i;
			generateTile(Vector2f.add(position, calculateXY(new Vector3f(r, g, b), HEXAGON_SIDE_LENGTH, null), null));

			for (int j = 0; j < HEXAGON_SIDE_COUNT; j++) {
				if (j == HEXAGON_SIDE_COUNT - 1) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + GENERATE_DELTAS[j][0];
					g = g + GENERATE_DELTAS[j][1];
					b = b + GENERATE_DELTAS[j][2];
					generateTile(Vector2f.add(position, calculateXY(new Vector3f(r, g, b), HEXAGON_SIDE_LENGTH, null), null));
				}
			}
		}
	}

	private void generateTile(Vector2f position) {
		//if (tiles.size() == 0) {
		//	tiles.add(new TerrainStone(FlounderEntities.getEntities(), new Vector3f(position.x, 0.0f, position.y), new Vector3f(), this));
		//} else {
		//	tiles.add(new TerrainWater(FlounderEntities.getEntities(), new Vector3f(position.x, 0.0f, position.y), new Vector3f(), this));
		//}

		float chance = Maths.randomInRange(0.0f, 4.0f);
		float height = Math.random() > 0.75 ? (2.0f * (float) Math.sqrt(2.0f)) : 0.0f;

		if (chance > 3.0f) {
			tiles.add(new TerrainWater(FlounderEntities.getEntities(), new Vector3f(position.x, 0.0f, position.y), new Vector3f(), this));
		} else {
			if (height > 0.0f) {
				tiles.add(new TerrainStone(FlounderEntities.getEntities(), new Vector3f(position.x, 0.0f, position.y), new Vector3f(), this));
			}

			if (chance > 2.0f) {
				tiles.add(new TerrainSand(FlounderEntities.getEntities(), new Vector3f(position.x, height, position.y), new Vector3f(), this));
			} else if (chance > 1.0f) {
				tiles.add(new TerrainStone(FlounderEntities.getEntities(), new Vector3f(position.x, height, position.y), new Vector3f(), this));
			} else if (chance > 0.0f) {
				tiles.add(new TerrainGrass(FlounderEntities.getEntities(), new Vector3f(position.x, height, position.y), new Vector3f(), this));
			}
		}
	}

	public void update(Vector3f playerPosition) {
		if (tilesChanged) {
			recalculate();
			tilesChanged = false;
		}

		if (playerPosition != null) {
			//	double distance = Math.sqrt(Math.pow(position.x - playerPosition.x, 2.0) + Math.pow(position.y - playerPosition.y, 2.0));
			//	if (distance >= 30.0) {
			//		darkness = 0.7f;
			//	} else {
			//		darkness = 0.0f;
			//	}
		}

		FlounderBounding.addShapeRender(aabb);
	}

	public void recalculate() {
		for (Entity tile : tiles) {
			tile.update();
			AABB aabb = (AABB) tile.getBounding();

			if (aabb.getMinExtents().x < this.aabb.getMinExtents().x) {
				this.aabb.getMinExtents().x = aabb.getMinExtents().x;
			} else if (aabb.getMaxExtents().x > this.aabb.getMaxExtents().x) {
				this.aabb.getMaxExtents().x = aabb.getMaxExtents().x;
			}

			if (aabb.getMinExtents().y < this.aabb.getMinExtents().y) {
				this.aabb.getMinExtents().y = aabb.getMinExtents().y;
			} else if (aabb.getMaxExtents().y > this.aabb.getMaxExtents().y) {
				this.aabb.getMaxExtents().y = aabb.getMaxExtents().y;
			}

			if (aabb.getMinExtents().z < this.aabb.getMinExtents().z) {
				this.aabb.getMinExtents().z = aabb.getMinExtents().z;
			} else if (aabb.getMaxExtents().z > this.aabb.getMaxExtents().z) {
				this.aabb.getMaxExtents().z = aabb.getMaxExtents().z;
			}
		}
	}

	public static Vector3f calculateRGB(Vector2f position, float length, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		destination.x = (((float) Math.sqrt(3.0f) / 3.0f) * position.x - (position.y / 3.0f)) / length;
		destination.y = -(((float) Math.sqrt(3.0f) / 3.0f) * position.x + (position.y / 3.0f)) / length;
		destination.z = (2.0f / 3.0f) * position.y / length;
		return destination;
	}

	public static Vector2f calculateXY(Vector3f position, float length, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		destination.x = (float) Math.sqrt(3.0f) * length * ((position.z / 2.0f) + position.x);
		destination.y = (3.0f / 2.0f) * length * position.z;
		return destination;
	}

	public List<Entity> getTiles() {
		return tiles;
	}

	public void addTile(Entity tile) {
		tiles.add(tile);
		tilesChanged = true;
	}

	public float getDarkness() {
		return darkness;
	}
}
