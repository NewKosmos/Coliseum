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
import flounder.framework.*;
import flounder.helpers.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.visual.*;

import java.util.*;

public class ComponentMultiplayer extends IComponentEntity {
	public static final Map<String, ComponentMultiplayer> players = new HashMap<>();

	public static final int ID = EntityIDAssigner.getId();

	private String username;

	public ComponentMultiplayer(Entity entity, String username) {
		super(entity, ID);
		this.username = username;

		players.put(username, this);
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
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {
		//	players.remove(username);
	}
}
