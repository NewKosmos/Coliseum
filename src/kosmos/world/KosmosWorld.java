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
import flounder.guis.*;
import flounder.helpers.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.noise.*;
import flounder.profiling.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.chunks.*;
import kosmos.entities.components.*;
import kosmos.entities.instances.world.*;
import kosmos.water.*;

import java.util.*;

public class KosmosWorld extends Module {
	private static final KosmosWorld INSTANCE = new KosmosWorld();
	public static final String PROFILE_TAB_NAME = "Kosmos World";

	public static final float GRAVITY = -11.0f;

	public static final Colour SKY_COLOUR_NIGHT = new Colour(0.0f, 0.07f, 0.19f);
	public static final Colour SKY_COLOUR_SUNRISE = new Colour(0.713f, 0.494f, 0.356f);
	public static final Colour SKY_COLOUR_DAY = new Colour(0.0f, 0.30f, 0.70f);

	public static final Colour SUN_COLOUR_SUNRISE = new Colour(0.713f, 0.494f, 0.356f);
	public static final Colour SUN_COLOUR_DAY = new Colour(0.70f, 0.70f, 0.70f);

	public static final Colour MOON_COLOUR = new Colour(0.233f, 0.233f, 0.233f);

	public static final float DAY_NIGHT_CYCLE = 210.0f; // The day/night length (sec).

