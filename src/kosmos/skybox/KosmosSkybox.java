/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.skybox;

import flounder.camera.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.lights.*;
import flounder.loaders.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.world.*;

public class KosmosSkybox extends Module {
	private static final KosmosSkybox INSTANCE = new KosmosSkybox();
	public static final String PROFILE_TAB_NAME = "Kosmos Skybox";

	public static final MyFile SKYBOX_FOLDER = new MyFile(MyFile.RES_FOLDER, "skybox");

	private static MyFile[] TEXTURE_FILES = {
			new MyFile(SKYBOX_FOLDER, "starsRight.png"),
			new MyFile(SKYBOX_FOLDER, "starsLeft.png"),
			new MyFile(SKYBOX_FOLDER, "starsTop.png"),
			new MyFile(SKYBOX_FOLDER, "starsBottom.png"),
			new MyFile(SKYBOX_FOLDER, "starsBack.png"),
			new MyFile(SKYBOX_FOLDER, "starsFront.png")
	};

	public static final Colour SKY_COLOUR_NIGHT = new Colour(0.0f, 0.07f, 0.19f);
	public static final Colour SKY_COLOUR_SUNRISE = new Colour(0.713f, 0.494f, 0.356f);
	public static final Colour SKY_COLOUR_DAY = new Colour(0.0f, 0.30f, 0.70f);

	public static final Colour SUN_COLOUR_NIGHT = new Colour(0.0f, 0.0f, 0.0f);
	public static final Colour SUN_COLOUR_SUNRISE = new Colour(0.713f, 0.494f, 0.356f);
	public static final Colour SUN_COLOUR_DAY = new Colour(0.7f, 0.7f, 0.7f);

	public static final Colour MOON_COLOUR = new Colour(0.1f, 0.1f, 0.19f);

	public static final float DAY_NIGHT_CYCLE = 1200.0f; // The day/night length (sec).

	private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.3f, 0.0f, 0.5f); // The starting light direction.

	private TextureObject cubemap;
	private Matrix4f modelMatrix;

	private Fog fog;
	private LinearDriver dayDriver;
	private float dayFactor;
	private Colour skyColour;
	private Vector3f lightRotation;
	private Vector3f lightPosition;

	public KosmosSkybox() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderLoader.class, FlounderTextures.class, FlounderShaders.class);
	}

	@Override
	public void init() {
		this.cubemap = TextureFactory.newBuilder().setCubemap(TEXTURE_FILES).create();
		this.modelMatrix = new Matrix4f();

		this.fog = new Fog(new Colour(), 0.025f, 2.0f, 0.0f, 50.0f);
		this.dayDriver = new LinearDriver(0.0f, 100.0f, DAY_NIGHT_CYCLE);
		this.dayFactor = 0.0f;
		this.skyColour = new Colour(SKY_COLOUR_DAY);
		this.lightRotation = new Vector3f();
		this.lightPosition = new Vector3f(LIGHT_DIRECTION);
	}

	@Override
	public void update() {
		// Update the skybox transformation.
		if (FlounderCamera.getCamera() != null) {
			Matrix4f.transformationMatrix(FlounderCamera.getCamera().getPosition(), getLightRotation(), 1.0f, modelMatrix);
		}

		// Update the sky colours and sun position.
		float scaledSpeed = 0.0f;

		if (FlounderGuis.getGuiMaster() instanceof KosmosGuis) {
			scaledSpeed = ((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlaySlider().inStartMenu() ? 10.0f : 2.0f;
		}

		dayFactor = dayDriver.update(Framework.getDelta() * scaledSpeed) / 100.0f;
		Colour.interpolate(SKY_COLOUR_SUNRISE, SKY_COLOUR_NIGHT, getSunriseFactor(), skyColour);
		Colour.interpolate(skyColour, SKY_COLOUR_DAY, getShadowFactor(), skyColour);
		Vector3f.rotate(LIGHT_DIRECTION, lightRotation.set(dayFactor * 360.0f, 0.0f, 0.0f), lightPosition);
		fog.setFogColour(skyColour);
	}

	@Override
	public void profile() {
	}

	public static TextureObject getCubemap() {
		return INSTANCE.cubemap;
	}

	public static Matrix4f getModelMatrix() {
		return INSTANCE.modelMatrix;
	}

	public static Fog getFog() {
		return INSTANCE.fog;
	}

	public static void setDayDriver(LinearDriver dayDriver) {
		INSTANCE.dayDriver = dayDriver;
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
		return 0.73f; // 0.8f * (getShadowFactor()) + 0.2f; // TODO
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
		this.cubemap.delete();
	}
}
