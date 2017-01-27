package coliseum.world.terrain;

import coliseum.entities.components.*;
import coliseum.world.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class TerrainWater extends Entity {
	public TerrainWater(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation, Chunk chunk) {
		super(structure, position, rotation);

		Model model = Model.newModel(new MyFile(MyFile.RES_FOLDER, "terrains", "water", "water.obj")).create();
		Texture texture = Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "water", "water.png")).create();

		new ComponentModel(this, model, 2.0f, texture, 0);
		new ComponentCollider(this);
		new ComponentCollision(this);
		new ComponentTerrain(this, chunk);
	}
}
