package coliseum.world;

import flounder.maths.vectors.*;

public class Tile {
	public static final float SIDE_LENGTH = 2.0f; //  Each tile can be broken into equilateral triangles with sides of length. (0.015f)

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

	public Vector3f getRGB(Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		destination.x = (((float) Math.sqrt(3.0f) / 3.0f) * position.x - (position.y / 3.0f)) / SIDE_LENGTH;
		destination.y = -(((float) Math.sqrt(3.0f) / 3.0f) * position.x + (position.y / 3.0f)) / SIDE_LENGTH;
		destination.z = (2.0f / 3.0f) * position.y / SIDE_LENGTH;

		return destination;
	}
}
