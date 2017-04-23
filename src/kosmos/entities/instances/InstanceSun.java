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
import kosmos.entities.components.*;
import kosmos.world.*;

/// Automatically generated entity source
/// Date generated: 30.3.2017 - 12:8
/// Created by: matthew

public class InstanceSun extends Entity {
	private static final ModelObject MODEL = ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "sun", "model.obj")).create();
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "sun", "diffuse.png")).setNumberOfRows(1).create();
	private static final TextureObject TEXTURE_GLOW = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "sun", "glow.png")).setNumberOfRows(1).create();

	public InstanceSun(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new ComponentCelestial(this, ComponentCelestial.LightType.SUN);
		new ComponentModel(this, 16.0f, MODEL, TEXTURE, 1);
		new ComponentGlow(this, TEXTURE_GLOW);
		new ComponentSurface(this, 1.0f, 0.0f, true, true, false);
		new ComponentLight(this, new Vector3f(0.0f, 0.0f, 0.0f), new Colour(KosmosWorld.SUN_COLOUR_DAY), new Attenuation(1.0f, 0.0f, 0.0f));
	}
}

