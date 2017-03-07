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
import flounder.noise.*;
import flounder.profiling.*;
import kosmos.entities.components.*;
import kosmos.entities.instances.*;

import java.util.*;

public class KosmosWorld extends Module {
	private static final KosmosWorld INSTANCE = new KosmosWorld();
	public static final String PROFILE_TAB_NAME = "Kosmos World";

	public static final float GRAVITY = -9.81f;

	private PerlinNoise noise;

	private Map<String, Pair<Vector3f, Vector3f>> playerQue;
	private Map<String, Entity> players;

	private Fog fog;
	private SkyCycle skyCycle;

	public KosmosWorld() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderEntities.class);
	}

	@Override
	public void init() {
		this.noise = new PerlinNoise(537); // new PerlinNoise(KosmosConfigs.configSave.getIntWithDefault("seed", (int) Maths.randomInRange(1.0, 10000.0), () -> KosmosChunks.getNoise().getSeed()));

		this.playerQue = new HashMap<>();
		this.players = new HashMap<>();

		this.fog = new Fog(new Colour(), 0.02f, 2.0f, 0.0f, 50.0f);
		this.skyCycle = new SkyCycle();
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
		FlounderProfiler.add(PROFILE_TAB_NAME, "Seed", noise.getSeed());
	}

	public static PerlinNoise getNoise() {
		return INSTANCE.noise;
	}

	public static Entity getPlayer(String username) {
		return INSTANCE.players.get(username);
	}

	public static boolean containsPlayer(String username) {
		return INSTANCE.players.containsKey(username);
	}

	public static void addPlayer(Vector3f position, Vector3f rotation, String username) {
		INSTANCE.players.put(username, new InstanceMuliplayer(FlounderEntities.getEntities(), position, rotation, username));
	}

	public static void movePlayer(String username, float x, float y, float z, float w, float chunkX, float chunkZ) {
		if (FlounderNetwork.getUsername().equals(username)) {
			return;
		}

		if (!INSTANCE.players.containsKey(username)) {
			if (!INSTANCE.playerQue.containsKey(username)) {
				INSTANCE.playerQue.put(username, new Pair<>(new Vector3f(x, y, z), new Vector3f(0.0f, w, 0.0f)));
			}

			return;
		}

		((ComponentMultiplayer) INSTANCE.players.get(username).getComponent(ComponentMultiplayer.ID)).move(x, y, z, w, chunkX, chunkZ);
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
