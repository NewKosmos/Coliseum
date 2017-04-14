/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos;

import flounder.devices.*;
import flounder.framework.*;
import flounder.maths.*;
import flounder.networking.*;
import flounder.parsing.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.shadows.*;
import flounder.textures.*;
import kosmos.post.*;
import kosmos.water.*;
import org.lwjgl.glfw.*;

/**
 * A class that contains a bunch of config references.
 */
public class KosmosConfigs {
	// Main configs.
	private static final Config CONFIG_MAIN = new Config(new MyFile(Framework.getRoamingFolder(), "configs", "settings.conf"));
	public static final ConfigData PROFILER_OPEN = CONFIG_MAIN.getData(ConfigSection.DEBUG, "profilerOpen", false, FlounderProfiler::isOpen);
	public static final ConfigData BOUNDINGS_RENDER = CONFIG_MAIN.getData(ConfigSection.DEBUG, "boundingsRender", false, FlounderBounding::renders);

	public static final ConfigData MUSIC_ENABLED = CONFIG_MAIN.getData(ConfigSection.AUDIO, "musicEnabled", true);
	public static final ConfigData MUSIC_VOLUME = CONFIG_MAIN.getData(ConfigSection.AUDIO, "musicVolume", 0.5f);
	public static final ConfigData SOUND_VOLUME = CONFIG_MAIN.getData(ConfigSection.AUDIO, "soundVolume", 1.0f);

