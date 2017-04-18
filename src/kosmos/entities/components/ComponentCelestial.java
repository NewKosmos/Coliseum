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
import flounder.shadows.*;
import kosmos.world.*;

import javax.swing.*;

public class ComponentCelestial extends IComponentEntity implements IComponentEditor {
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
		super(entity);

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
		getEntity().getPosition().set(FlounderShadows.getLightPosition());
		Vector3f.multiply(getEntity().getPosition(), startPosition, getEntity().getPosition());

		if (FlounderCamera.getCamera() != null) {
			Vector3f.add(getEntity().getPosition(), FlounderCamera.getCamera().getPosition(), getEntity().getPosition());
		}

		getEntity().setMoved();

		if (sunsetColours) {
			ComponentLight componentLight = (ComponentLight) getEntity().getComponent(ComponentLight.class);

			if (componentLight != null) {
				Colour.interpolate(KosmosWorld.SUN_COLOUR_SUNRISE, KosmosWorld.SUN_COLOUR_NIGHT, KosmosWorld.getSunriseFactor(), componentLight.getLight().getColour());
				Colour.interpolate(componentLight.getLight().getColour(), KosmosWorld.SUN_COLOUR_DAY, KosmosWorld.getShadowFactor(), componentLight.getLight().getColour());
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
