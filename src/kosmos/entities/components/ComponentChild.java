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
import flounder.entities.components.*;
import flounder.helpers.*;

import javax.swing.*;

public class ComponentChild extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private Entity parent;

	/**
	 * Creates a new ComponentChild.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentChild(Entity entity) {
		this(entity, null);
	}

	/**
	 * Creates a new ComponentChild.
	 *
	 * @param entity The entity this component is attached to.
	 * @param parent
	 */
	public ComponentChild(Entity entity, Entity parent) {
		super(entity, ID);

		this.parent = parent;
	}

	@Override
	public void update() {
		if (parent == null || parent.isRemoved()) {
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