	public static final ConfigData DISPLAY_WIDTH = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayWidth", 1080, FlounderDisplay::getWindowWidth);
	public static final ConfigData DISPLAY_HEIGHT = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayHeight", 720, FlounderDisplay::getWindowHeight);
	public static final ConfigData DISPLAY_VSYNC = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayVSync", false, FlounderDisplay::isVSync);
	public static final ConfigData DISPLAY_ANTIALIAS = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayAntialias", true, FlounderDisplay::isAntialiasing);
	public static final ConfigData DISPLAY_FULLSCREEN = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayFullscreen", false, FlounderDisplay::isFullscreen);
	public static final ConfigData FRAMEWORK_FPS_LIMIT = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "frameworkFpsLimit", 144, Framework::getFpsLimit);
	public static final ConfigData TEXTURES_ANISOTROPY_MAX = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "texturesAnisotropyMax", 8.0f, FlounderTextures::getAnisotropyLevel);

	public static final ConfigData WATER_COLOUR_INTENSITY = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "waterColourIntensity", 0.7f, KosmosWater::getColourIntensity);
	public static final ConfigData WATER_REFLECTION_ENABLED = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "waterReflectionEnabled", false, KosmosWater::reflectionsEnabled);
	public static final ConfigData WATER_REFLECTION_QUALITY = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "waterReflectionQuality", 0.3f, KosmosWater::getReflectionQuality);
	public static final ConfigData WATER_REFLECTION_SHADOWS = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "waterReflectionShadows", true, KosmosWater::reflectionShadows);

	public static final ConfigData BRIGHTNESS_BOOST = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "brightnessBoost", 0.123f, FlounderShadows::getBrightnessBoost);
	public static final ConfigData SHADOWMAP_SIZE = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapSize", 8192, FlounderShadows::getShadowSize);
	public static final ConfigData SHADOWMAP_PCF = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapPcf", 0, FlounderShadows::getShadowPCF);
	public static final ConfigData SHADOWMAP_BIAS = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapBias", 0.001f, FlounderShadows::getShadowBias);
	public static final ConfigData SHADOWMAP_DARKNESS = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapDarkness", 0.6f, FlounderShadows::getShadowDarkness);

	public static final ConfigData POST_EFFECTS_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "effectsEnabled", true, KosmosPost::isEffectsEnabled);
	public static final ConfigData POST_BLOOM_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "bloomEnabled", true, KosmosPost::isBloomEnabled);
	public static final ConfigData POST_MOTIONBLUR_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "motionBlurEnabled", false, KosmosPost::isMotionBlurEnabled);
	public static final ConfigData POST_TILTSHIFT_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "tiltShiftEnabled", true, KosmosPost::isTiltShiftEnabled);
	public static final ConfigData POST_LENSFLARE_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "lensFlareEnabled", true, KosmosPost::isLensFlareEnabled);
	public static final ConfigData POST_CRT_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "crtEnabled", false, KosmosPost::isCrtEnabled);
	public static final ConfigData POST_GRAIN_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "grainEnabled", false, KosmosPost::isCrtEnabled);

	public static final ConfigData HUD_COSSHAIR_TYPE = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "hudCrosshairType", 1); // Reference set in master overlay.
	public static final ConfigData CAMERA_SENSITIVITY = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "cameraSensitivity", 1.0f); // Reference set in camera.
	public static final ConfigData CAMERA_REANGLE = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "cameraReangle", GLFW.GLFW_MOUSE_BUTTON_RIGHT); // Reference set in camera.
	public static final ConfigData CAMERA_MOUSE_LOCKED = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "cameraMouseLocked", true); // , FlounderMouse::isCursorDisabled
	public static final ConfigData CAMERA_FIRST_PERSON = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "cameraFirstPerson", false); // Reference set in camera.

	public static final ConfigData CLIENT_USERNAME = CONFIG_MAIN.getData(ConfigSection.CLIENT, "username", "USERNAME" + ((int) (Math.random() * 10000)), FlounderNetwork::getUsername);

	// Host server configs.
	private static final Config CONFIG_HOST = new Config(new MyFile(Framework.getRoamingFolder(), "configs", "host.conf"));
	public static final ConfigData HOST_PORT = CONFIG_HOST.getData(ConfigSection.SEVER, "hostPort", FlounderNetwork.getPort()); // Reference set in server interface.
	public static final ConfigData HOST_SEED = CONFIG_HOST.getData(ConfigSection.WORLD, "hostSeed", (int) Maths.randomInRange(1.0, 10000.0)); // Reference set in server interface.

	// Server0 configs.
	private static final Config CONFIG_SERVER0 = new Config(new MyFile(Framework.getRoamingFolder(), "servers", "server0.conf"));
	public static final ConfigData SERVER_PORT = CONFIG_SERVER0.getData(ConfigSection.SEVER, "serverPort", FlounderNetwork.DEFAULT_PORT); // Reference set in client interface.
	public static final ConfigData SERVER_IP = CONFIG_SERVER0.getData(ConfigSection.SEVER, "serverIP", "localhost"); // Reference set in client interface.

	// Save0 configs.
	private static final Config CONFIG_SAVE0 = new Config(new MyFile(Framework.getRoamingFolder(), "saves", "save0.conf"));
	public static final ConfigData SAVE_SEED = CONFIG_SAVE0.getData(ConfigSection.WORLD, "saveSeed", (int) Maths.randomInRange(1.0, 10000.0)); // Reference set in world.
	public static final ConfigData SAVE_PLAYER_X = CONFIG_SAVE0.getData(ConfigSection.WORLD, "playerX", 0.0f); // Reference set in world.
	public static final ConfigData SAVE_PLAYER_Y = CONFIG_SAVE0.getData(ConfigSection.WORLD, "playerY", 0.0f); // Reference set in world.
	public static final ConfigData SAVE_PLAYER_Z = CONFIG_SAVE0.getData(ConfigSection.WORLD, "playerZ", 0.0f); // Reference set in world.
	public static final ConfigData SAVE_CHUNK_X = CONFIG_SAVE0.getData(ConfigSection.WORLD, "chunkX", 0.0f); // Reference set in chunks.
	public static final ConfigData SAVE_CHUNK_Z = CONFIG_SAVE0.getData(ConfigSection.WORLD, "chunkZ", 0.0f); // Reference set in chunks.

	/**
	 * Saves the configs when closing the game.
	 */
	public static void saveAllConfigs() {
		CONFIG_MAIN.save();
		CONFIG_HOST.save();
		CONFIG_SERVER0.save();
		CONFIG_SAVE0.save();
	}
}
