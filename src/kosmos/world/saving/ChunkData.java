/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.world.saving;

import flounder.entities.*;
import flounder.maths.vectors.*;

import java.util.*;

public class ChunkData {
	private Vector3f position;
	private List<Vector3f> removed;
	private List<Entity> added;

	public ChunkData(Vector3f position, List<Vector3f> removed, List<Entity> added) {
		this.position = position;
		this.removed = removed;
		this.added = added;
	}

	public ChunkData(String loaded) {
		this.position = new Vector3f();
		this.removed = new ArrayList<>();
		this.added = new ArrayList<>();

		String[] p = loaded.split("]")[0].replace("[", "").trim().split(",");
		this.position.x = Float.parseFloat(p[0]);
		this.position.y = Float.parseFloat(p[1]);
		this.position.z = Float.parseFloat(p[2]);

		String[] r = loaded.split("}")[0].replace("{", "").trim().split(",");

		String[] a = loaded.split("\\{")[2].replace("}", "").trim().split(",");

	}

	public Vector3f getPosition() {
		return position;
	}

	public List<Vector3f> getRemoved() {
		return removed;
	}

	public void remove(Entity entity) {
		added.remove(entity);
		removed.add(entity.getPosition());
	}

	public List<Entity> getAdded() {
		return added;
	}

	public void add(Entity entity) {
		added.add(entity);
	}

	protected String getSaveData() {
		StringBuilder result = new StringBuilder("[" + position.x + "," + position.y + "," + position.z + "]: {");

		for (Vector3f r : removed) {
			result.append(r.x).append(",").append(r.y).append(",").append(r.z).append(",");
		}

		result.append("}: {");

		for (Entity a : added) {
			result.append("\'").append(a.getClass().getName()).append("\', ").append(a.getPosition().x).append(",").append(a.getPosition().y).append(",").append(a.getPosition().z).append(",");
			result.append(a.getRotation().x).append(",").append(a.getRotation().y).append(",").append(a.getRotation().z).append(",");
		}

		result.append("}");

		return result.toString();
	}
}
