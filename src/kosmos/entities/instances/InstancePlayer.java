package kosmos.entities.instances;

import flounder.entities.*;
import flounder.helpers.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

import java.util.*;

/// Automatically generated entity source
/// Date generated: 30.3.2017 - 12:8
/// Created by: matthew

public class InstancePlayer extends Entity {
	private static final MyFile COLLADA = new MyFile(FlounderEntities.ENTITIES_FOLDER, "player", "collada.dae");
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "player", "diffuse.png")).setNumberOfRows(1).create();

	public InstancePlayer(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new kosmos.entities.components.ComponentPlayer(this);
		new kosmos.entities.components.ComponentAnimation(this, 0.2f, COLLADA, TEXTURE, 1);
		new kosmos.entities.components.ComponentSurface(this, 1.0f, 0.0f, false, false);
		new kosmos.entities.components.ComponentLight(this, new Vector3f(0.0f, 2.0f, 0.0f), new Colour(1.0f, 1.0f, 1.0f), new Attenuation(1.0f, 0.02f, 0.5f));
		new kosmos.entities.components.ComponentCollision(this);
		List<Pair<Collider, Vector3f>> colliders = new ArrayList<>();
		colliders.add(new Pair<>(new Sphere(2.2f), new Vector3f(0.0f, 8.0f, 0.0f)));
	//	colliders.add(new Pair<>(new AABB(new Vector3f(-2.0f, -0.0f, -2.0f), new Vector3f(2.0f, 6.0f, 2.0f)), new Vector3f(0.0f, 0.0f, 0.0f)));
		colliders.add(new Pair<>(new Cylinder(2.0f, 7.0f, new Vector3f()), new Vector3f(0.0f, 3.5f, 0.0f)));
		new kosmos.entities.components.ComponentCollider(this, colliders);
	}
}

