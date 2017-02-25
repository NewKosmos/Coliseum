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

public class InstancePod extends Entity {
	private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "pod", "pod.obj")).create();
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "pod", "pod.png")).create();

	public InstancePod(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);

		ComponentModel componentModel = new ComponentModel(this, model, 1.0f, texture, 1);
		texture.setHasAlpha(true);
		ComponentLighting componentLighting = new ComponentLighting(this, 1.0f, 0.6f, false, false);
		ComponentLight componentLight = new ComponentLight(this, new Vector3f(0.0f, 0.5f, 0.0f), new Colour(1.0f, 1.0f, 1.0f), new Attenuation(1.0f, 0.03f, 0.08f));
		ComponentCollider componentCollider = new ComponentCollider(this);
		ComponentCollision componentCollision = new ComponentCollision(this);
	}
}
