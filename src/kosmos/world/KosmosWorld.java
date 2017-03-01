/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.world;

import flounder.entities.*;
import flounder.framework.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import kosmos.entities.instances.*;
import kosmos.particles.*;

public class KosmosWorld extends Module {
	private static final KosmosWorld INSTANCE = new KosmosWorld();
	public static final String PROFILE_TAB_NAME = "Kosmos World";

	private Fog fog;
	private SkyCycle skyCycle;

	private Entity entitySun;
	private Entity entityMoon;

	public KosmosWorld() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderEntities.class, KosmosParticles.class);
	}

	@Override
	public void init() {
		this.fog = new Fog(new Colour(), 0.02f, 2.0f, 0.0f, 50.0f);
		this.skyCycle = new SkyCycle();

		this.entityMoon = new InstanceMoon(FlounderEntities.getEntities(), new Vector3f(200.0f, 200.0f, 200.0f), new Vector3f(0.0f, 0.0f, 0.0f));
		this.entitySun = new InstanceSun(FlounderEntities.getEntities(), new Vector3f(-200.0f, -200.0f, -200.0f), new Vector3f(0.0f, 0.0f, 0.0f));
	}

	@Override
	public void update() {
		skyCycle.update();
		fog.setFogColour(skyCycle.getSkyColour());
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

	public static Entity getEntitySun() {
		return INSTANCE.entitySun;
	}

	public static Entity getEntityMoon() {
		return INSTANCE.entityMoon;
	}

	public static float getSwayOffsetX() {
		float windPower = 0.2f;
		float systemTime = Framework.getTimeSec();
		return windPower * (float) (Math.sin(0.25 * systemTime) - Math.sin(1.2 * systemTime) + Math.cos(0.5 * systemTime));
	}

	public static float getSwayOffsetY() {
		float windPower = 0.2f;
		float systemTime = Framework.getTimeSec();
		return windPower * (float) (Math.cos(0.25 * systemTime) - Math.cos(1.2 * systemTime) + Math.sin(0.5 * systemTime));
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
