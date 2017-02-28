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
import flounder.maths.vectors.*;
import kosmos.world.*;

import javax.swing.*;

public class ComponentCelestial extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private Vector3f startPosition;
	private Vector3f startRotation;

	/**
	 * Creates a new ComponentCelestial.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentCelestial(Entity entity) {
		super(entity, ID);

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
		getEntity().getPosition().set(KosmosWorld.getSkyCycle().getSunEntityDirection());
		Vector3f.multiply(getEntity().getPosition(), startPosition, getEntity().getPosition());

		if (FlounderCamera.getCamera() != null) {
			Vector3f.add(getEntity().getPosition(), FlounderCamera.getCamera().getPosition(), getEntity().getPosition());
		}

		getEntity().setMoved();
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public String[] getSaveParameters(String entityName) {
		return new String[]{};
	}

	@Override
	public void dispose() {
	}
}
