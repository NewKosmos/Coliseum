/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.instances;

import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.entities.components.*;

public class InstanceBush extends Entity {
	private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "bushBerry", "bush.obj")).create();
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "bushBerry", "bush.png")).create();

	public InstanceBush(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);

		ComponentModel componentModel = new ComponentModel(this, model, 1.0f, texture, 1);
		ComponentCollider componentCollider = new ComponentCollider(this);
		ComponentCollision componentCollision = new ComponentCollision(this);

		// new InstanceBushBerry(structure, new Vector3f(position.x, position.y + 0.45f, position.z), rotation);
	}

	public static class InstanceBushBerry extends Entity {
		private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "bushBerry", "berry.obj")).create();
		private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "bushBerry", "berry.png")).create();

		public InstanceBushBerry(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
			super(structure, position, rotation);

			ComponentModel componentModel = new ComponentModel(this, model, 1.0f, texture, 1);
			ComponentCollider componentCollider = new ComponentCollider(this);
			ComponentCollision componentCollision = new ComponentCollision(this);
		}
	}
}
