/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.components;

import flounder.camera.*;
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.helpers.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import kosmos.world.*;

import javax.swing.*;

public class ComponentCelestial extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private Vector3f startPosition;
	private Vector3f startRotation;

	private boolean sunsetColours;

	/**
	 * Creates a new ComponentCelestial.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentCelestial(Entity entity) {
		this(entity, false);
	}

	/**
	 * Creates a new ComponentCelestial.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentCelestial(Entity entity, boolean sunsetColours) {
		super(entity, ID);

		this.sunsetColours = sunsetColours;

		if (entity != null) {
			this.startPosition = new Vector3f(entity.getPosition());
			this.startRotation = new Vector3f(entity.getRotation());
		} else {
			this.startPosition = new Vector3f();
			this.startRotation = new Vector3f();
		}
	}

	@Override
	public void update() {
		getEntity().getPosition().set(KosmosWorld.getSkyCycle().getLightPosition());
		Vector3f.multiply(getEntity().getPosition(), startPosition, getEntity().getPosition());

		if (FlounderCamera.getCamera() != null) {
			Vector3f.add(getEntity().getPosition(), FlounderCamera.getCamera().getPosition(), getEntity().getPosition());
		}

		getEntity().setMoved();

		if (sunsetColours) {
			ComponentLight componentLight = (ComponentLight) getEntity().getComponent(ComponentLight.ID);

			if (componentLight != null) {
				Colour.interpolate(SkyCycle.SUN_COLOUR_SUNRISE, SkyCycle.SUN_COLOUR_DAY, KosmosWorld.getSkyCycle().getSunriseFactor(), componentLight.getLight().getColour());
				Colour.interpolate(componentLight.getLight().getColour(), SkyCycle.SUN_COLOUR_DAY, KosmosWorld.getSkyCycle().getShadowFactor(), componentLight.getLight().getColour());
				//	float daySin = (float) Math.sin(2.0 * Math.PI * KosmosWorld.getSkyCycle().getDayFactor());
				//	Colour.interpolate(SkyCycle.SUN_COLOUR_DAY, SkyCycle.SUN_COLOUR_SUNRISE, daySin, componentLight.getLight().getColour());
				//	FlounderLogger.log("["+daySin+"]: " + componentLight.getLight().colour);
				////	componentLight.getLight().getColour().set(KosmosWorld.getSkyCycle().getSkyColour());
			}
		}
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		String saveSunsetColours = sunsetColours + "";

		return new Pair<>(
				new String[]{}, // Static variables
				new String[]{saveSunsetColours} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}
}
