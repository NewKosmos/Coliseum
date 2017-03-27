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

public abstract class IEditorParticleSpawn {
	public abstract String getTabName();

	public abstract IParticleSpawn getComponent();

	public abstract void addToPanel(JPanel panel);

	/**
	 * Gets a list of saveable values.
	 *
	 * @return The saveable values.
	 */
	public abstract String[] getSavableValues();
}
