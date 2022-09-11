/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.entities.instances;

import com.flounder.entities.*;
import com.flounder.entities.components.*;
import com.flounder.maths.vectors.*;
import com.flounder.resources.*;
import com.flounder.space.*;
import com.flounder.textures.*;
import com.kosmos.entities.components.*;

/// Automatically generated entity source
/// Date generated: 30.3.2017 - 12:8
/// Created by: matthew

public class InstancePlayer extends Entity {
	private static final MyFile COLLADA = new MyFile(FlounderEntities.ENTITIES_FOLDER, "player", "collada.dae");
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "player", "diffuse.png")).setNumberOfRows(1).create();

	public InstancePlayer(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new ComponentPlayer(this);
		new ComponentAnimation(this, 0.25f, COLLADA, TEXTURE, 1);
		new ComponentSurface(this, 1.0f, 0.0f, false, false, true);
		//	new ComponentLight(this, new Vector3f(0.0f, 2.0f, 0.0f), new Colour(1.0f, 1.0f, 1.0f), new Attenuation(1.0f, 0.02f, 0.5f));
		new ComponentCollision(this);
		new ComponentCollider(this);
	}
}

