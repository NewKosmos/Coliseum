package kosmos.terrain;

import flounder.entities.*;
import flounder.framework.*;
import flounder.physics.bounding.*;

public class KosmosTerrain extends Module {
	private static final KosmosTerrain INSTANCE = new KosmosTerrain();
	public static final String PROFILE_TAB_NAME = "Kosmos Terrain";

	private Terrain terrain;

	public KosmosTerrain() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderEntities.class);
	}

	@Override
	public void init() {
		this.terrain = new Terrain(FlounderEntities.getEntities(), 0.0f, 0.0f);
	}

	@Override
	public void update() {
		terrain.update();
		FlounderBounding.addShapeRender(terrain.getBounding());
	}

	@Override
	public void profile() {
	}

	public static Terrain getTerrain() {
		return INSTANCE.terrain;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		terrain.delete();
	}
}
