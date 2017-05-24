package kosmos.chunks.biomes;

import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.space.*;

public abstract class EntitySpawn {
	protected final float spawnChance;
	protected final float heightOffset;

	public EntitySpawn(float spawnChance, float heightOffset) {
		this.spawnChance = spawnChance;
		this.heightOffset = heightOffset;
	}

	/**
	 * Creates a entity from the spawn parameters.
	 *
	 * @param structure The structure to add the entity to.
	 * @param position The position of the entity, there should be a Y offset added.
	 * @param rotation The rotation added to the entity, primary yaw.
	 *
	 * @return The created entity.
	 */
	public abstract Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation);
}
