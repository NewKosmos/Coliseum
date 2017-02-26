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

public class InstanceMoon extends Entity {
	private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "moon", "moon.obj")).create();
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "moon", "moon.png")).clampEdges().create();

	public InstanceMoon(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		ComponentCelestial componentCelestial = new ComponentCelestial(this);
		ComponentModel componentModel = new ComponentModel(this, model, 8.5f, texture, 0);
		ComponentSurface componentSurface = new ComponentSurface(this, 1.0f, 0.0f, true, true);
		ComponentLight componentLight = new ComponentLight(this, new Vector3f(), new Colour(0.08f, 0.08f, 0.08f), new Attenuation(1.0f, 0.0f, 0.0f));
		//ComponentCollider componentCollider = new ComponentCollider(this);
		// ComponentCollision componentCollision = new ComponentCollision(this);
	}
}
