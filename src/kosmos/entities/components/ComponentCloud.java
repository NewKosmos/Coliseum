/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.components;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.entities.template.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import kosmos.world.*;

import javax.swing.*;

public class ComponentCloud extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private Vector3f startPosition;

	/**
	 * Creates a new ComponentCloud.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentCloud(Entity entity) {
		super(entity, ID);

		if (entity != null) {
			this.startPosition = new Vector3f(entity.getPosition());
		} else {
			this.startPosition = new Vector3f();
		}
	}

	@Override
	public void update() {
		Vector3f.rotate(startPosition, new Vector3f(0.0f, KosmosWorld.getSkyCycle().getDayFactor() * 180.0f, 0.0f), getEntity().getPosition());
		getEntity().setMoved();
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public String[] getSavableValues(String entityName) {
		return new String[]{};
	}

	@Override
	public void dispose() {
	}
}
