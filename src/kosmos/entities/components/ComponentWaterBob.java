/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.entities.components;

import flounder.entities.*;
import flounder.helpers.*;
import kosmos.water.*;

import javax.swing.*;

public class ComponentWaterBob extends IComponentEntity implements IComponentEditor {
	private float startY;

	/**
	 * Creates a new ComponentSway.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentWaterBob(Entity entity) {
		super(entity);

		if (entity != null) {
			this.startY = entity.getPosition().getY();
		} else {
			this.startY = 0.0f;
		}
	}

	@Override
	public void update() {
		if (KosmosWater.get().getWater() == null) {
			return;
		}

		float waterHeight = KosmosWater.get().getWater().getHeight(getEntity().getPosition().x, getEntity().getPosition().z);
		getEntity().getPosition().y = startY + waterHeight;
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
