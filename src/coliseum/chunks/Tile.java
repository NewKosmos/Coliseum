package coliseum.chunks;

import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.textures.*;

public class Tile implements Comparable<Tile> {
	private Vector3f position;
	private Model model;
	private Texture texture;

	protected Tile(Vector3f position, Model model, Texture texture) {
		this.position = position;
		this.model = model;
		this.texture = texture;

		//	Entity e = new Entity(FlounderEntities.getEntities(), position, new Vector3f());
		//	new ComponentModel(e, model, 2.0f, texture, 0);
		//	new ComponentCollider(e);
		//	new ComponentCollision(e);
	}

	public Vector3f getPosition() {
		return position;
	}

	public Model getModel() {
		return model;
	}

	public Texture getTexture() {
		return texture;
	}

	@Override
	public int compareTo(Tile o) {
		return ((Float) position.lengthSquared()).compareTo(o.position.lengthSquared());
	}
}
