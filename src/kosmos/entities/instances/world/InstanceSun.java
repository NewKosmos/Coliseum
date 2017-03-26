/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.instances.world;

import flounder.entities.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.entities.components.*;
import kosmos.skybox.*;

public class InstanceSun extends Entity {
	private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "sun", "sun.obj")).create();
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "sun", "sun.png")).clampEdges().create();

	public InstanceSun(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new ComponentCelestial(this, true);
		new ComponentModel(this, 13.0f, model, texture, 0);
		new ComponentLight(this, new Vector3f(), new Colour(KosmosSkybox.SUN_COLOUR_DAY), new Attenuation(1.0f, 0.0f, 0.0f));
		new ComponentSurface(this, 1.0f, 0.0f, true, true);
		//new ComponentCollider(this);
		//new ComponentCollision(this);
	}
}
