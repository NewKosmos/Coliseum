package coliseum.chunks;

import coliseum.entities.components.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;

public class TerrainChunk extends Entity {
	private static final Model model = Model.newModel(new MyFile(MyFile.RES_FOLDER, "terrains", "grass", "grass.obj")).create();

	public TerrainChunk(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation, Chunk chunk) {
		super(structure, position, rotation);
		ComponentModel componentModel = new ComponentModel(this, model, 2.0f, null, 0);
	}
}
