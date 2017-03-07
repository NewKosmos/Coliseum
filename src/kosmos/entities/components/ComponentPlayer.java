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
import flounder.maths.vectors.*;
import kosmos.camera.*;

import javax.swing.*;

public class ComponentPlayer extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private Vector3f lastPosition;
	private Vector3f lastRotation;

	/**
	 * Creates a new ComponentPlayer.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentPlayer(Entity entity) {
		super(entity, ID);
		this.lastPosition = new Vector3f();
		this.lastRotation = new Vector3f();
	}

	@Override
	public void update() {
		if (FlounderCamera.getPlayer() == null) {
			return;
		}

		getEntity().getPosition().set(FlounderCamera.getPlayer().getPosition());
		getEntity().getPosition().y += PlayerBasic.PLAYER_OFFSET_Y;
		getEntity().getRotation().set(FlounderCamera.getPlayer().getRotation());

		if (!getEntity().getPosition().equals(lastPosition) || !getEntity().getRotation().equals(lastRotation)) {
			getEntity().setMoved();

			lastPosition.set(getEntity().getPosition());
			lastRotation.set(getEntity().getRotation());
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
		return new Pair<>(
				new String[]{}, // Static variables
				new String[]{} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}
}
