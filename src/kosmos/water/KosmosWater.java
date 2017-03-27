/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

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
	private boolean reflectionShadows;

	public KosmosWater() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class);
	}

	@Override
	public void init() {
		this.waveTime = 0.0f;

		this.enableReflections = KosmosConfigs.WATER_REFLECTION_ENABLED.getBoolean();
		this.reflectionQuality = KosmosConfigs.WATER_REFLECTION_QUALITY.getFloat();
		this.reflectionShadows = KosmosConfigs.WATER_REFLECTION_SHADOWS.getBoolean();
	}

	@Override
	public void update() {
		if (water == null) {
			return;
		}

		water.update();
		waveTime += Framework.getDelta();
		waveTime %= Water.WAVE_SPEED;
		FlounderBounding.addShapeRender(water.getAABB());
	}

	public static void generateWater() {
		INSTANCE.water = new Water(new Vector3f(0.0f, -0.1f, 0.0f), new Vector3f(), 1.0f);
	}

	public static void deleteWater() {
		INSTANCE.water.delete();
		INSTANCE.water = null;
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
		return INSTANCE.enableReflections;
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

	public static boolean reflectionShadows() {
		return INSTANCE.reflectionShadows;
	}

	public static void setReflectionShadows(boolean reflectionShadows) {
		INSTANCE.reflectionShadows = reflectionShadows;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		if (water != null) {
			water.delete();
		}
	}
}
