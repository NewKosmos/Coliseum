package kosmos.water;

import flounder.framework.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import kosmos.*;

public class KosmosWater extends Module {
	private static final KosmosWater INSTANCE = new KosmosWater();
	public static final String PROFILE_TAB_NAME = "Kosmos Water";

	private Water water;
	private float waveTime;

	private boolean enableReflections;
	private float reflectionQuality;

	public KosmosWater() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class);
	}

	@Override
	public void init() {
		this.water = new Water(new Vector3f(0.0f, -0.1f, 0.0f), new Vector3f(), 1.0f);
		this.waveTime = 0.0f;

		this.enableReflections = KosmosConfigs.WATER_REFLECTION_ENABLED.getBoolean();
		this.reflectionQuality = KosmosConfigs.WATER_REFLECTION_QUALITY.getFloat();
	}

	@Override
	public void update() {
		water.update();
		waveTime += Framework.getDelta();
		waveTime %= Water.WAVE_SPEED;
		FlounderBounding.addShapeRender(water.getAABB());
	}

	@Override
	public void profile() {
	}

	public static Water getWater() {
		return INSTANCE.water;
	}

	public static float getWaveTime() {
		return INSTANCE.waveTime;
	}

	public static boolean reflectionsEnabled() {
		return INSTANCE.enableReflections && INSTANCE.water.getColour().a != 1.0f;
	}

	public static void setReflectionsEnabled(boolean enableReflections) {
		INSTANCE.enableReflections = enableReflections;
	}

	public static float getReflectionQuality() {
		return INSTANCE.reflectionQuality;
	}

	public static void setReflectionQuality(float reflectionQuality) {
		INSTANCE.reflectionQuality = reflectionQuality;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		water.delete();
	}
}
