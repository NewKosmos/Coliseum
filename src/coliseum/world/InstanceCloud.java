package coliseum.world;

import coliseum.entities.components.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class InstanceCloud extends Entity {
	private static final Model model = Model.newModel(new MyFile(MyFile.RES_FOLDER, "clouds.obj")).create();
	private static final Texture texture = Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "clouds.png")).clampEdges().create();

	public InstanceCloud(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new ComponentModel(this, model, 128.0f, texture, 0);
	}
}
