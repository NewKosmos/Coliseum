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
import flounder.framework.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.visual.*;

import javax.swing.*;

public class ComponentCloud extends IComponentEntity implements IComponentEditor {
	private Vector3f startPosition;

	private SinWaveDriver driverHeight;

	/**
	 * Creates a new ComponentChild.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentCloud(Entity entity) {
		super(entity);

		if (entity != null) {
			this.startPosition = new Vector3f(entity.getPosition());
			float variation = Math.abs(2.0f - entity.getPosition().y);
			this.driverHeight = new SinWaveDriver(-variation, variation, 30.0f * variation);
		} else {
			this.startPosition = new Vector3f();
			this.driverHeight = new SinWaveDriver(-2.0f, 3.0f, 30.0f);
		}
	}

	@Override
	public void update() {
		//Vector3f.rotate(startPosition, new Vector3f(0.0f, KosmosWorld.getSkyCycle().getDayFactor() * 180.0f, 0.0f), getEntity().getPosition());
		float height = driverHeight.update(Framework.getDelta());
		getEntity().getPosition().y = startPosition.y + height;
		getEntity().setMoved();
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		return new Pair<>(
				new String[]{}, // Static variables
				new String[]{} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}
}
