package coliseum.world;

import flounder.maths.vectors.*;

import java.util.*;

public class Chunk {
	private static final float[][] GENERATE_DELTAS = new float[][]{{1.0f, 0.0f, -1.0f}, {0.0f, 1.0f, -1.0f}, {-1.0f, 1.0f, 0.0f}, {-1.0f, 0.0f, 1.0f}, {0.0f, -1.0f, 1.0f}, {1.0f, -1.0f, 0.0f}};

	public static final int CHUNK_RADIUS = 4; // The amount of tiles that make up the radius.

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

			generateTile(r, g, b);

			for (int j = 0; j < Tile.SIDE_COUNT; j++) {
				if (j == 5) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + GENERATE_DELTAS[j][0];
					g = g + GENERATE_DELTAS[j][1];
					b = b + GENERATE_DELTAS[j][2];
					generateTile(r, g, b);
				}
			}
		}
	}

	/**
	 * The "hexagonal" coordinates are represented as" (r,g,b)
	 * @param r
	 * @param g
	 * @param b
	 */
	private void generateTile(float r, float g, float b) {
		// http://stackoverflow.com/questions/2459402/hexagonal-grid-coordinates-to-pixel-coordinates
		float y = (3.0f / 2.0f) * Tile.SIDE_LENGTH * b;
		float x = (float) Math.sqrt(3.0f) * Tile.SIDE_LENGTH * ((b / 2.0f) + r);
		Tile t = new Tile(this, Vector2f.add(position, new Vector2f(x, y), null));
		System.out.println("[" + r + ", " + g + ", " + b + "]  |  " + x + ", " + y);
		tiles.add(t);
	}

	public List<Tile> getTiles() {
		return tiles;
	}
}
