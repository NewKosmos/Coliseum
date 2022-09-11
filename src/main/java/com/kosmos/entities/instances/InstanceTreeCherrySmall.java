package com.kosmos.entities.instances;

import com.flounder.entities.*;
import com.flounder.entities.components.*;
import com.flounder.maths.vectors.*;
import com.flounder.models.*;
import com.flounder.resources.*;
import com.flounder.space.*;
import com.flounder.textures.*;

/// Automatically generated entity source
/// Date generated: 30.3.2017 - 12:8
/// Created by: matthew

public class InstanceTreeCherrySmall extends Entity {
	private static final ModelObject MODEL = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "treeCherrySmall", "model.obj")).create();
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "treeCherrySmall", "diffuse.png")).setNumberOfRows(1).create();
	private static final TextureObject TEXTURE_SWAY = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "treeCherrySmall", "sway.png")).setNumberOfRows(1).create();

	public InstanceTreeCherrySmall(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new ComponentModel(this, 1.0f, MODEL, TEXTURE, 1);
		new ComponentSurface(this, 1.0f, 0.0f, false, false, true);
		new ComponentSway(this, TEXTURE_SWAY);
		new ComponentCollision(this);
		new ComponentCollider(this);
	}
}

