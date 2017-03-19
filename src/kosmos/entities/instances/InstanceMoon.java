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
import kosmos.world.*;

public class InstanceMoon extends Entity {
	private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "moon", "moon.obj")).create();
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "moon", "moon.png")).clampEdges().create();

	public InstanceMoon(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new ComponentCelestial(this, false);
		new ComponentModel(this, 12.0f, model, texture, 1);
		new ComponentSurface(this, 1.0f, 0.0f, true, true);
		new ComponentLight(this, new Vector3f(), new Colour(KosmosWorld.MOON_COLOUR), new Attenuation(1.0f, 0.0f, 0.0f));
		//new ComponentCollider(this);
		//new ComponentCollision(this);
	}
}
