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

	public static final float DAY_NIGHT_CYCLE = 420.0f; // The day/night length (sec).

	private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.2f, 0.0f, 0.5f); // The starting light direction.

	private PerlinNoise noise;

	private Map<String, Pair<Vector3f, Vector3f>> playerQue;
	private Map<String, Entity> players;

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
		this.noise = new PerlinNoise(-1);

		this.entityPlayer = null;
		this.entitySun = new InstanceSun(FlounderEntities.get().getEntities(), new Vector3f(-250.0f, -250.0f, -250.0f), new Vector3f(0.0f, 0.0f, 0.0f));
		this.entityMoon1 = new InstanceMoon1(FlounderEntities.get().getEntities(), new Vector3f(200.0f, 250.0f, 220.0f), new Vector3f(0.0f, 0.0f, 0.0f)); // Red

		this.playerQue = new HashMap<>();
		this.players = new HashMap<>();

		this.dayDriver = new LinearDriver(0.0f, 100.0f, DAY_NIGHT_CYCLE);
		this.dayFactor = 0.0f;

		FlounderShadows.get().setBrightnessBoost(KosmosConfigs.BRIGHTNESS_BOOST.getFloat());
		FlounderShadows.get().setShadowSize(KosmosConfigs.SHADOWMAP_SIZE.getInteger());
		FlounderShadows.get().setShadowPCF(KosmosConfigs.SHADOWMAP_PCF.getInteger());
		FlounderShadows.get().setShadowBias(KosmosConfigs.SHADOWMAP_BIAS.getFloat());
		FlounderShadows.get().setShadowDarkness(KosmosConfigs.SHADOWMAP_DARKNESS.getFloat());
		FlounderShadows.get().setRenderUnlimited(KosmosConfigs.SHADOWMAP_UNLIMITED.getBoolean());
		FlounderSkybox.get().setCubemap(TextureFactory.newBuilder().setCubemap(SKYBOX_TEXTURE_FILES).create());
	}

	public void generateWorld(int seed) {
		// Sets the seed.
		this.noise.setSeed(seed);

		// Creates the player.
		this.entityPlayer = new InstancePlayer(FlounderEntities.get().getEntities(),
				new Vector3f(
						KosmosConfigs.SAVE_PLAYER_X.setReference(() -> KosmosWorld.get().getEntityPlayer().getPosition().x).getFloat(),
						KosmosConfigs.SAVE_PLAYER_Y.setReference(() -> KosmosWorld.get().getEntityPlayer().getPosition().y).getFloat(),
						KosmosConfigs.SAVE_PLAYER_Z.setReference(() -> KosmosWorld.get().getEntityPlayer().getPosition().z).getFloat()),
				new Vector3f()
		);

		// Creates the current chunk.
		KosmosChunks.get().setCurrent(new Chunk(FlounderEntities.get().getEntities(), new Vector3f(
				KosmosConfigs.SAVE_CHUNK_X.setReference(() -> KosmosChunks.get().getCurrent().getPosition().x).getFloat(),
				0.0f,
				KosmosConfigs.SAVE_CHUNK_Z.setReference(() -> KosmosChunks.get().getCurrent().getPosition().z).getFloat()
		)));

		// Creates the water.
		KosmosWater.get().generateWater();
	}

	public void deleteWorld() {
		removeAllPlayers();
		KosmosWater.get().deleteWater();
		KosmosChunks.get().clear(false);
		this.entityPlayer.forceRemove();
		this.noise.setSeed(-1);
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		// Move qued players to the world.
		if (!playerQue.isEmpty()) {
			for (String name : new HashMap<>(playerQue).keySet()) {
				Pair<Vector3f, Vector3f> data = playerQue.get(name);
				players.put(name, new InstanceMuliplayer(FlounderEntities.get().getEntities(), data.getFirst(), data.getSecond(), name));
				playerQue.remove(name);
			}
		}

		// Update the sky colours and sun position.
		float scaledSpeed = 1.0f;

		if (FlounderGuis.get().getGuiMaster() instanceof KosmosGuis) {
			scaledSpeed = ((KosmosGuis) FlounderGuis.get().getGuiMaster()).getOverlaySlider().inStartMenu() ? 4.20f : 1.0f;
		}

		dayFactor = dayDriver.update(Framework.getDelta() * scaledSpeed) / 100.0f;
		Vector3f.rotate(LIGHT_DIRECTION, FlounderSkybox.get().getRotation().set(dayFactor * 360.0f, 0.0f, 0.0f), FlounderShadows.get().getLightPosition()).normalize();
		Colour.interpolate(SKY_COLOUR_SUNRISE, SKY_COLOUR_NIGHT, getSunriseFactor(), FlounderSkybox.get().getFog().getFogColour());
		Colour.interpolate(FlounderSkybox.get().getFog().getFogColour(), SKY_COLOUR_DAY, getShadowFactor(), FlounderSkybox.get().getFog().getFogColour());
		FlounderSkybox.get().getFog().setFogDensity(0.023f + ((1.0f - getShadowFactor()) * 0.016f));
		FlounderSkybox.get().getFog().setFogGradient(2.80f - ((1.0f - getShadowFactor()) * 0.5f));
		FlounderSkybox.get().setBlendFactor(starIntensity());
		FlounderShadows.get().setShadowBoxOffset(10.0f);
		FlounderShadows.get().setShadowBoxDistance(35.0f);
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		FlounderProfiler.get().add(getTab(), "Seed", noise.getSeed());
	}

	public PerlinNoise getNoise() {
		return this.noise;
	}

	public Entity getPlayer(String username) {
		return this.players.get(username);
	}

	public boolean containsPlayer(String username) {
		return this.players.containsKey(username);
	}

	public synchronized void quePlayer(String username, Vector3f position, Vector3f rotation) {
		this.playerQue.put(username, new Pair<>(position, rotation));
	}

	public synchronized void movePlayer(String username, float x, float y, float z, float w, float chunkX, float chunkZ) {
		if (FlounderNetwork.get().getUsername().equals(username)) {
			return;
		}

		if (!this.players.containsKey(username)) {
			if (!this.playerQue.containsKey(username)) {
				this.playerQue.put(username, new Pair<>(new Vector3f(x, y, z), new Vector3f(0.0f, w, 0.0f)));
			} else {
				this.playerQue.get(username).getFirst().set(x, y, z);
				this.playerQue.get(username).getSecond().set(0.0f, w, 0.0f);
			}

			return;
		}

		((ComponentMultiplayer) this.players.get(username).getComponent(ComponentMultiplayer.class)).move(x, y, z, w, chunkX, chunkZ);
	}

	public synchronized void removePlayer(String username) {
		if (this.playerQue.containsKey(username)) {
			this.playerQue.remove(username);
		}

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

		this.playerQue.clear();
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
		return (KosmosWorld) Framework.getInstance(KosmosWorld.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Kosmos World";
	}
}
