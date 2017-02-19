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
	private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "bush", "bush.obj")).create();
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "bush", "bush.png")).create();

	public InstanceBush(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);

		ComponentModel componentModel = new ComponentModel(this, model, 1.0f, texture, 1);
	}
}
