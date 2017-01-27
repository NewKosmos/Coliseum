package coliseum.world;

import flounder.maths.vectors.*;

import java.util.*;

/**
 * A hexagonal chunk based off of the article: http://stackoverflow.com/questions/2459402/hexagonal-grid-coordinates-to-pixel-coordinates
 */
public class Chunk {
	public static final float[][] GENERATE_DELTAS = new float[][]{{1.0f, 0.0f, -1.0f}, {0.0f, 1.0f, -1.0f}, {-1.0f, 1.0f, 0.0f}, {-1.0f, 0.0f, 1.0f}, {0.0f, -1.0f, 1.0f}, {1.0f, -1.0f, 0.0f}};

	public static final int CHUNK_RADIUS = 6; // The amount of tiles that make up the radius.

	private Vector2f position;
	private List<Tile> tiles;

	public Chunk(Vector2f position) {
		this.position = position;
		this.tiles = new ArrayList<>();
		generate();
	}

	private void generate() {
		for (int i = 0; i < CHUNK_RADIUS; i++) {
			int shapesOnEdge = i;
			float r = 0;
			float g = -i;
			float b = i;
			tiles.add(new Tile(this, Vector2f.add(position, calculateXY(new Vector3f(r, g, b), Tile.SIDE_LENGTH, null), null)));

			for (int j = 0; j < Tile.SIDE_COUNT; j++) {
				if (j == Tile.SIDE_COUNT - 1) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + GENERATE_DELTAS[j][0];
					g = g + GENERATE_DELTAS[j][1];
					b = b + GENERATE_DELTAS[j][2];
					tiles.add(new Tile(this, Vector2f.add(position, calculateXY(new Vector3f(r, g, b), Tile.SIDE_LENGTH, null), null)));
				}
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

	public List<Tile> getTiles() {
		return tiles;
	}
}
