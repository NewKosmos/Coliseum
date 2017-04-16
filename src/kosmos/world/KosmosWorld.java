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
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.noise.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.shadows.*;
import flounder.skybox.*;
import flounder.textures.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.chunks.*;
import kosmos.entities.components.*;
import kosmos.entities.instances.*;
import kosmos.water.*;

import java.util.*;

public class KosmosWorld extends Module {
	private static final KosmosWorld INSTANCE = new KosmosWorld();
	public static final String PROFILE_TAB_NAME = "Kosmos World";

	public static final float GRAVITY = -11.0f;

	private static MyFile[] SKYBOX_TEXTURE_FILES = {
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsRight.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsLeft.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsTop.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsBottom.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsBack.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsFront.png")
	};

	public static final Colour SKY_COLOUR_NIGHT = new Colour(0.05f, 0.05f, 0.1f);
	public static final Colour SKY_COLOUR_SUNRISE = new Colour(0.8f, 0.4f, 0.3f);
	public static final Colour SKY_COLOUR_DAY = new Colour(0.0f, 0.3f, 0.7f);

	public static final Colour SUN_COLOUR_NIGHT = new Colour(0.0f, 0.0f, 0.0f);
	public static final Colour SUN_COLOUR_SUNRISE = new Colour(0.7f, 0.4f, 0.3f);
	public static final Colour SUN_COLOUR_DAY = new Colour(0.8f, 0.8f, 0.8f);

	public static final Colour MOON_COLOUR = new Colour(0.1f, 0.1f, 0.3f);

	public static final float DAY_NIGHT_CYCLE = 500.0f; // The day/night length (sec).

