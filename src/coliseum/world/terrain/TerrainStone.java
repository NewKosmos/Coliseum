package coliseum.world.terrain;

import coliseum.entities.components.*;
import coliseum.world.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class TerrainStone extends Entity {
	private static final Model model = Model.newModel(new MyFile(MyFile.RES_FOLDER, "terrains", "stone", "stone.obj")).create();
	private static final Texture texture = Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "stone", "stone.png")).create();

	public TerrainStone(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation, Chunk chunk) {
		super(structure, position, rotation);
		new ComponentModel(this, model, 2.0f, texture, 0);
		new ComponentCollider(this);
		new ComponentCollision(this);
		new ComponentTerrain(this, chunk);
	}
}
