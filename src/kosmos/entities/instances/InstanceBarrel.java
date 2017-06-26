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
import flounder.textures.*;

/// Automatically generated entity source
/// Date generated: 18.6.2017 - 17:0
/// Created by: matthew

public class InstanceBarrel extends Entity {
	private static final ModelObject MODEL = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "barrel", "model.obj")).create();
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "barrel", "diffuse.png")).setNumberOfRows(1).create();
	private static final TextureObject TEXTURE_NORMALS = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "barrel", "normals.png")).setNumberOfRows(1).create();

	public InstanceBarrel(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new ComponentModel(this, 0.1f, MODEL, TEXTURE, 1);
		new ComponentNormals(this, TEXTURE_NORMALS);
		new ComponentSurface(this, 10.0f, 0.5f, false, false, true);
		new ComponentCollision(this);
		new ComponentCollider(this);
	}
}

