package kosmos.entities.instances;

import flounder.entities.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

/// Automatically generated entity source
/// Date generated: 29.3.2017 - 12:20
/// Created by: matthew

public class InstanceMuliplayer extends Entity {
	private static final MyFile COLLADA = new MyFile(FlounderEntities.ENTITIES_FOLDER, "muliplayer", "muliplayer.dae");
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "muliplayer", "muliplayerDiffuse.png")).setNumberOfRows(1).create();
	
	public InstanceMuliplayer(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation, String username) {
		super(structure, position, rotation);
		new kosmos.entities.components.ComponentMultiplayer(this, username);
		new kosmos.entities.components.ComponentAnimation(this, 0.2f, COLLADA, TEXTURE, 1);
		new kosmos.entities.components.ComponentSurface(this, 1.0f, 0.0f, false, false);
		new kosmos.entities.components.ComponentLight(this, new Vector3f(0.0f, 2.0f, 0.0f), new Colour(1.0f, 1.0f, 1.0f), new Attenuation(1.0f, 0.02f, 0.5f));
		new kosmos.entities.components.ComponentCollider(this);
		new kosmos.entities.components.ComponentCollision(this);
	 }
 }

