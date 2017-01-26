package coliseum.world;

import flounder.maths.vectors.*;

public class Tile {
	public static final int SIDE_COUNT = 6; // The number of sides for each figure (hexagon).
	public static final int SIDE_LENGTH = 1; //  Each tile can be broken into equilateral triangles with sides of length.

	private Chunk parent;
	private Vector2f position;

	public Tile(Chunk parent, Vector2f position) {
		this.parent = parent;
		this.position = position;
	}

	public Chunk getParent() {
		return parent;
	}

	public Vector2f getPosition() {
		return position;
	}
}
