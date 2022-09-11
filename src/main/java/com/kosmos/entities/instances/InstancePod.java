package com.kosmos.entities.instances;

import com.flounder.entities.*;
import com.flounder.entities.components.*;
import com.flounder.lights.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.models.*;
import com.flounder.resources.*;
import com.flounder.space.*;
import com.flounder.textures.*;

/// Automatically generated entity source
/// Date generated: 30.3.2017 - 12:8
/// Created by: matthew

public class InstancePod extends Entity {
	private static final ModelObject MODEL = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "pod", "model.obj")).create();
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "pod", "diffuse.png")).setNumberOfRows(1).create();
	private static final TextureObject TEXTURE_GLOW = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "pod", "glow.png")).setNumberOfRows(1).create();

	public InstancePod(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		TEXTURE.setHasAlpha(true);
		new ComponentModel(this, 1.0f, MODEL, TEXTURE, 1);
		new ComponentGlow(this, TEXTURE_GLOW);
		new ComponentSurface(this, 1.0f, 0.0f, false, false, true);
		new ComponentLight(this, new Vector3f(0.0f, 0.5f, 0.0f), new Colour(1.0f, 1.0f, 1.0f), new Attenuation(1.0f, 0.06f, 1.0f));
		new ComponentCollision(this);
		new ComponentCollider(this);
	}
}

