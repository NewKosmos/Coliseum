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
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import kosmos.chunks.*;

public class ComponentPlayer extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private Vector3f startPosition;
	private Vector3f startRotation;

	public ComponentPlayer(Entity entity) {
		super(entity, ID);
		this.startPosition = new Vector3f(entity.getPosition());
		this.startRotation = new Vector3f(entity.getRotation());
	}

	@Override
	public void update() {
		if (FlounderCamera.getPlayer() == null) {
			return;
		}

		getEntity().getPosition().set(FlounderCamera.getPlayer().getPosition());
		Vector3f.add(getEntity().getPosition(), startPosition, getEntity().getPosition());

		getEntity().getRotation().set(FlounderCamera.getPlayer().getRotation());
		Vector3f.add(getEntity().getRotation(), startRotation, getEntity().getRotation());

		/*for (Chunk chunk : KosmosChunks.getChunks()) {
			if (chunk.getBounding() != null && (chunk.getBounding().contains(getEntity().getBounding()) || chunk.getBounding().intersects((AABB) getEntity().getBounding()).isIntersection())) {
				FlounderLogger.log("Player in chunk: " + chunk.toString());
				return;
			}
		}*/
	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {

	}
}
