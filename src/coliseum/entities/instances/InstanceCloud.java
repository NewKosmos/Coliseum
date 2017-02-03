package coliseum.entities.instances;

import coliseum.entities.components.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class InstanceCloud extends Entity {
	private static final Model model = Model.newModel(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cloud", "cloud.obj")).create();
	private static final Texture texture = Texture.newTexture(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cloud", "cloud.png")).clampEdges().create();

	public InstanceCloud(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation, float scale) {
		super(structure, position, rotation);
		ComponentModel componentModel = new ComponentModel(this, model, scale, texture, 0);
		componentModel.setIgnoreShadows(true);
	}
}
