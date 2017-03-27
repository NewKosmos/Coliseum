/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package editors.editor;

import flounder.framework.*;

/**
 * A extension used with {@link KosmosEditor} to define a editor type.
 */
public abstract class IEditorType extends Extension {
	/**
	 * Creates a new editor type.
	 *
	 * @param requires The classes that are extra requirements for this implementation.
	 */
	public IEditorType(Class... requires) {
		super(KosmosEditor.class, requires);
	}

	/**
	 * Run when initializing the editor type.
	 */
	public abstract void init();

	/**
	 * Run when updating the editor type.
	 */
	public abstract void update();

	/**
	 * Run when profiling the editor type.
	 */
	public abstract void profile();

	/**
	 * Run when disposing the editor type.
	 */
	public abstract void dispose();

	@Override
	public abstract boolean isActive();
}
