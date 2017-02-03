package coliseum.entities.instances;

import coliseum.entities.components.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class InstanceClouds extends Entity {
	private static final Model model = Model.newModel(new MyFile(FlounderEntities.ENTITIES_FOLDER, "clouds", "clouds.obj")).create();
	private static final Texture texture = Texture.newTexture(new MyFile(FlounderEntities.ENTITIES_FOLDER, "clouds", "clouds.png")).clampEdges().create();

	public InstanceClouds(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		ComponentModel componentModel = new ComponentModel(this, model, 128.0f, texture, 0);
		componentModel.setIgnoreShadows(false);
		componentModel.setIgnoreFog(true);
	}
}
