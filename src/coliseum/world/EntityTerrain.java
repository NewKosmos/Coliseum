package coliseum.world;

import coliseum.entities.components.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class EntityTerrain extends Entity {
	public EntityTerrain(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);

		Model model = Model.newModel(new MyFile(MyFile.RES_FOLDER, "terrains", "grass", "grass.obj")).create();
		Texture texture = Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "grass", "grass.png")).create();

		new ComponentModel(this, model, 2.0f, texture, 0);
		new ComponentCollider(this);
		new ComponentCollision(this);
	}
}
