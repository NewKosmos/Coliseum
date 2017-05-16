package kosmos.entities.instances;

import flounder.entities.Entity;
import flounder.entities.FlounderEntities;
import flounder.entities.components.ComponentModel;
import flounder.entities.components.ComponentSurface;
import flounder.entities.components.ComponentSway;
import flounder.maths.vectors.Vector3f;
import flounder.models.ModelFactory;
import flounder.models.ModelObject;
import flounder.resources.MyFile;
import flounder.space.ISpatialStructure;
import flounder.textures.TextureFactory;
import flounder.textures.TextureObject;

/// Automatically generated entity source
/// Date generated: 30.3.2017 - 12:8
/// Created by: matthew

public class InstanceGrassTuft1 extends Entity {
	private static final ModelObject MODEL = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "grasstuft1", "model.obj")).create();
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "grasstuft1", "diffuse.png")).setNumberOfRows(1).create();
	private static final TextureObject TEXTURE_SWAY = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "grasstuft1", "sway.png")).setNumberOfRows(1).create();

	public InstanceGrassTuft1(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new ComponentModel(this, 1.0f, MODEL, TEXTURE, 1);
		new ComponentSurface(this, 1.0f, 0.0f, false, false, true);
		new ComponentSway(this, TEXTURE_SWAY);
		//	new flounder.entities.components.ComponentCollision(this);
	}
}

