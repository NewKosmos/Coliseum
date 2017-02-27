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
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.entities.components.*;

public class InstanceMuliplayer extends Entity {
	private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cowboy", "cowboy.obj")).create();
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cowboy", "cowboy.png")).create();

	public InstanceMuliplayer(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation, String username) {
		super(structure, position, rotation);

		ComponentMultiplayer componentMultiplayer = new ComponentMultiplayer(this, username);
		ComponentSurface componentSurface = new ComponentSurface(this, 1.0f, 0.0f, false, false);
		ComponentLight componentLight = new ComponentLight(this, new Vector3f(0.0f, 2.0f, 0.0f), new Colour(0.9f, 0.8f, 0.8f), new Attenuation(1.0f, 0.02f, 1.0f));
		ComponentModel componentModel = new ComponentModel(this, model, 0.2f, texture, 1);
		ComponentCollider componentCollider = new ComponentCollider(this);
		ComponentCollision componentCollision = new ComponentCollision(this);
	}
}
