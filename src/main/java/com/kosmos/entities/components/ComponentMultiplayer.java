/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.entities.components;

import com.flounder.entities.*;
import com.flounder.helpers.*;

import javax.swing.*;

public class ComponentMultiplayer extends IComponentEntity implements IComponentEditor {
	private String username;

	private float chunkX, chunkZ;

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
	 * @param username The username of this player.
	 */
	public ComponentMultiplayer(Entity entity, String username) {
		super(entity);

		this.username = username;
		this.chunkX = 0.0f;
		this.chunkZ = 0.0f;
	}

	@Override
	public void update() {
		getEntity().setMoved();
	}

	public void move(float x, float y, float z, float w, float chunkX, float chunkZ) {
		getEntity().getPosition().set(x, y, z);
		// getEntity().getPosition().y += KosmosPlayer.PLAYER_OFFSET_Y;
		getEntity().getRotation().set(0.0f, w, 0.0f);
		getEntity().setMoved();
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	public String getUsername() {
		return username;
	}

	public float getChunkX() {
		return chunkX;
	}

	public float getChunkZ() {
		return chunkZ;
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
		//	KosmosWorld.removePlayer(username);
	}
}
