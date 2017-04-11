package kosmos.entities.instances;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.lights.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.skybox.*;

/// Automatically generated entity source
/// Date generated: 30.3.2017 - 12:8
/// Created by: matthew

public class InstanceMoon extends Entity {
	private static final ModelObject MODEL = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "moon", "model.obj")).create();
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "moon", "diffuse.png")).setNumberOfRows(1).create();

	public InstanceMoon(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new kosmos.entities.components.ComponentCelestial(this, false);
		new ComponentModel(this, 10.0f, MODEL, TEXTURE, 1);
		new ComponentSurface(this, 1.0f, 0.0f, true, true);
		new ComponentLight(this, new Vector3f(0.0f, 0.0f, 0.0f), KosmosSkybox.MOON_COLOUR, new Attenuation(1.0f, 0.0f, 0.0f));
	}
}

