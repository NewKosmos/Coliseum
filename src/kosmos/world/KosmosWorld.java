/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.world;

import flounder.camera.*;
import flounder.entities.*;
import flounder.events.*;
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
import kosmos.camera.*;
import kosmos.entities.components.*;
import kosmos.entities.instances.*;
import kosmos.world.chunks.*;
import kosmos.world.water.*;

import java.util.*;

public class KosmosWorld extends Module {
	public static final float GRAVITY = -32.0f;
	public static final Colour SKY_COLOUR_NIGHT = new Colour(0.05f, 0.05f, 0.1f);
	public static final Colour SKY_COLOUR_SUNRISE = new Colour(0.9f, 0.3f, 0.3f);
	public static final Colour SKY_COLOUR_DAY = new Colour(0.0f, 0.3f, 0.7f);
	public static final Colour SUN_COLOUR_NIGHT = new Colour(0.0f, 0.0f, 0.0f);
	public static final Colour SUN_COLOUR_SUNRISE = new Colour(0.9f, 0.3f, 0.3f);
	public static final Colour SUN_COLOUR_DAY = new Colour(1.0f, 1.0f, 1.0f);
	public static final Colour MOON_COLOUR_NIGHT = new Colour(0.4f, 0.4f, 0.6f);
	public static final Colour MOON_COLOUR_DAY = new Colour(0.0f, 0.0f, 0.0f);
	private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.2f, 0.0f, 0.5f); // The starting light direction.
	private MyFile[] SKYBOX_TEXTURE_FILES = {
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsRight.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsLeft.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsTop.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsBottom.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsBack.png"),
			new MyFile(FlounderSkybox.SKYBOX_FOLDER, "starsFront.png")
	};
	private WorldDefinition worldDefinition;

	private Map<String, Entity> players;

	private Entity entityPlayer;
	private Entity entitySun;
	private Entity entityMoon;

	private LinearDriver dayDriver;
	private float dayFactor;

	public KosmosWorld() {
		super(FlounderEntities.class, KosmosChunks.class, KosmosWater.class);
	}

	@Module.Instance
	public static KosmosWorld get() {
		return (KosmosWorld) Framework.get().getInstance(KosmosWorld.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.worldDefinition = null;

		this.players = new HashMap<>();

		this.entityPlayer = null;
		this.entitySun = new InstanceSun(FlounderEntities.get().getEntities(), new Vector3f(-250.0f, -250.0f, -250.0f), new Vector3f(0.0f, 0.0f, 0.0f));
		this.entityMoon = new InstanceMoon(FlounderEntities.get().getEntities(), new Vector3f(200.0f, 250.0f, 220.0f), new Vector3f(0.0f, 0.0f, 0.0f));

		this.dayDriver = new LinearDriver(0.0f, 100.0f, 100.0f);
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

		// Quickly save the world every so often.
		FlounderEvents.get().addEvent(new EventTime(200.0f, true) {
			@Override
			public void onEvent() {
				if (worldDefinition != null) {
					worldDefinition.save();
				}
			}
		});
	}

	public void generateWorld(WorldDefinition world, Vector3f positionPlayer, Vector3f positionChunk) {
		FlounderTasks.get().addTask(() -> {
			// Sets the seed.
			if (world != null) {
				setWorld(world);
			}

			if (FlounderNetwork.get().getSocketServer() == null) {
				// Creates the player.
				entityPlayer = new InstancePlayer(FlounderEntities.get().getEntities(), positionPlayer, new Vector3f());

				// Creates the current chunk.
				KosmosChunks.get().setCurrent(new Chunk(FlounderEntities.get().getEntities(), positionChunk));
			}

			// Creates the water.
			KosmosWater.get().generateWater();
		});
	}

	public void deleteWorld(boolean save) {
		if (save && worldDefinition != null) {
			worldDefinition.save();
		}

		KosmosConfigs.saveAllConfigs();

		FlounderEvents.get().addEvent(new EventTime(0.4f, false) {
			@Override
			public void onEvent() {
				if (worldDefinition != null) {
					worldDefinition.dispose();
					worldDefinition = null;
				}

				if (entityPlayer != null) {
					entityPlayer.forceRemove();
				}

				clearPlayers();

				KosmosChunks.get().clear(false);
				KosmosWater.get().deleteWater();

				System.gc();
			}
		});
	}

	public void clearPlayers() {
		for (String username : this.players.keySet()) {
			Entity otherPlayer = this.players.get(username);
			otherPlayer.forceRemove();
		}

		this.players.clear();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		// Update the sky colours and sun position.
		if (FlounderSkybox.get() != null && FlounderShadows.get() != null) {
			dayFactor = dayDriver.update(Framework.get().getDelta()) / 100.0f;
			// TODO: Use 'worldDefinition.getDayNightRatio()'!
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

	public float getSunriseFactor() {
		return (float) Maths.clamp(-(Math.sin(2.0 * Math.PI * getDayFactor()) - 1.0) / 2.0f, 0.0, 1.0);
	}

	public float getDayFactor() {
		return dayFactor;
	}

	public float getShadowFactor() {
		return (float) Maths.clamp(1.7f * Math.sin(2.0f * Math.PI * getDayFactor()), 0.0, 1.0);
	}

	public float starIntensity() {
		float addedIntensity = 0.0f;

		if (FlounderGuis.get().getGuiMaster() instanceof KosmosGuis) {
			addedIntensity = ((KosmosGuis) FlounderGuis.get().getGuiMaster()).getOverlaySlider().inStartMenu() ? 0.5f : 0.0f;
		}

		return Maths.clamp(1.0f - getShadowFactor() + addedIntensity, 0.0f, 1.0f);
	}

	public WorldDefinition getWorld() {
		return worldDefinition;
	}

	public void setWorld(WorldDefinition world) {
		if (this.worldDefinition != null) {
			this.worldDefinition.save();
			this.worldDefinition.dispose();
		}

		this.worldDefinition = world;

		if (worldDefinition != null) {
			this.dayDriver = new LinearDriver(0.0f, 100.0f, worldDefinition.getDayNightCycle());
			this.worldDefinition.generateMap();
		}

		KosmosChunks.get().clear(true);
	}

	public TextureObject getMapTexture() {
		if (worldDefinition == null) {
			return null;
		}

		return worldDefinition.getTextureMap();
	}

	public Map<String, Entity> getPlayers() {
		return this.players;
	}

	public boolean containsPlayer(String username) {
		return this.players.containsKey(username);
	}

	public Entity getPlayer(String username) {
		return this.players.get(username);
	}

	public void addPlayer(String username, Vector3f position, Vector3f rotation) {
		FlounderTasks.get().addTask(() -> players.put(username, new InstanceMuliplayer(FlounderEntities.get().getEntities(), position, rotation, username)));
	}

	public void updatePlayer(String username, float x, float y, float z, float w, float chunkX, float chunkZ) {
		if (FlounderNetwork.get().getUsername().equals(username)) {
			return;
		}

		if (!this.players.containsKey(username)) {
			return;
		}

		((ComponentMultiplayer) this.players.get(username).getComponent(ComponentMultiplayer.class)).move(x, y, z, w, chunkX, chunkZ);
	}

	public void removePlayer(String username) {
		if (this.players.containsKey(username)) {
			Entity otherPlayer = this.players.get(username);
			otherPlayer.forceRemove();
			this.players.remove(username);
		}
	}

	public Entity getEntityPlayer() {
		return this.entityPlayer;
	}

	public void askSendData() {
		((KosmosPlayer) FlounderCamera.get().getPlayer()).askSendData();
	}

	public Entity getEntityMoon() {
		return entityMoon;
	}

	public float getSunHeight() {
		float addedHeight = 0.0f;

		if (FlounderGuis.get().getGuiMaster() instanceof KosmosGuis) {
			addedHeight = ((KosmosGuis) FlounderGuis.get().getGuiMaster()).getOverlaySlider().inStartMenu() ? 500.0f : 0.0f;
		}

		return getEntitySun().getPosition().getY() + addedHeight;
	}

	public Entity getEntitySun() {
		return this.entitySun;
	}

	@Override
	public Module getInstance() {
		return this;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		if (worldDefinition != null) {
			worldDefinition.save();
			worldDefinition.dispose();
		}
	}
}
