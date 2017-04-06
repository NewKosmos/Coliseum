package kosmos.entities.instances;

import flounder.entities.*;
import flounder.helpers.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

import java.util.*;

/// Automatically generated entity source
/// Date generated: 30.3.2017 - 12:8
/// Created by: matthew

public class InstanceGemPurple extends Entity {
	private static final ModelObject MODEL = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "gemPurple", "model.obj")).create();
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "gemPurple", "diffuse.png")).setNumberOfRows(1).create();
	private static final TextureObject TEXTURE_GLOW = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "gemPurple", "glow.png")).setNumberOfRows(1).create();

	public InstanceGemPurple(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new kosmos.entities.components.ComponentModel(this, 1.0f, MODEL, TEXTURE, 1);
		new kosmos.entities.components.ComponentGlow(this, TEXTURE_GLOW);
		new kosmos.entities.components.ComponentSurface(this, 1.0f, 0.0f, false, false);
		new kosmos.entities.components.ComponentLight(this, new Vector3f(0.0f, 1.5f, 0.0f), new Colour(0.631f, 0.243f, 0.812f), new Attenuation(1.0f, 0.02f, 2.0f));
		new kosmos.entities.components.ComponentCollision(this);
		List<Pair<Collider, Vector3f>> colliders = new ArrayList<>();
		colliders.add(new Pair<>(new Sphere(0.65f), new Vector3f(0.0f, 0.2f, 0.0f)));
		new kosmos.entities.components.ComponentCollider(this, colliders);
	}
}

