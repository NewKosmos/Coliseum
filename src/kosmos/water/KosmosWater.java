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
import flounder.loaders.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import kosmos.*;

public class KosmosWater extends Module {
	private Water water;
	private float waveTime;

	private float colourIntensity; // 0 being 100% reflective, 1 disables reflections.
	private boolean enableReflections;
	private float reflectionQuality;
	private boolean reflectionShadows;

	public KosmosWater() {
		super(FlounderBounding.class, FlounderLoader.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.waveTime = 0.0f;

		this.colourIntensity = KosmosConfigs.WATER_COLOUR_INTENSITY.getFloat();
		this.enableReflections = KosmosConfigs.WATER_REFLECTION_ENABLED.getBoolean();
		this.reflectionQuality = KosmosConfigs.WATER_REFLECTION_QUALITY.getFloat();
		this.reflectionShadows = KosmosConfigs.WATER_REFLECTION_SHADOWS.getBoolean();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		if (water == null) {
			return;
		}

		water.update();
		waveTime += Framework.getDelta();
		waveTime %= Water.WAVE_SPEED;
		FlounderBounding.get().addShapeRender(water.getAABB());
	}

	public void generateWater() {
		this.water = new Water(new Vector3f(0.0f, -0.1f, 0.0f), new Vector3f(), 1.0f);
	}

	public void deleteWater() {
		this.water.delete();
		this.water = null;
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
	}

	public Water getWater() {
		return this.water;
	}

	public float getWaveTime() {
		return this.waveTime;
	}

	public float getColourIntensity() {
		return this.colourIntensity;
	}

	public void setColourIntensity(float colourIntensity) {
		this.colourIntensity = colourIntensity;
	}

	public boolean reflectionsEnabled() {
		return this.enableReflections;
	}

	public void setReflectionsEnabled(boolean enableReflections) {
		this.enableReflections = enableReflections;
	}

	public float getReflectionQuality() {
		return this.reflectionQuality;
	}

	public void setReflectionQuality(float reflectionQuality) {
		this.reflectionQuality = reflectionQuality;
	}

	public boolean reflectionShadows() {
		return this.reflectionShadows;
	}

	public void setReflectionShadows(boolean reflectionShadows) {
		this.reflectionShadows = reflectionShadows;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		if (water != null) {
			water.delete();
		}
	}

	@Module.Instance
	public static KosmosWater get() {
		return (KosmosWater) Framework.getInstance(KosmosWater.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Kosmos Water";
	}
}
