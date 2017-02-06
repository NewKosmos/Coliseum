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

public class SpawnPoint implements IParticleSpawn {
	private Vector3f point;

	public SpawnPoint() {
		point = new Vector3f();
	}

	public SpawnPoint(String[] template) {
		point = new Vector3f();
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		return point;
	}
}
