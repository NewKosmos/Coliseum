/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.components;

import flounder.entities.*;
import flounder.helpers.*;
import kosmos.chunks.*;

import javax.swing.*;

public class ComponentChunk extends IComponentEntity implements IComponentEditor {
	/**
	 * Creates a new ComponentChunk.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentChunk(Entity entity) {
		super(entity);
	}

	@Override
	public void update() {
		if (getEntity() == null || !(getEntity() instanceof Chunk)) {
			return;
		}

		Chunk chunk = (Chunk) getEntity();

		if (chunk.isRemoved()) {
			getEntity().forceRemove(false);
		}
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		//return new Pair<>(
		//		new String[]{}, // Static variables
		//		new String[]{} // Class constructor
		//);
		return null;
	}

	@Override
	public void dispose() {
	}
}