	private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.2f, 0.0f, 0.5f); // The starting light direction.

	private PerlinNoise noise;

	private Map<String, Pair<Vector3f, Vector3f>> playerQue;
	private Map<String, Entity> players;

	private Entity entityPlayer;
	private Entity entitySun;
	private Entity entityMoon;

	private LinearDriver dayDriver;
	private float dayFactor;

	public KosmosWorld() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderEntities.class);
	}

	@Override
	public void init() {
		this.noise = new PerlinNoise(KosmosConfigs.SAVE_SEED.setReference(() -> noise.getSeed()).getInteger());

		this.entityMoon = new InstanceMoon(FlounderEntities.getEntities(), new Vector3f(250.0f, 250.0f, 250.0f), new Vector3f(0.0f, 0.0f, 0.0f));
		this.entitySun = new InstanceSun(FlounderEntities.getEntities(), new Vector3f(-250.0f, -250.0f, -250.0f), new Vector3f(0.0f, 0.0f, 0.0f));

		this.playerQue = new HashMap<>();
		this.players = new HashMap<>();

		this.dayDriver = new LinearDriver(0.0f, 100.0f, DAY_NIGHT_CYCLE);
		this.dayFactor = 0.0f;

		FlounderShadows.setBrightnessBoost(KosmosConfigs.BRIGHTNESS_BOOST.getFloat());
		FlounderShadows.setShadowSize(KosmosConfigs.SHADOWMAP_SIZE.getInteger());
		FlounderShadows.setShadowPCF(KosmosConfigs.SHADOWMAP_PCF.getInteger());
		FlounderShadows.setShadowBias(KosmosConfigs.SHADOWMAP_BIAS.getFloat());
		FlounderShadows.setShadowDarkness(KosmosConfigs.SHADOWMAP_DARKNESS.getFloat());
		FlounderSkybox.setCubemap(TextureFactory.newBuilder().setCubemap(SKYBOX_TEXTURE_FILES).create());
	}

	public static void generatePlayer() {
		INSTANCE.entityPlayer = new InstancePlayer(FlounderEntities.getEntities(),
				new Vector3f(
						KosmosConfigs.SAVE_PLAYER_X.setReference(() -> KosmosWorld.getEntityPlayer().getPosition().x).getFloat(),
						KosmosConfigs.SAVE_PLAYER_Y.setReference(() -> KosmosWorld.getEntityPlayer().getPosition().y).getFloat(),
						KosmosConfigs.SAVE_PLAYER_Z.setReference(() -> KosmosWorld.getEntityPlayer().getPosition().z).getFloat()),
				new Vector3f()
		);
		KosmosChunks.setCurrent(new Chunk(FlounderEntities.getEntities(), new Vector3f(
				KosmosConfigs.SAVE_CHUNK_X.setReference(() -> KosmosChunks.getCurrent().getPosition().x).getFloat(),
				0.0f,
				KosmosConfigs.SAVE_CHUNK_Z.setReference(() -> KosmosChunks.getCurrent().getPosition().z).getFloat()
		))); // The root chunk.
		KosmosWater.generateWater();
	}

	public static void deletePlayer() {
		KosmosWater.deleteWater();
		KosmosChunks.clear(false);
		INSTANCE.entityPlayer.forceRemove();
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
		float scaledSpeed = 1.0f;

		if (FlounderGuis.getGuiMaster() instanceof KosmosGuis) {
			scaledSpeed = ((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlaySlider().inStartMenu() ? 4.20f : 1.0f;
		}

		dayFactor = dayDriver.update(Framework.getDelta() * scaledSpeed) / 100.0f;
		Vector3f.rotate(LIGHT_DIRECTION, FlounderSkybox.getRotation().set(dayFactor * 360.0f, 0.0f, 0.0f), FlounderShadows.getLightPosition());
		Colour.interpolate(SKY_COLOUR_SUNRISE, SKY_COLOUR_NIGHT, getSunriseFactor(), FlounderSkybox.getFog().getFogColour());
		Colour.interpolate(FlounderSkybox.getFog().getFogColour(), SKY_COLOUR_DAY, getShadowFactor(), FlounderSkybox.getFog().getFogColour());
		FlounderSkybox.getFog().setFogDensity(0.023f + ((1.0f - KosmosWorld.getShadowFactor()) * 0.016f));
		FlounderSkybox.getFog().setFogGradient(2.80f - ((1.0f - KosmosWorld.getShadowFactor()) * 0.5f));
		FlounderSkybox.setBlendFactor(starIntensity());
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

		((ComponentMultiplayer) INSTANCE.players.get(username).getComponent(ComponentMultiplayer.class)).move(x, y, z, w, chunkX, chunkZ);
	}

	public synchronized static void removePlayer(String username) {
		if (INSTANCE.playerQue.containsKey(username)) {
			INSTANCE.playerQue.remove(username);
		}

		if (INSTANCE.players.containsKey(username)) {
			Entity otherPlayer = INSTANCE.players.get(username);
			otherPlayer.forceRemove();
			INSTANCE.players.remove(username);
		}
	}

	public synchronized static void removeAllPlayers() {
		for (String username : INSTANCE.players.keySet()) {
			Entity otherPlayer = INSTANCE.players.get(username);
			otherPlayer.forceRemove();
		}

		INSTANCE.playerQue.clear();
		INSTANCE.players.clear();
	}

	public static Map<String, Entity> getPlayers() {
		return INSTANCE.players;
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

	public static float getDayFactor() {
		return INSTANCE.dayFactor;
	}

	public static float getSunriseFactor() {
		return (float) Maths.clamp(-(Math.sin(2.0 * Math.PI * getDayFactor()) - 1.0) / 2.0f, 0.0, 1.0);
	}

	public static float getShadowFactor() {
		return (float) Maths.clamp(1.7f * Math.sin(2.0f * Math.PI * getDayFactor()), 0.0, 1.0);
	}

	public static float getSunHeight() {
		float addedHeight = 0.0f;

		if (FlounderGuis.getGuiMaster() instanceof KosmosGuis) {
			addedHeight = ((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlaySlider().inStartMenu() ? 500.0f : 0.0f;
		}

		return KosmosWorld.getEntitySun().getPosition().getY() + addedHeight;
	}

	public static float starIntensity() {
		float addedIntensity = 0.0f;

		if (FlounderGuis.getGuiMaster() instanceof KosmosGuis) {
			addedIntensity = ((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlaySlider().inStartMenu() ? 0.5f : 0.0f;
		}

		return Maths.clamp(1.0f - getShadowFactor() + addedIntensity, 0.0f, 1.0f);
	}

	public static float getBloomThreshold() {
		return 0.60f; // 0.8f * (getShadowFactor()) + 0.2f; // TODO
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
