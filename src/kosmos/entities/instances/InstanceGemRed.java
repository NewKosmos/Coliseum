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

public class InstanceGemRed extends Entity {
	private static final ModelObject model = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "gemRed", "gemRed.obj")).create();
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "gemRed", "gemRed.png")).create();
	private static final TextureObject textureGlow = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "gemRed", "gemRedGlow.png")).create();

	public InstanceGemRed(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);

		ComponentModel componentModel = new ComponentModel(this, 1.0f, model, texture, 1);
		new ComponentGlow(this, textureGlow);
		ComponentSurface componentSurface = new ComponentSurface(this, 1.0f, 0.0f, false, false);
		ComponentLight componentLight = new ComponentLight(this, new Vector3f(0.0f, 1.75f, 0.0f), new Colour(0.878f, 0.271f, 0.271f), new Attenuation(1.0f, 0.02f, 2.0f));
		ComponentCollider componentCollider = new ComponentCollider(this);
		ComponentCollision componentCollision = new ComponentCollision(this);
	}
}
