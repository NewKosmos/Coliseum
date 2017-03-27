/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.components.particles;

import kosmos.particles.spawns.*;

import javax.swing.*;

public class EditorParticlePoint extends IEditorParticleSpawn {
	private SpawnPoint spawn;

	public EditorParticlePoint() {
		spawn = new SpawnPoint();
	}

	@Override
	public String getTabName() {
		return "Point";
	}

	@Override
	public SpawnPoint getComponent() {
		return spawn;
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public String[] getSavableValues() {
		return new String[]{};
	}
}
