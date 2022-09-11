/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.uis.screens;

import com.flounder.events.*;
import com.flounder.fonts.*;
import com.flounder.guis.*;
import com.flounder.logger.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.networking.*;
import com.flounder.visual.*;
import com.kosmos.*;
import com.kosmos.network.packets.*;
import com.kosmos.uis.*;
import com.kosmos.world.*;

public class ScreenPause extends ScreenObject {
	public ScreenPause(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Title.
		TextObject title = new TextObject(this, new Vector2f(0.5f, 0.1f), "Game Paused", 4.0f, FlounderFonts.CANDARA, 1.0f, GuiAlign.CENTRE);
		title.setInScreenCoords(true);
		title.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		title.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		title.setBorder(new ConstantDriver(0.022f));
		title.setScaleDriver(new SinWaveDriver(2.0f, 4.0f, 2.0f));
		title.setRotationDriver(new SinWaveDriver(-20.0f, 20.0f, 3.0f));

		float yPosition = 0.30f;
		float ySpacing = 0.07f;

		// Save Game.
		GuiButtonText saveGame = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Save Game", GuiAlign.CENTRE);
		saveGame.addLeftListener(() -> {
			if (KosmosWorld.get().getWorld() != null) {
				KosmosWorld.get().getWorld().save();
			}

			KosmosConfigs.saveAllConfigs();
		});

		// Settings.
		ScreenSettings screenSettings = new ScreenSettings(slider);
		screenSettings.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText settings = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Settings", GuiAlign.CENTRE);
		settings.addLeftListener(() -> slider.setNewSecondaryScreen(screenSettings));

		// About.
		ScreenAbout screenAbout = new ScreenAbout(slider);
		screenAbout.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText about = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "About", GuiAlign.CENTRE);
		about.addLeftListener(() -> slider.setNewSecondaryScreen(screenAbout));

		// Exit.
		GuiButtonText exitToMenu = new GuiButtonText(this, new Vector2f(0.5f, yPosition += 1.2f * ySpacing), "Exit To Menu", GuiAlign.CENTRE);
		exitToMenu.addLeftListener(() -> {
			FlounderEvents.get().addEvent(new EventTime(0.4f, false) {
				@Override
				public void onEvent() {
					slider.sliderStartMenu(true);
				}
			});

			FlounderLogger.get().log("Leaving world!");

			if (FlounderNetwork.get().getSocketClient() != null) {
				new PacketDisconnect(FlounderNetwork.get().getUsername()).writeData(FlounderNetwork.get().getSocketClient());
				FlounderNetwork.get().closeClient();
				KosmosWorld.get().deleteWorld(false);
			} else {
				KosmosWorld.get().deleteWorld(true);
			}
		});
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {

	}
}
