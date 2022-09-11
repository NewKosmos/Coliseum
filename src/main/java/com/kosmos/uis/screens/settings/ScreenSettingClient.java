/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.uis.screens.settings;

import com.flounder.events.*;
import com.flounder.fonts.*;
import com.flounder.guis.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.visual.*;
import com.kosmos.camera.*;
import com.kosmos.uis.*;
import com.kosmos.uis.screens.*;

public class ScreenSettingClient extends ScreenObject {
	public ScreenSettingClient(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Title.
		TextObject title = new TextObject(this, new Vector2f(0.5f, 0.1f), "Client Settings", 3.0f, FlounderFonts.CANDARA, 1.0f, GuiAlign.CENTRE);
		title.setInScreenCoords(true);
		title.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		title.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		title.setBorder(new ConstantDriver(0.022f));

		// Text Username.
		GuiInputText textUsername = new GuiInputText(this, new Vector2f(0.5f, 0.20f), "Username: ", KosmosPlayer.getUsername(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<String>(KosmosPlayer::getUsername) {
			@Override
			public void onEvent(String newValue) {
				textUsername.setValue(newValue);
			}
		});
		textUsername.addChangeListener(() -> KosmosPlayer.setUsername(textUsername.getValue()));

		// Back.
		GuiButtonText back = new GuiButtonText(this, new Vector2f(0.5f, 0.9f), "Back", GuiAlign.CENTRE);
		back.addLeftListener(() -> slider.setNewSecondaryScreen(settings));
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {

	}
}
