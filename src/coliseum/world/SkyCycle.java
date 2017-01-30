package coliseum.world;

import flounder.framework.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.visual.*;

public class SkyCycle {
	private static final Colour SKY_COLOUR_DAY = new Colour(0.0f, 0.498f, 1.0f);
	private static final Colour SKY_COLOUR_SUNRISE = new Colour(0.9921f, 0.490f, 0.004f);
	private static final Colour SKY_COLOUR_NIGHT = new Colour(0.01f, 0.01f, 0.01f);

	private static final Vector3f SUN_POSITION = new Vector3f(0, 200.0f, 0);
	private static final float DAY_NIGHT_CYCLE = 60.0f; // The day/night length (sec)

	private float dayFactor;
	private Colour skyColour;
	private Vector3f lightDirection;

	public SkyCycle() {
		this.dayFactor = 0.0f;
		this.skyColour = new Colour(SKY_COLOUR_DAY);
		this.lightDirection = new Vector3f(SUN_POSITION);
	}

	public void update() {
		dayFactor = FlounderFramework.getTimeSec() / DAY_NIGHT_CYCLE;

		if (dayFactor >= DAY_NIGHT_CYCLE) {
			dayFactor -= DAY_NIGHT_CYCLE;
		}

		Colour.interpolate(SKY_COLOUR_DAY, SKY_COLOUR_NIGHT, dayFactor, skyColour);

		Vector3f.rotate(SUN_POSITION, new Vector3f(dayFactor * 360.0f, 0.0f, 0.0f), lightDirection);
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
