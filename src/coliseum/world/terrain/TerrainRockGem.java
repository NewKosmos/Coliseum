package coliseum.world.terrain;

import coliseum.entities.components.*;
import coliseum.world.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class TerrainRockGem extends Entity {
	private static final Model model = Model.newModel(new MyFile(MyFile.RES_FOLDER, "terrains", "rockGem", "rockGem.obj")).create();
	private static final Texture texture = Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "rockGem", "rockGem.png")).clampEdges().create();

	public TerrainRockGem(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation, Chunk chunk) {
		super(structure, position, rotation);
		new ComponentModel(this, model, 2.0f, texture, 0);
		new ComponentCollider(this);
		new ComponentCollision(this);
		new ComponentTerrain(this, chunk, (float) Math.sqrt(2.0f) * 2.0f);
	}
}
