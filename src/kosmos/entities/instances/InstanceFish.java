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
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.entities.components.*;

public class InstanceFish extends Entity {
	private static final MyFile colladaFile = new MyFile(FlounderEntities.ENTITIES_FOLDER, "fish", "fish.dae");
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "fish", "fishTexture.png")).create();

	public InstanceFish(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new ComponentAnimation(this, colladaFile, 0.5f, texture, 1);
		new ComponentSurface(this, 1.0f, 0.0f, false, false);
		new ComponentLight(this, new Vector3f(0.0f, 2.0f, 0.0f), new Colour(1.0f, 1.0f, 1.0f), new Attenuation(1.0f, 0.02f, 0.5f));
	}
}