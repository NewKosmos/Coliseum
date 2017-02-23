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
	private static final Colour SKY_COLOUR_DAY = new Colour(0.0f, 0.498f, 1.0f);
	private static final Colour SKY_COLOUR_SUNRISE = new Colour(0.9921f, 0.490f, 0.004f);
	private static final Colour SKY_COLOUR_NIGHT = new Colour(0.0f, 0.0f, 0.0f);

	private static final float DAY_NIGHT_CYCLE = 103.3f; // The day/night length (sec)

	private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.2f, -0.3f, -0.8f); // The starting light direction.

	private Vector3f sunPosition;

	private float dayFactor;
	private LinearDriver dayDriver;

	private Colour skyColour;
	private Vector3f lightDirection;

	public SkyCycle() {
		this.sunPosition = new Vector3f(LIGHT_DIRECTION);

		this.dayFactor = 0.0f;
		this.dayDriver = new LinearDriver(0.0f, 100.0f, DAY_NIGHT_CYCLE);

		this.skyColour = new Colour(SKY_COLOUR_DAY);
		this.lightDirection = new Vector3f(sunPosition);
	}

	public void update() {
		dayFactor = dayDriver.update(Framework.getDelta()) / 100.0f;
		//	Colour.interpolate(SKY_COLOUR_DAY, SKY_COLOUR_NIGHT, dayFactor, skyColour);
		Vector3f.rotate(LIGHT_DIRECTION, new Vector3f(0.0f, dayFactor * 360.0f, 0.0f), lightDirection);
	}

	public float getDayFactor() {
		return dayFactor;
	}

	public Colour getSkyColour() {
		return skyColour;
	}

	public Vector3f getLightDirection() {
		return lightDirection;
	}
}
