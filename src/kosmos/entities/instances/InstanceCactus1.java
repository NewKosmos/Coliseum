/**
 * Automatically generated entity source
 * Date generated: 28.2.2017 - 21:56
 * Created by: mattp
 */
package kosmos.entities.instances;

import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class InstanceCactus1 extends Entity {
	public InstanceCactus1(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);
		new kosmos.entities.components.ComponentModel(this, 1.0f, ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cactus1", "cactus1.obj")).create(), TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cactus1", "cactus1Diffuse.png")).setNumberOfRows(1).create(), 1);
		new kosmos.entities.components.ComponentSway(this, TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cactus1", "cactus1Diffuse.png")).setNumberOfRows(1).create());
		new kosmos.entities.components.ComponentSurface(this, 1.0f, 0.0f, false, false);
		new kosmos.entities.components.ComponentCollider(this);
		new kosmos.entities.components.ComponentCollision(this);
	};
};

