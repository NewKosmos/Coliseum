/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.entities.instances;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;

/// Automatically generated entity source
/// Date generated: 27.06.2017 - 12:40
/// Created by: decaxon

public class InstanceTable extends Entity {
	private static final ModelObject MODEL = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "table", "model.obj")).create();
	// private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "table", "diffuse.png")).setNumberOfRows(1).create();

	public InstanceTable(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new ComponentModel(this, 1.5f, MODEL, null, 1);
		new ComponentSurface(this, 1.0f, 0.0f, false, false, true);
		new ComponentCollision(this);
		new ComponentCollider(this);
	}
}

