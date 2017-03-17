/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.world;

import flounder.framework.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.visual.*;

public class SkyCycle {
	public static final Colour SKY_COLOUR_NIGHT = new Colour(0.0f, 0.07f, 0.19f);
	public static final Colour SKY_COLOUR_SUNRISE = new Colour(0.9921f, 0.490f, 0.004f);
	public static final Colour SKY_COLOUR_DAY = new Colour(0.0f, 0.30f, 0.70f);

	public static final Colour SUN_COLOUR_SUNRISE = new Colour(0.9921f, 0.490f, 0.004f);
	public static final Colour SUN_COLOUR_DAY = new Colour(0.64f, 0.64f, 0.64f);

	public static final Colour MOON_COLOUR = new Colour(0.32f, 0.32f, 0.32f);

	public static final float DAY_NIGHT_CYCLE = 160.0f; // The day/night length (sec).

	private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.5f, 0.0f, 0.5f); // The starting light direction.

	private LinearDriver dayDriver;
	private float dayFactor;

	private Colour skyColour;
	private Vector3f lightPosition;

	public SkyCycle() {
		this.dayDriver = new LinearDriver(0.0f, 100.0f, DAY_NIGHT_CYCLE);
		this.dayFactor = 0.0f;

		this.skyColour = new Colour(SKY_COLOUR_DAY);
		this.lightPosition = new Vector3f(LIGHT_DIRECTION);
	}

	public void update() {
		dayFactor = 0.2f; // dayDriver.update(Framework.getDelta()) / 100.0f; // 0.52f
		Colour.interpolate(SKY_COLOUR_SUNRISE, SKY_COLOUR_NIGHT, getSunriseFactor(), skyColour);
		Colour.interpolate(skyColour, SKY_COLOUR_DAY, getShadowFactor(), skyColour);
		Vector3f.rotate(LIGHT_DIRECTION, new Vector3f(dayFactor * 360.0f, 0.0f, 0.0f), lightPosition);
	}

	public float getDayFactor() {
		return dayFactor;
	}

	public float getSunriseFactor() {
		return (float) -(Math.sin(2.0 * Math.PI * KosmosWorld.getSkyCycle().getDayFactor()) - 1.0) / 2.0f;
	}

	public float getShadowFactor() {
		return (float) Maths.clamp(1.7f * Math.sin(2.0f * Math.PI * KosmosWorld.getSkyCycle().getDayFactor()), 0.0, 1.0);
	}

	public Colour getSkyColour() {
		return skyColour;
	}

	public Vector3f getLightPosition() {
		return lightPosition;
	}
}
