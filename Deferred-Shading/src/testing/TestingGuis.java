/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package testing;

import flounder.guis.*;
import testing.ui.*;

public class TestingGuis extends GuiMaster {
	private MasterOverlay masterOverlay;

	public TestingGuis() {
		super();
	}

	@Override
	public void init() {
		this.masterOverlay = new MasterOverlay();

		FlounderGuis.addComponent(masterOverlay, 0.0f, 0.0f, 1.0f, 1.0f);
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
	public void openMenu() {

	}

	@Override
	public float getBlurFactor() {
		return 0.0f;
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
