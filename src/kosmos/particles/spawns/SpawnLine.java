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
import kosmos.entities.components.*;

import java.util.*;

public class SpawnLine implements IParticleSpawn {
	private float length;
	private Vector3f axis;
	private Random random;
	private Vector3f spawnPosition;

	public SpawnLine(float length, Vector3f axis) {
		this.length = length;
		this.axis = axis.normalize();
		this.random = new Random();
		this.spawnPosition = new Vector3f();
	}

	public SpawnLine(String[] template) {
		this.length = Float.parseFloat(template[0]);
		this.axis = ComponentParticles.createVector3f(template[1]).normalize();
		this.random = new Random();
		this.spawnPosition = new Vector3f();
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public Vector3f getAxis() {
		return axis;
	}

	public void setAxis(Vector3f axis) {
		this.axis = axis;
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		spawnPosition.set(axis.x * length, axis.y * length, axis.z * length);
		spawnPosition.scale(random.nextFloat() - 0.5f);
		return spawnPosition;
	}
}
