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

public class InstanceCloud extends Entity {
	private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cloud", "cloud.obj")).create();
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cloud", "cloud.png")).clampEdges().create();

	public InstanceCloud(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation, float scale) {
		super(structure, position, rotation);
		ComponentCloud componentCloud = new ComponentCloud(this);
		ComponentModel componentModel = new ComponentModel(this, model, scale, texture, 0);
		ComponentSurface componentSurface = new ComponentSurface(this, 1.0f, 0.0f, false, false);
		//ComponentCollider componentCollider = new ComponentCollider(this);
		//ComponentCollision componentCollision = new ComponentCollision(this);
	}
}
