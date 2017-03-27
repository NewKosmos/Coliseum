/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package editors.editor;

import flounder.guis.*;
import flounder.maths.*;

public class EditorGuis extends GuiMaster {
	private static final Colour COLOUR_PRIMARY = new Colour(1.0f, 0.0f, 0.0f);

	public EditorGuis() {
		super();
	}

	@Override
	public void init() {

	}

	@Override
	public void update() {

	}

	@Override
	public void profile() {

	}

	@Override
	public boolean isGamePaused() {
		return false;
	}

	@Override
	public float getBlurFactor() {
		return 0.0f;
	}

	@Override
	public Colour getPrimaryColour() {
		return COLOUR_PRIMARY;
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
