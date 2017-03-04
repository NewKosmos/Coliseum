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

public class InstanceGemPurple extends Entity {
	private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "gemPurple", "gemPurple.obj")).create();
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "gemPurple", "gemPurple.png")).create();

	public InstanceGemPurple(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);

		ComponentModel componentModel = new ComponentModel(this, 1.0f, model, texture, 1);
		ComponentSurface componentSurface = new ComponentSurface(this, 1.0f, 0.0f, false, false);
		ComponentLight componentLight = new ComponentLight(this, new Vector3f(0.0f, 1.5f, 0.0f), new Colour(0.631f, 0.243f, 0.812f), new Attenuation(1.0f, 0.02f, 2.0f));
		ComponentCollider componentCollider = new ComponentCollider(this);
		ComponentCollision componentCollision = new ComponentCollision(this);
	}
}
