package coliseum.world;

import coliseum.entities.components.*;
import flounder.animation.*;
import flounder.collada.*;
import flounder.collada.animation.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class InstanceDerpWalk extends Entity {
	private static final MyFile colladaFile = new MyFile(FlounderEntities.ENTITIES_FOLDER, "derpWalk.dae");
	private static final Texture texture = null; // Texture.newTexture(new MyFile(FlounderEntities.ENTITIES_FOLDER, "derpWalk.png")).create();

	public InstanceDerpWalk(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);

		ModelAnimated modelAnimated = FlounderCollada.loadCollada(colladaFile);

		AnimationData animationData = FlounderCollada.loadAnimation(colladaFile);
		Animation animation = FlounderAnimation.loadAnimation(animationData);

		//	new ComponentCollision(this);
		//	new ComponentCollider(this);
		ComponentAnimation componentAnimation = new ComponentAnimation(this, modelAnimated, 1.0f, texture, 1);
		componentAnimation.doAnimation(animation);
	}
}
