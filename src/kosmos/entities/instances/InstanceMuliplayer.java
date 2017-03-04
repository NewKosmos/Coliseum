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
	private static final MyFile colladaFile = new MyFile(FlounderEntities.ENTITIES_FOLDER, "cowboy", "cowboy.dae");
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cowboy", "cowboy.png")).create();

	public InstanceMuliplayer(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation, String username) {
		super(structure, position, rotation);

		new ComponentMultiplayer(this, username);
		new ComponentAnimation(this, colladaFile, 0.2f, texture, 1);
		new ComponentSurface(this, 1.0f, 0.0f, false, false);
		new ComponentLight(this, new Vector3f(0.0f, 2.0f, 0.0f), new Colour(0.9f, 0.8f, 0.8f), new Attenuation(1.0f, 0.02f, 0.2f));
	//	new ComponentModel(this, 1.0f, model, texture, 1);
	//	new ComponentCollider(this);
	//	new ComponentCollision(this);
	}
}
