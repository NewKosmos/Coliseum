/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.chunks.biomes;

import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.space.*;

public class EntitySpawn {
	protected final EntityCreate create;
	protected final float spawnChance;
	protected final float heightOffset;

	public EntitySpawn(EntityCreate create, float spawnChance, float heightOffset) {
		this.create = create;
		this.spawnChance = spawnChance;
		this.heightOffset = heightOffset;
	}

	/**
	 * Creates a entity from the spawn parameters.
	 */
	public interface EntityCreate {
		/**
		 * Creates a entity from the spawn parameters.
		 *
		 * @param structure The structure to add the entity to.
		 * @param position The position of the entity, there should be a Y offset added.
		 * @param rotation The rotation added to the entity, primary yaw.
		 *
		 * @return The created entity.
		 */
		Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation);
	}
}
