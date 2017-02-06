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
import flounder.maths.vectors.*;
import flounder.physics.*;
import kosmos.world.*;

public class ComponentCloud extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private Vector3f startPosition;

	public ComponentCloud(Entity entity) {
		super(entity, ID);
		this.startPosition = new Vector3f(entity.getPosition());
	}

	@Override
	public void update() {
		Vector3f.rotate(startPosition, new Vector3f(0.0f, KosmosWorld.getSkyCycle().getDayFactor() * 180.0f, 0.0f), getEntity().getPosition());
	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {

	}
}
