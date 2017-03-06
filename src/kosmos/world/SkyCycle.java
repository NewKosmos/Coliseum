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
	private static final Colour SKY_COLOUR_NIGHT = new Colour(0.0f, 0.08f, 0.26f);
	private static final Colour SKY_COLOUR_DAY = new Colour(0.0f, 0.30f, 0.70f);

	private static final Colour SUN_COLOUR_DAY = new Colour(0.5f, 0.5f, 0.5f);
	private static final Colour SUN_COLOUR_SUNRISE = new Colour(0.9921f, 0.490f, 0.004f);

	private static final float DAY_NIGHT_CYCLE = 180.0f; // The day/night length (sec)

	private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.2f, -0.3f, -0.8f); // The starting light direction.

	private float dayFactor;
	private LinearDriver dayDriver;

	private Colour skyColour;
	private Vector3f lightDirection;
	private Vector3f sunEntityDirection;

	public SkyCycle() {
		this.dayFactor = 0.0f;
		this.dayDriver = new LinearDriver(0.0f, 100.0f, DAY_NIGHT_CYCLE);

		this.skyColour = new Colour(SKY_COLOUR_DAY);
		this.lightDirection = new Vector3f(LIGHT_DIRECTION);
		this.sunEntityDirection = new Vector3f(LIGHT_DIRECTION);
	}

	public void update() {
		dayFactor = dayDriver.update(Framework.getDelta()) / 100.0f;
		Colour.interpolate(SKY_COLOUR_DAY, SKY_COLOUR_NIGHT, getSinDay(), skyColour);
		Vector3f.rotate(LIGHT_DIRECTION, new Vector3f(dayFactor * 360.0f, 0.0f, 0.0f), lightDirection);
		Vector3f.rotate(LIGHT_DIRECTION, new Vector3f(dayFactor * 360.0f, 0.0f, 0.0f), sunEntityDirection);
	}

	public float getDayFactor() {
		return dayFactor;
	}

	public float getSinDay() {
		return (float) Math.sin(Math.PI * KosmosWorld.getSkyCycle().getDayFactor());
	}

	public Colour getSkyColour() {
		return skyColour;
	}

	public Vector3f getLightDirection() {
		return lightDirection;
	}

	public Vector3f getSunEntityDirection() {
		return sunEntityDirection;
	}
}
