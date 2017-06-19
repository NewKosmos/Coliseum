package kosmos.entities.instances;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

/// Automatically generated entity source
/// Date generated: 30.3.2017 - 12:8
/// Created by: matthew

public class InstanceGemGreen extends Entity {
	private static final ModelObject MODEL = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "gemGreen", "model.obj")).create();
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "gemGreen", "diffuse.png")).setNumberOfRows(1).create();
	private static final TextureObject TEXTURE_GLOW = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "gemGreen", "glow.png")).setNumberOfRows(1).create();

	public InstanceGemGreen(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new ComponentModel(this, 1.0f, MODEL, TEXTURE, 1);
		new ComponentGlow(this, TEXTURE_GLOW);
		new ComponentSurface(this, 1.0f, 0.0f, false, false, true);
		new ComponentLight(this, new Vector3f(0.0f, 2.5f, 0.0f), new Colour(0.0f, 1.0f, 0.0f), new Attenuation(1.0f, 0.02f, 2.0f));
		new ComponentCollision(this);
		new ComponentCollider(this);
	}
}

