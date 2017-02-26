package kosmos.entities.editing.particles;

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
