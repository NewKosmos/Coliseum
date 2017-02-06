/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package dev.uis;

import flounder.guis.*;

import java.util.*;

public class MenuPause extends GuiComponent {
	private MasterMenu superMenu;
	private MasterSlider masterSlider;

	public MenuPause(MasterMenu superMenu, MasterSlider masterSlider) {
		this.superMenu = superMenu;
		this.masterSlider = masterSlider;
	}

	@Override
	protected void updateSelf() {

	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {

	}
}
