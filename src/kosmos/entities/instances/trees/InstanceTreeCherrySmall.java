/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.instances.trees;

import flounder.entities.Entity;
import flounder.entities.FlounderEntities;
import flounder.maths.vectors.Vector3f;
import flounder.models.ModelFactory;
import flounder.models.ModelObject;
import flounder.resources.MyFile;
import flounder.space.ISpatialStructure;
import flounder.textures.TextureFactory;
import flounder.textures.TextureObject;
import kosmos.entities.components.*;

public class InstanceTreeCherrySmall extends Entity {
	private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "treeCherrySmall", "treeCherrySmall.obj")).create();
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "treeCherrySmall", "treeCherrySmall.png")).create();
	private static final TextureObject textureSway = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "treeCherrySmall", "treeCherrySmallSway.png")).create();

	public InstanceTreeCherrySmall(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);

		ComponentModel componentModel = new ComponentModel(this, 1.0f, model, texture, 1);
		ComponentSurface componentSurface = new ComponentSurface(this, 1.0f, 0.0f, false, false);
		ComponentSway componentSway = new ComponentSway(this, textureSway);
		ComponentCollider componentCollider = new ComponentCollider(this);
		ComponentCollision componentCollision = new ComponentCollision(this);
	}
}
