package coliseum.entities.instances;

import coliseum.entities.components.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class InstanceMoon extends Entity {
	private static final Model model = Model.newModel(new MyFile(FlounderEntities.ENTITIES_FOLDER, "moon", "moon.obj")).create();
	private static final Texture texture = Texture.newTexture(new MyFile(FlounderEntities.ENTITIES_FOLDER, "moon", "moon.png")).clampEdges().create();

	public InstanceMoon(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		ComponentModel componentModel = new ComponentModel(this, model, 8.5f, texture, 0);
		componentModel.setIgnoreShadows(true);
		componentModel.setIgnoreFog(true);
		ComponentCelestial componentCelestial = new ComponentCelestial(this);
	}
}
