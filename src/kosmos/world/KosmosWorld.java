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
import flounder.helpers.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import kosmos.entities.components.*;
import kosmos.entities.instances.*;
import kosmos.particles.*;

import java.util.*;

public class KosmosWorld extends Module {
	private static final KosmosWorld INSTANCE = new KosmosWorld();
	public static final String PROFILE_TAB_NAME = "Kosmos World";

	private Map<String, Pair<Vector3f, Vector3f>> playerQue;
	private Map<String, Entity> players;

	private Fog fog;
	private SkyCycle skyCycle;

	private Entity entitySun;
	private Entity entityMoon;

	public KosmosWorld() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderEntities.class, KosmosParticles.class);
	}

	@Override
	public void init() {
		this.playerQue = new HashMap<>();
		this.players = new HashMap<>();

		this.fog = new Fog(new Colour(), 0.02f, 2.0f, 0.0f, 50.0f);
		this.skyCycle = new SkyCycle();

		this.entityMoon = new InstanceMoon(FlounderEntities.getEntities(), new Vector3f(200.0f, 200.0f, 200.0f), new Vector3f(0.0f, 0.0f, 0.0f));
		this.entitySun = new InstanceSun(FlounderEntities.getEntities(), new Vector3f(-200.0f, -200.0f, -200.0f), new Vector3f(0.0f, 0.0f, 0.0f));
	}

	@Override
	public void update() {
		if (!playerQue.isEmpty()) {
			for (String name : playerQue.keySet()) {
				Pair<Vector3f, Vector3f> data = playerQue.get(name);
				players.put(name, new InstanceMuliplayer(FlounderEntities.getEntities(), data.getFirst(), data.getSecond(), name));
			}

			playerQue.clear();
		}

		skyCycle.update();
		fog.setFogColour(skyCycle.getSkyColour());
	}

	@Override
	public void profile() {
	}

	public static Entity getPlayer(String username) {
		return INSTANCE.players.get(username);
	}

	public static void addPlayer(Vector3f position, Vector3f rotation, String username) {
		INSTANCE.players.put(username, new InstanceMuliplayer(FlounderEntities.getEntities(), position, rotation, username));
	}

	public static void movePlayer(String username, float x, float y, float z, float w) {
		if (FlounderNetwork.getUsername().equals(username)) {
			return;
		}

		if (!INSTANCE.players.containsKey(username)) {
			if (!INSTANCE.playerQue.containsKey(username)) {
				INSTANCE.playerQue.put(username, new Pair<>(new Vector3f(x, y, z), new Vector3f(0.0f, w, 0.0f)));
			}

			return;
		}

		((ComponentMultiplayer) INSTANCE.players.get(username).getComponent(ComponentMultiplayer.ID)).move(x, y, z, w);
	}

	public static void removePlayer(String username) {
		if (!INSTANCE.players.containsKey(username)) {
			return;
		}

		INSTANCE.players.get(username).forceRemove(true);
		INSTANCE.players.remove(username);
	}

	public static int connectedPlayers() {
		return INSTANCE.players.size();
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
