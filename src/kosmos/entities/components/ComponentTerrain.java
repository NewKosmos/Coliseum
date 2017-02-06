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
import flounder.physics.*;
import kosmos.chunks.*;

public class ComponentTerrain extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private Chunk chunk;

	public ComponentTerrain(Entity entity, Chunk chunk) {
		super(entity, ID);
		this.chunk = chunk;
	}

	public ComponentTerrain(Entity entity, EntityTemplate template) {
		super(entity, ID);
		this.chunk = null;
	}

	@Override
	public void update() {
	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	public float getDarkness() {
		return chunk.getDarkness();
	}

	@Override
	public void dispose() {
	}
}
