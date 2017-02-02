package coliseum.entities.instances;

import coliseum.entities.components.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class InstanceRobit extends Entity {
	private static final Model model = Model.newModel(new MyFile(FlounderEntities.ENTITIES_FOLDER, "robit", "robit.obj")).create();
	private static final Texture texture = Texture.newTexture(new MyFile(FlounderEntities.ENTITIES_FOLDER, "robit", "robit.png")).create();

	public InstanceRobit(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);

		ComponentModel componentModel = new ComponentModel(this, model, 1.25f, texture, 1);
		ComponentPlayer componentPlayer = new ComponentPlayer(this);
	}
}
