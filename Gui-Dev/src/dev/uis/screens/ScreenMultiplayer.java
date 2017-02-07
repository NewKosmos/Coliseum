/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package dev.uis.screens;

import dev.uis.*;
import flounder.events.*;
import flounder.guis.*;

import java.util.*;

public class ScreenMultiplayer extends GuiComponent {
	private MasterSlider masterSlider;

	public ScreenMultiplayer(MasterSlider masterSlider) {
		this.masterSlider = masterSlider;

		createBackOption(0.9f);

		super.show(false);

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return ScreenMultiplayer.super.isShown() && MasterSlider.BACK_KEY.wasDown();
			}

			@Override
			public void onEvent() {
				masterSlider.closeSecondaryScreen();
			}
		});
	}

	private void createBackOption(float yPos) {
		GuiTextButton button = MasterSlider.createButton("Back", yPos, this);
		button.addLeftListener(masterSlider::closeSecondaryScreen);
	}

	@Override
	protected void updateSelf() {

	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {

	}
}
