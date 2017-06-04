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
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.resources.*;
import flounder.shadows.*;
import flounder.skybox.*;
import flounder.tasks.*;
import flounder.textures.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.chunks.*;
import kosmos.entities.components.*;
import kosmos.entities.instances.*;
import kosmos.water.*;

import java.util.*;

public class KosmosWorld extends Module {
	public static final float GRAVITY = -32.0f;

	private MyFile[] SKYBOX_TEXTURE_FILES = {
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsRight.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsLeft.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsTop.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsBottom.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsBack.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsFront.png")
	};

	public static final Colour SKY_COLOUR_NIGHT = new Colour(0.05f, 0.05f, 0.1f);
	public static final Colour SKY_COLOUR_SUNRISE = new Colour(0.9f, 0.3f, 0.3f);
	public static final Colour SKY_COLOUR_DAY = new Colour(0.0f, 0.3f, 0.7f);

	public static final Colour SUN_COLOUR_NIGHT = new Colour(0.0f, 0.0f, 0.0f);
	public static final Colour SUN_COLOUR_SUNRISE = new Colour(0.9f, 0.3f, 0.3f);
	public static final Colour SUN_COLOUR_DAY = new Colour(0.7f, 0.7f, 0.7f);

	public static final Colour MOON_COLOUR_NIGHT = new Colour(0.2f, 0.2f, 0.3f);
	public static final Colour MOON_COLOUR_DAY = new Colour(0.0f, 0.0f, 0.0f);

	public static final float DAY_NIGHT_CYCLE = 300.0f; // The day/night length (sec).

