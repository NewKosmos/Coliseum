/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.uis.screens;

import flounder.fonts.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.visual.*;
import kosmos.uis.*;

public class ScreenStart extends ScreenObject {
	public ScreenStart(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Game Title.
		TextObject title = new TextObject(this, new Vector2f(0.5f, 0.15f), "New Kosmos", 5.0f, FlounderFonts.CANDARA, 1.0f, GuiAlign.CENTRE);
		title.setInScreenCoords(true);
		title.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		title.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		title.setBorder(new ConstantDriver(0.022f));

		float yPosition = 0.30f;
		float ySpacing = 0.07f;

		// Singleplayer.
		ScreenSingleplayer screenSingleplayer = new ScreenSingleplayer(slider);
		screenSingleplayer.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText buttonSingleplayer = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Singleplayer", GuiAlign.CENTRE);
		buttonSingleplayer.addLeftListener(() -> slider.setNewSecondaryScreen(screenSingleplayer));

		// Multiplayer.
		ScreenMultiplayer screenMultiplayer = new ScreenMultiplayer(slider);
		screenMultiplayer.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText buttonMultiplayer = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Multiplayer", GuiAlign.CENTRE);
		buttonMultiplayer.addLeftListener(() -> slider.setNewSecondaryScreen(screenMultiplayer));

		// Settings.
		ScreenSettings screenSettings = new ScreenSettings(slider);
		screenSettings.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText buttonSettings = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Settings", GuiAlign.CENTRE);
		buttonSettings.addLeftListener(() -> slider.setNewSecondaryScreen(screenSettings));

		// About.
		ScreenAbout screenAbout = new ScreenAbout(slider);
		screenAbout.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText buttonAbout = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "About", GuiAlign.CENTRE);
		buttonAbout.addLeftListener(() -> slider.setNewSecondaryScreen(screenAbout));

		// Exit.
		GuiButtonText buttonExit = new GuiButtonText(this, new Vector2f(0.5f, yPosition += 1.2f * ySpacing), "Exit To Desktop", GuiAlign.CENTRE);
		buttonExit.addLeftListener(() -> {
			FlounderLogger.get().log("Exiting to desktop!");
			Framework.get().requestClose(false);
		});
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {

	}
}
