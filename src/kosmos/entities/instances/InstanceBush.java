package kosmos.entities.instances;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

/// Automatically generated entity source
/// Date generated: 30.3.2017 - 12:8
/// Created by: matthew

public class InstanceBush extends Entity {
	private static final ModelObject MODEL = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "bush", "model.obj")).create();
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "bush", "diffuse.png")).setNumberOfRows(1).create();
	private static final TextureObject TEXTURE_SWAY = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "bush", "sway.png")).setNumberOfRows(1).create();

	public InstanceBush(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new ComponentModel(this, 1.0f, MODEL, TEXTURE, 1);
		new ComponentSway(this, TEXTURE_SWAY);
		new ComponentSurface(this, 1.0f, 0.0f, false, false, true);
		new ComponentCollision(this);
		new ComponentCollider(this);
	}
}