	private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.2f, 0.0f, 0.5f); // The starting light direction.

	private Map<String, Entity> players;

	private WorldSetup worldSetup;

	private Entity entityPlayer;
	private Entity entitySun;
	private Entity entityMoon1;

	private LinearDriver dayDriver;
	private float dayFactor;

	public KosmosWorld() {
		super(FlounderEntities.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.entityPlayer = null;
		this.entitySun = new InstanceSun(FlounderEntities.get().getEntities(), new Vector3f(-250.0f, -250.0f, -250.0f), new Vector3f(0.0f, 0.0f, 0.0f));
		this.entityMoon1 = new InstanceMoon1(FlounderEntities.get().getEntities(), new Vector3f(200.0f, 250.0f, 220.0f), new Vector3f(0.0f, 0.0f, 0.0f)); // Red

		this.players = new HashMap<>();

		this.worldSetup = null;

		this.dayDriver = new LinearDriver(0.0f, 100.0f, DAY_NIGHT_CYCLE);
		this.dayFactor = 0.0f;

		if (FlounderShadows.get() != null) {
			FlounderShadows.get().setBrightnessBoost(KosmosConfigs.BRIGHTNESS_BOOST.getFloat());
			FlounderShadows.get().setShadowSize(KosmosConfigs.SHADOWMAP_SIZE.getInteger());
			FlounderShadows.get().setShadowPCF(KosmosConfigs.SHADOWMAP_PCF.getInteger());
			FlounderShadows.get().setShadowBias(KosmosConfigs.SHADOWMAP_BIAS.getFloat());
			FlounderShadows.get().setShadowDarkness(KosmosConfigs.SHADOWMAP_DARKNESS.getFloat());
			FlounderShadows.get().setRenderUnlimited(KosmosConfigs.SHADOWMAP_UNLIMITED.getBoolean());
		}

		if (FlounderSkybox.get() != null) {
			FlounderSkybox.get().setCubemap(TextureFactory.newBuilder().setCubemap(SKYBOX_TEXTURE_FILES).create());
		}
	}

	public void generateWorld(int seed, Vector3f positionPlayer, Vector3f positionChunk) {
		this.worldSetup = new WorldSetup(seed, positionPlayer, positionChunk);
	}

	public void deleteWorld() {
		KosmosConfigs.saveAllConfigs();
		KosmosChunks.get().getNoise().setSeed(-1);

		entityPlayer.forceRemove();
		removeAllPlayers();

		KosmosChunks.get().clear(false);
		KosmosWater.get().deleteWater();

		KosmosConfigs.fixConfigRefs();

		System.gc();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		// Create the world if a world setup exists.
		if (worldSetup != null) {
			// Sets the seed.
			KosmosChunks.get().getNoise().setSeed(worldSetup.seed);

			// Creates the player.
			this.entityPlayer = new InstancePlayer(FlounderEntities.get().getEntities(), worldSetup.positionPlayer, new Vector3f());

			// Creates the current chunk.
			KosmosChunks.get().setCurrent(new Chunk(FlounderEntities.get().getEntities(), worldSetup.positionChunk));

			// Creates the water.
			KosmosWater.get().generateWater();

			worldSetup = null;
		}

		// Update the sky colours and sun position.
		if (FlounderSkybox.get() != null && FlounderShadows.get() != null) {
			dayFactor = dayDriver.update(Framework.get().getDelta()) / 100.0f;
			// TODO: Day night factor.
			Vector3f.rotate(LIGHT_DIRECTION, FlounderSkybox.get().getRotation().set(dayFactor * 360.0f, 0.0f, 0.0f), FlounderShadows.get().getLightPosition()).normalize();
			Colour.interpolate(SKY_COLOUR_SUNRISE, SKY_COLOUR_NIGHT, getSunriseFactor(), FlounderSkybox.get().getFog().getFogColour());
			Colour.interpolate(FlounderSkybox.get().getFog().getFogColour(), SKY_COLOUR_DAY, getShadowFactor(), FlounderSkybox.get().getFog().getFogColour());
			FlounderSkybox.get().getFog().setFogDensity(0.006f + ((16 - KosmosChunks.get().getChunkDistance()) * 0.001f) + ((1.0f - getShadowFactor()) * 0.006f));
			FlounderSkybox.get().getFog().setFogGradient(2.80f - ((1.0f - getShadowFactor()) * 0.4f));
			FlounderSkybox.get().setBlendFactor(starIntensity());
			FlounderShadows.get().setShadowBoxOffset(10.0f);
			FlounderShadows.get().setShadowBoxDistance(35.0f);
		}
	}

	public Entity getPlayer(String username) {
		return this.players.get(username);
	}

	public boolean containsPlayer(String username) {
		return this.players.containsKey(username);
	}

	public synchronized void quePlayer(String username, Vector3f position, Vector3f rotation) {
		FlounderTasks.get().addTask(() -> {
			players.put(username, new InstanceMuliplayer(FlounderEntities.get().getEntities(), position, rotation, username));
		});
	}

	public synchronized void movePlayer(String username, float x, float y, float z, float w, float chunkX, float chunkZ) {
		if (FlounderNetwork.get().getUsername().equals(username)) {
			return;
		}

		if (!this.players.containsKey(username)) {
			return;
		}

		((ComponentMultiplayer) this.players.get(username).getComponent(ComponentMultiplayer.class)).move(x, y, z, w, chunkX, chunkZ);
	}

	public synchronized void removePlayer(String username) {
		if (this.players.containsKey(username)) {
			Entity otherPlayer = this.players.get(username);
			otherPlayer.forceRemove();
			this.players.remove(username);
		}
	}

	public synchronized void removeAllPlayers() {
		for (String username : this.players.keySet()) {
			Entity otherPlayer = this.players.get(username);
			otherPlayer.forceRemove();
		}

		this.players.clear();
	}

	public Map<String, Entity> getPlayers() {
		return this.players;
	}

	public int connectedPlayers() {
		return this.players.size();
	}

	public Entity getEntityPlayer() {
		return this.entityPlayer;
	}

	public Entity getEntitySun() {
		return this.entitySun;
	}

	public Entity getEntityMoon1() {
		return entityMoon1;
	}

	public float getDayFactor() {
		return this.dayFactor;
	}

	public float getSunriseFactor() {
		return (float) Maths.clamp(-(Math.sin(2.0 * Math.PI * getDayFactor()) - 1.0) / 2.0f, 0.0, 1.0);
	}

	public float getShadowFactor() {
		return (float) Maths.clamp(1.7f * Math.sin(2.0f * Math.PI * getDayFactor()), 0.0, 1.0);
	}

	public float getSunHeight() {
		float addedHeight = 0.0f;

		if (FlounderGuis.get().getGuiMaster() instanceof KosmosGuis) {
			addedHeight = ((KosmosGuis) FlounderGuis.get().getGuiMaster()).getOverlaySlider().inStartMenu() ? 500.0f : 0.0f;
		}

		return getEntitySun().getPosition().getY() + addedHeight;
	}

	public float starIntensity() {
		float addedIntensity = 0.0f;

		if (FlounderGuis.get().getGuiMaster() instanceof KosmosGuis) {
			addedIntensity = ((KosmosGuis) FlounderGuis.get().getGuiMaster()).getOverlaySlider().inStartMenu() ? 0.5f : 0.0f;
		}

		return Maths.clamp(1.0f - getShadowFactor() + addedIntensity, 0.0f, 1.0f);
	}

	@Override
	public Module getInstance() {
		return this;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@Module.Instance
	public static KosmosWorld get() {
		return (KosmosWorld) Framework.get().getInstance(KosmosWorld.class);
	}

	public static class WorldSetup {
		protected final int seed;
		protected final Vector3f positionPlayer;
		protected final Vector3f positionChunk;

		public WorldSetup(int seed, Vector3f positionPlayer, Vector3f positionChunk) {
			this.seed = seed;
			this.positionPlayer = positionPlayer;
			this.positionChunk = positionChunk;
		}
	}
}
