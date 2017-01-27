package coliseum.entities.components;

import coliseum.world.*;
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.entities.template.*;
import flounder.physics.*;

public class ComponentTerrain extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private Tile tile;

	public ComponentTerrain(Entity entity, Tile tile) {
		super(entity, ID);
		this.tile = tile;
	}

	public ComponentTerrain(Entity entity, EntityTemplate template) {
		super(entity, ID);
		this.tile = null;
	}

	@Override
	public void update() {

	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {

	}
}
