/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos;

import com.flounder.devices.*;
import com.flounder.framework.*;
import com.flounder.guis.*;
import com.flounder.networking.*;
import com.flounder.parsing.config.*;
import com.flounder.resources.*;
import com.flounder.shadows.*;
import com.flounder.textures.*;
import com.kosmos.post.*;
import com.kosmos.world.chunks.*;
import com.kosmos.world.water.*;

import static com.flounder.platform.Constants.*;

/**
 * A class that contains a bunch of config references.
 */
public class KosmosConfigs {
	// Main configs.
	private static final Config CONFIG_MAIN = new Config(new MyFile(Framework.getRoamingFolder("kosmos"), "configs", "settings.conf"));

	public static final ConfigData MUSIC_ENABLED = CONFIG_MAIN.getData(ConfigSection.AUDIO, "musicEnabled", true);
	public static final ConfigData MUSIC_VOLUME = CONFIG_MAIN.getData(ConfigSection.AUDIO, "musicVolume", 0.5f);
	public static final ConfigData SOUND_VOLUME = CONFIG_MAIN.getData(ConfigSection.AUDIO, "soundVolume", 1.0f);

	public static final ConfigData DISPLAY_WIDTH = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayWidth", 1080, () -> FlounderDisplay.get().getWindowWidth());
	public static final ConfigData DISPLAY_HEIGHT = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayHeight", 720, () -> FlounderDisplay.get().getWindowHeight());
	public static final ConfigData DISPLAY_VSYNC = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayVSync", false, () -> FlounderDisplay.get().isVSync());
	public static final ConfigData DISPLAY_ANTIALIAS = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayAntialias", true, () -> FlounderDisplay.get().isAntialiasing());
	public static final ConfigData DISPLAY_FULLSCREEN = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayFullscreen", false, () -> FlounderDisplay.get().isFullscreen());
	public static final ConfigData FRAMEWORK_FPS_LIMIT = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "frameworkFpsLimit", 144, () -> Framework.get().getFpsLimit());
	public static final ConfigData TEXTURES_ANISOTROPY_MAX = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "texturesAnisotropyMax", 8.0f, () -> FlounderTextures.get().getAnisotropyLevel());

	public static final ConfigData WATER_COLOUR_INTENSITY = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "waterColourIntensity", 0.7f, () -> KosmosWater.get().getColourIntensity());
	public static final ConfigData WATER_REFLECTION_ENABLED = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "waterReflectionEnabled", false, () -> KosmosWater.get().reflectionsEnabled());
	public static final ConfigData WATER_REFLECTION_QUALITY = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "waterReflectionQuality", 0.3f, () -> KosmosWater.get().getReflectionQuality());
	public static final ConfigData WATER_REFLECTION_SHADOWS = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "waterReflectionShadows", true, () -> KosmosWater.get().reflectionShadows());

	public static final ConfigData BRIGHTNESS_BOOST = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "brightnessBoost", 0.1f, () -> FlounderShadows.get().getBrightnessBoost());
	public static final ConfigData SHADOWMAP_SIZE = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapSize", 8192, () -> FlounderShadows.get().getShadowSize());
	public static final ConfigData SHADOWMAP_PCF = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapPcf", 0, () -> FlounderShadows.get().getShadowPCF());
	public static final ConfigData SHADOWMAP_BIAS = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapBias", 0.001f, () -> FlounderShadows.get().getShadowBias());
	public static final ConfigData SHADOWMAP_DARKNESS = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapDarkness", 0.7f, () -> FlounderShadows.get().getShadowDarkness());
	public static final ConfigData SHADOWMAP_UNLIMITED = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapUnlimited", true, () -> FlounderShadows.get().isRenderUnlimited());

	public static final ConfigData CHUNK_DISTANCE = CONFIG_MAIN.getData(ConfigSection.GENERAL, "chunkDistance", 4, () -> KosmosChunks.get().getChunkDistance());

	public static final ConfigData POST_EFFECTS_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "effectsEnabled", true, () -> KosmosPost.get().isEffectsEnabled());
	public static final ConfigData POST_BLOOM_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "bloomEnabled", true, () -> KosmosPost.get().isBloomEnabled());
	public static final ConfigData POST_MOTIONBLUR_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "motionBlurEnabled", false, () -> KosmosPost.get().isMotionBlurEnabled());
	public static final ConfigData POST_TILTSHIFT_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "tiltShiftEnabled", true, () -> KosmosPost.get().isTiltShiftEnabled());
	public static final ConfigData POST_LENSFLARE_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "lensFlareEnabled", true, () -> KosmosPost.get().isLensFlareEnabled());
	public static final ConfigData POST_CRT_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "crtEnabled", false, () -> KosmosPost.get().isCrtEnabled());
	public static final ConfigData POST_GRAIN_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "grainEnabled", false, () -> KosmosPost.get().isGrainEnabled());

	public static final ConfigData BRANDING_ENABLED = CONFIG_MAIN.getData(ConfigSection.POST, "brandingEnabled", true, () -> KosmosPost.get().isBrandingEnabled());

	public static final ConfigData HUD_CROSSHAIR_TYPE = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "hudCrosshairType", 1); // Reference set in master overlay.
	public static final ConfigData CAMERA_FOV = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "cameraFOV", 45.0f); // Reference set in camera.
	public static final ConfigData CAMERA_SENSITIVITY = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "cameraSensitivity", 1.0f); // Reference set in camera.
	public static final ConfigData CAMERA_ANGLE = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "cameraAngle", GLFW_MOUSE_BUTTON_RIGHT); // Reference set in camera.
	public static final ConfigData CAMERA_MOUSE_LOCKED = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "cameraMouseLocked", true); // Reference set in camera.

	public static final ConfigData GUI_SCALE = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "guiScale", 1.0f, () -> FlounderGuis.get().getGuiScale());

	public static final ConfigData CLIENT_USERNAME = CONFIG_MAIN.getData(ConfigSection.CLIENT, "username", "USERNAME" + ((int) (Math.random() * 10000)), () -> FlounderNetwork.get().getUsername());

	// Server0 configs.
	private static final Config CONFIG_SERVER0 = new Config(new MyFile(Framework.getRoamingFolder("kosmos"), "servers", "server0.conf"));
	public static final ConfigData SERVER_PORT = CONFIG_SERVER0.getData(ConfigSection.SEVER, "serverPort", FlounderNetwork.DEFAULT_PORT); // Reference set in client interface.
	public static final ConfigData SERVER_IP = CONFIG_SERVER0.getData(ConfigSection.SEVER, "serverIP", "localhost"); // Reference set in client interface.

	/**
	 * Saves the configs when closing the game.
	 */
	public static void saveAllConfigs() {
		CONFIG_MAIN.save();
		CONFIG_SERVER0.save();
	}
}
