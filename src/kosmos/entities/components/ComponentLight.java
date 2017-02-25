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
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.*;

public class ComponentLight extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private Vector3f offset;
	private Light light;

	public ComponentLight(Entity entity, Vector3f offset, Light light) {
		super(entity, ID);
		this.offset = offset;
		this.light = light;
	}

	@Override
	public void update() {
		Vector3f.add(super.getEntity().getPosition(), offset, light.getPosition());
	}

	public Light getLight() {
		return light;
	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {

	}
}
