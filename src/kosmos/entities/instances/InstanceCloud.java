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
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.entities.components.*;

public class InstanceCloud extends Entity {
	private static final Model model = Model.newModel(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cloud", "cloud.obj")).create();
	private static final Texture texture = Texture.newTexture(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cloud", "cloud.png")).clampEdges().create();

	public InstanceCloud(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation, float scale) {
		super(structure, position, rotation);
		ComponentModel componentModel = new ComponentModel(this, model, scale, texture, 0);
		componentModel.setIgnoreShadows(true);
		ComponentCloud componentCloud = new ComponentCloud(this);
	}
}
