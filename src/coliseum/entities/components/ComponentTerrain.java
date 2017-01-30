package coliseum.entities.components;

import coliseum.world.*;
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.entities.template.*;
import flounder.physics.*;

public class ComponentTerrain extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private Chunk chunk;
	private float height;

	public ComponentTerrain(Entity entity, Chunk chunk, float height) {
		super(entity, ID);
		this.chunk = chunk;
		this.height = height;
	}

	public ComponentTerrain(Entity entity, EntityTemplate template) {
		super(entity, ID);
		this.chunk = null;
		this.height = (float) Math.sqrt(2.0f) * 2.0f;
	}

	@Override
	public void update() {
	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	public float getHeight() {
		return height;
	}

	public float getDarkness() {
		return chunk.getDarkness();
	}

	@Override
	public void dispose() {
	}
}
