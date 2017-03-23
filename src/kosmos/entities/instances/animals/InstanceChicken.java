/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.instances.animals;

import flounder.entities.Entity;
import flounder.entities.FlounderEntities;
import flounder.lights.Attenuation;
import flounder.maths.Colour;
import flounder.maths.vectors.Vector3f;
import flounder.resources.MyFile;
import flounder.space.ISpatialStructure;
import flounder.textures.TextureFactory;
import flounder.textures.TextureObject;
import kosmos.entities.components.ComponentAnimation;
import kosmos.entities.components.ComponentLight;
import kosmos.entities.components.ComponentSurface;

public class InstanceChicken extends Entity {
	private static final MyFile colladaFile = new MyFile(FlounderEntities.ENTITIES_FOLDER, "chicken", "chickenIdle.dae");
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "chicken", "chicken.png")).create();

	public InstanceChicken(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);

		new ComponentAnimation(this, colladaFile, 0.2f, texture, 1);
		new ComponentSurface(this, 1.0f, 0.0f, false, false);
//		new ComponentLight(this, new Vector3f(0.0f, 2.0f, 0.0f), new Colour(1.0f, 1.0f, 1.0f), new Attenuation(1.0f, 0.02f, 0.5f));
		//	ComponentCollider componentCollider = new ComponentCollider(this);
		//	ComponentCollision componentCollision = new ComponentCollision(this);
	}
}
