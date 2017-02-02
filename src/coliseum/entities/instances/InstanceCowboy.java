package coliseum.entities.instances;

import coliseum.entities.components.*;
import flounder.animation.*;
import flounder.collada.*;
import flounder.collada.animation.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

public class InstanceCowboy extends Entity {
	private static final MyFile colladaFile = new MyFile(FlounderEntities.ENTITIES_FOLDER, "cowboy", "cowboy.dae");
	private static final Texture texture = Texture.newTexture(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cowboy", "cowboy.png")).create();

	public InstanceCowboy(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
		super(structure, position, rotation);

		ModelAnimated modelAnimated = FlounderCollada.loadCollada(colladaFile);

		AnimationData animationData = FlounderCollada.loadAnimation(colladaFile);
		Animation animation = FlounderAnimation.loadAnimation(animationData);

		// ComponentCollider componentCollider = new ComponentCollider(this);
		// ComponentCollision componentCollision = new ComponentCollision(this);
		ComponentPlayer componentPlayer = new ComponentPlayer(this);
		ComponentAnimation componentAnimation = new ComponentAnimation(this, modelAnimated, 0.8f, texture, 1);
		componentAnimation.doAnimation(animation);
	}
}
