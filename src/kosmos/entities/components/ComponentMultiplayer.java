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
import flounder.helpers.*;
import kosmos.world.*;

import javax.swing.*;

public class ComponentMultiplayer extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private String username;

	/**
	 * Creates a new ComponentMultiplayer.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentMultiplayer(Entity entity) {
		this(entity, null);
	}

	/**
	 * Creates a new ComponentMultiplayer.
	 *
	 * @param entity The entity this component is attached to.
	 * @param username
	 */
	public ComponentMultiplayer(Entity entity, String username) {
		super(entity, ID);
		this.username = username;
	}

	@Override
	public void update() {
		getEntity().setMoved();
	}

	public void move(float x, float y, float z, float w) {
		getEntity().getPosition().set(x, y, z);
		getEntity().getRotation().set(0.0f, w, 0.0f);
		getEntity().setMoved();
	}

	public String getUsername() {
		return username;
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
		KosmosWorld.removePlayer(username);
	}
}
