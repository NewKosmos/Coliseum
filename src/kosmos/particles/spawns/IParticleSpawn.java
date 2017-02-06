/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.particles.spawns;

import flounder.maths.vectors.*;

/**
 * A interface that defines a particle spawn type.
 */
public interface IParticleSpawn {
	/**
	 * Gets the base spawn position.
	 *
	 * @return The base spawn position.
	 */
	Vector3f getBaseSpawnPosition();
}
