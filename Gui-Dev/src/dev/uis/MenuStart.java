/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package dev.uis;

import flounder.framework.*;
import flounder.guis.*;

import java.util.*;

public class MenuStart extends GuiComponent {
	private MasterMenu superMenu;
	private MasterSlider masterSlider;

	public MenuStart(MasterMenu superMenu, MasterSlider masterSlider) {
		this.superMenu = superMenu;
		this.masterSlider = masterSlider;

		/*float currentY = 1.0f + MasterSlider.BUTTONS_Y_SEPARATION;
		createQuitButton(currentY -= MasterSlider.BUTTONS_Y_SEPARATION);
		currentY -= MasterSlider.BUTTONS_Y_SEPARATION * MasterSlider.BUTTONS_Y_SEPARATION;

		createPlayButton(currentY -= MasterSlider.BUTTONS_Y_SEPARATION);*/

		createPlayButton(0.1f);
		createQuitButton(0.3f);
	}

	private void createQuitButton(float yPos) {
		GuiTextButton button = MasterSlider.createButton("Quit", yPos, this);
		button.addLeftListener(FlounderFramework::requestClose);
		button.addRightListener(null);
	}

	private void createPlayButton(float yPos) {
		//	GuiCheckbox checkbox = MasterSlider.createCheckbox("Testing", GuiAlign.LEFT, yPos - MasterSlider.BUTTONS_Y_SEPARATION, false, this);
		GuiTextButton button = MasterSlider.createButton("Play", yPos, this);
		//	button.addLeftListener(() -> masterSlider.setNewSecondaryScreen(screenPlay, true));
		//	button.addRightListener(null);
	}

	@Override
	protected void updateSelf() {

	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {

	}
}
