package coliseum.entities.instances.terrain;

import coliseum.chunks.*;
import coliseum.entities.components.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class TerrainSand extends Entity {
	private static final Model model = Model.newModel(new MyFile(MyFile.RES_FOLDER, "terrains", "sand", "sand.obj")).create();
	private static final Texture texture = Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "sand", "sand.png")).clampEdges().create();

	public TerrainSand(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation, Chunk chunk) {
		super(structure, position, rotation);
		ComponentModel componentModel = new ComponentModel(this, model, 2.0f, texture, 0);
		ComponentCollider componentCollider = new ComponentCollider(this);
		ComponentCollision componentCollision = new ComponentCollision(this);
		ComponentTerrain componentTerrain = new ComponentTerrain(this, chunk, (float) Math.sqrt(2.0f) * 2.0f);
	}
}