	private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.5f, 0.0f, 0.5f); // The starting light direction.

	private PerlinNoise noise;

	private Map<String, Pair<Vector3f, Vector3f>> playerQue;
	private Map<String, Entity> players;

	private Entity entityPlayer;
	private Entity entitySun;
	private Entity entityMoon;

	private Fog fog;

	private LinearDriver dayDriver;
	private float dayFactor;
	private Colour skyColour;
	private Vector3f lightRotation;
	private Vector3f lightPosition;

	public KosmosWorld() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderEntities.class);
	}

	@Override
	public void init() {
		this.noise = new PerlinNoise(KosmosConfigs.SAVE_SEED.setReference(() -> noise.getSeed()).getInteger());

		this.entityMoon = new InstanceMoon(FlounderEntities.getEntities(), new Vector3f(150.0f, 150.0f, 150.0f), new Vector3f(0.0f, 0.0f, 0.0f));
		this.entitySun = new InstanceSun(FlounderEntities.getEntities(), new Vector3f(-150.0f, -150.0f, -150.0f), new Vector3f(0.0f, 0.0f, 0.0f));

		this.playerQue = new HashMap<>();
		this.players = new HashMap<>();

		this.fog = new Fog(new Colour(), 0.02f, 2.0f, 0.0f, 50.0f);

		this.dayDriver = new LinearDriver(0.0f, 100.0f, DAY_NIGHT_CYCLE);
		this.dayFactor = 0.0f;
		this.skyColour = new Colour(SKY_COLOUR_DAY);
		this.lightRotation = new Vector3f();
		this.lightPosition = new Vector3f(LIGHT_DIRECTION);
	}

	public static void generatePlayer() {
		INSTANCE.entityPlayer = new InstancePlayer(FlounderEntities.getEntities(),
				new Vector3f(
						KosmosConfigs.SAVE_PLAYER_X.setReference(() -> KosmosWorld.getEntityPlayer().getPosition().x).getFloat(),
						KosmosConfigs.SAVE_PLAYER_Y.setReference(() -> KosmosWorld.getEntityPlayer().getPosition().y).getFloat(),
						KosmosConfigs.SAVE_PLAYER_Z.setReference(() -> KosmosWorld.getEntityPlayer().getPosition().z).getFloat()),
				new Vector3f()
		);
		KosmosWater.generateWater();
		KosmosChunks.setCurrent(new Chunk(KosmosChunks.getChunks(), new Vector3f(
				KosmosConfigs.SAVE_CHUNK_X.setReference(() -> KosmosChunks.getCurrent().getPosition().x).getFloat(),
				0.0f,
				KosmosConfigs.SAVE_CHUNK_Z.setReference(() -> KosmosChunks.getCurrent().getPosition().z).getFloat()
		))); // The root chunk.
	}

	public static void deletePlayer() {
		KosmosConfigs.saveAllConfigs();
		KosmosWater.deleteWater();
		KosmosChunks.clear(false);
		INSTANCE.entityPlayer.forceRemove(true);
	}

	@Override
	public void update() {
		// Move qued players to the world.
		if (!playerQue.isEmpty()) {
			for (String name : new HashMap<>(playerQue).keySet()) {
				Pair<Vector3f, Vector3f> data = playerQue.get(name);
				players.put(name, new InstanceMuliplayer(FlounderEntities.getEntities(), data.getFirst(), data.getSecond(), name));
				playerQue.remove(name);
			}
		}

		// Update the sky colours and sun position.
		dayFactor = dayDriver.update(Framework.getDelta()) / 100.0f; // 0.52f
		Colour.interpolate(SKY_COLOUR_SUNRISE, SKY_COLOUR_NIGHT, getSunriseFactor(), skyColour);
		Colour.interpolate(skyColour, SKY_COLOUR_DAY, getShadowFactor(), skyColour);
		Vector3f.rotate(LIGHT_DIRECTION, lightRotation.set(dayFactor * 360.0f, 0.0f, 0.0f), lightPosition);
		fog.setFogColour(skyColour);
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

	public synchronized static void quePlayer(String username, Vector3f position, Vector3f rotation) {
		INSTANCE.playerQue.put(username, new Pair<>(position, rotation));
	}

	public synchronized static void movePlayer(String username, float x, float y, float z, float w, float chunkX, float chunkZ) {
		if (FlounderNetwork.getUsername().equals(username)) {
			return;
		}

		if (!INSTANCE.players.containsKey(username)) {
			if (!INSTANCE.playerQue.containsKey(username)) {
				INSTANCE.playerQue.put(username, new Pair<>(new Vector3f(x, y, z), new Vector3f(0.0f, w, 0.0f)));
			} else {
				INSTANCE.playerQue.get(username).getFirst().set(x, y, z);
				INSTANCE.playerQue.get(username).getSecond().set(0.0f, w, 0.0f);
			}

			return;
		}

		((ComponentMultiplayer) INSTANCE.players.get(username).getComponent(ComponentMultiplayer.ID)).move(x, y, z, w, chunkX, chunkZ);
	}

	public synchronized static void removePlayer(String username) {
		if (INSTANCE.playerQue.containsKey(username)) {
			INSTANCE.playerQue.remove(username);
		}

		if (INSTANCE.players.containsKey(username)) {
			Entity otherPlayer = INSTANCE.players.get(username);
			otherPlayer.forceRemove(false);
			INSTANCE.players.remove(username);
			FlounderEntities.getEntities().remove(otherPlayer);
		}
	}

	public synchronized static void removeAllPlayers() {
		for (String username : INSTANCE.players.keySet()) {
			Entity otherPlayer = INSTANCE.players.get(username);
			otherPlayer.forceRemove(true);
		}

		INSTANCE.playerQue.clear();
		INSTANCE.players.clear();
	}

	public static int connectedPlayers() {
		return INSTANCE.players.size();
	}

	public static Entity getEntityPlayer() {
		return INSTANCE.entityPlayer;
	}

	public static Entity getEntitySun() {
		return INSTANCE.entitySun;
	}

	public static Entity getEntityMoon() {
		return INSTANCE.entityMoon;
	}

	public static Fog getFog() {
		return INSTANCE.fog;
	}

	public static float getDayFactor() {
		return INSTANCE.dayFactor;
	}

	public static float getSunriseFactor() {
		return (float) -(Math.sin(2.0 * Math.PI * getDayFactor()) - 1.0) / 2.0f;
	}

	public static float getShadowFactor() {
		return (float) Maths.clamp(1.7f * Math.sin(2.0f * Math.PI * getDayFactor()), 0.0, 1.0);
	}

	public static float starIntensity() {
		return (1.0f - KosmosWorld.getShadowFactor()) + (((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlaySlider().inStartMenu() ? 0.5f : 0.0f);
	}

	public static Colour getSkyColour() {
		return INSTANCE.skyColour;
	}

	public static Vector3f getLightRotation() {
		return INSTANCE.lightRotation;
	}

	public static Vector3f getLightPosition() {
		return INSTANCE.lightPosition;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
