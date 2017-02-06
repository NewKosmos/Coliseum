/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks;

import flounder.maths.vectors.*;

public class ChunkMaths {
	public static Vector3f calculateRGB(Vector2f position, float length, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		destination.x = (((float) Math.sqrt(3.0f) / 3.0f) * position.x - (position.y / 3.0f)) / length;
		destination.y = -(((float) Math.sqrt(3.0f) / 3.0f) * position.x + (position.y / 3.0f)) / length;
		destination.z = (2.0f / 3.0f) * position.y / length;
		return destination;
	}

	public static Vector2f calculateXY(Vector3f position, float length, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		destination.x = (float) Math.sqrt(3.0f) * length * ((position.z / 2.0f) + position.x);
		destination.y = (3.0f / 2.0f) * length * position.z;
		return destination;
	}
}
