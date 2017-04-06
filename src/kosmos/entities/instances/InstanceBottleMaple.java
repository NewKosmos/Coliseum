package kosmos.entities.instances;

import flounder.entities.*;
import flounder.helpers.*;
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

public class InstanceBottleMaple extends Entity {
	private static final ModelObject MODEL = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "bottleMaple", "model.obj")).create();
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "bottleMaple", "diffuse.png")).setNumberOfRows(1).create();

	public InstanceBottleMaple(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new kosmos.entities.components.ComponentModel(this, 1.0f, MODEL, TEXTURE, 1);
		new kosmos.entities.components.ComponentSurface(this, 1.0f, 0.0f, false, false);
		new kosmos.entities.components.ComponentCollision(this);
		List<Pair<Collider, Vector3f>> colliders = new ArrayList<>();
		// colliders.add(new Pair<>(new AABB(new Vector3f(-0.1f, -0.0f, -0.1f), new Vector3f(0.1f, 0.6f, 0.1f)), new Vector3f(0.0f, 0.7f, 0.0f)));
		colliders.add(new Pair<>(new Cylinder(0.1f, 0.6f, new Vector3f()), new Vector3f(0.0f, 1.0f, 0.0f)));
		new kosmos.entities.components.ComponentCollider(this, colliders);
	}
}

