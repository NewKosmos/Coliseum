package coliseum.world;

import coliseum.chunks.*;
import flounder.entities.*;
import flounder.framework.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.physics.bounding.*;
import flounder.textures.*;

public class ColiseumWorld extends IModule {
	private static final ColiseumWorld INSTANCE = new ColiseumWorld();
	public static final String PROFILE_TAB_NAME = "Coliseum World";

	private Fog fog;
	private SkyCycle skyCycle;
	private ChunksManager chunksManager;

	private boolean worldGenerated;

	public ColiseumWorld() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class, FlounderEntities.class);
		this.worldGenerated = false;
	}

	@Override
	public void init() {
		this.fog = new Fog(new Colour(), 0.003f, 2.0f, 0.0f, 50.0f);
		this.skyCycle = new SkyCycle();
	}

	@Override
	public void update() {
		if (!worldGenerated) {
			this.chunksManager = new ChunksManager();
			worldGenerated = true;
		}

		skyCycle.update();
		fog.setFogColour(skyCycle.getSkyColour());

		if (chunksManager != null) {
			chunksManager.update();
		}
	}

	@Override
	public void profile() {
	}

	public static Fog getFog() {
		return INSTANCE.fog;
	}

	public static SkyCycle getSkyCycle() {
		return INSTANCE.skyCycle;
	}

	@Override
	public IModule getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		worldGenerated = false;

		if (chunksManager != null) {
			chunksManager.dispose();
			chunksManager = null;
		}
	}
}
