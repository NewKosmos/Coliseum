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

	private LightType lightType;

	/**
	 * Creates a new ComponentCelestial.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentCelestial(Entity entity) {
		this(entity, LightType.NONE);
	}

	/**
	 * Creates a new ComponentCelestial.
	 *
	 * @param entity The entity this component is attached to.
	 * @param lightType
	 */
	public ComponentCelestial(Entity entity, LightType lightType) {
		super(entity);

		this.lightType = lightType;

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

		if (lightType != LightType.NONE) {
			ComponentLight componentLight = (ComponentLight) getEntity().getComponent(ComponentLight.class);

			if (componentLight != null) {
				switch (lightType) {
					case SUN:
						Colour.interpolate(KosmosWorld.SUN_COLOUR_SUNRISE, KosmosWorld.SUN_COLOUR_NIGHT, KosmosWorld.getSunriseFactor(), componentLight.getLight().getColour());
						Colour.interpolate(componentLight.getLight().getColour(), KosmosWorld.SUN_COLOUR_DAY, KosmosWorld.getShadowFactor(), componentLight.getLight().getColour());
						break;
					case MOON:
						Colour.interpolate(KosmosWorld.MOON_COLOUR_NIGHT, KosmosWorld.MOON_COLOUR_DAY, KosmosWorld.getShadowFactor(), componentLight.getLight().getColour());
						break;
					case NONE:
						break;
				}
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
		String saveLightType = lightType.name();

		return new Pair<>(
				new String[]{}, // Static variables
				new String[]{saveLightType} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}

	public static enum LightType {
		SUN, MOON, NONE
	}
}
