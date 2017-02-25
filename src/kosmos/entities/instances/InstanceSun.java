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

public class InstanceSun extends Entity {
	private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "sun", "sun.obj")).create();
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "sun", "sun.png")).clampEdges().create();

	public InstanceSun(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		ComponentCelestial componentCelestial = new ComponentCelestial(this);
		ComponentModel componentModel = new ComponentModel(this, model, 30.0f, texture, 0);
		ComponentLighting componentLighting = new ComponentLighting(this, 1.0f, 0.0f, true, true);
		//ComponentCollider componentCollider = new ComponentCollider(this);
		// ComponentCollision componentCollision = new ComponentCollision(this);
	}
}
