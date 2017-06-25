/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.uis.screens;

import flounder.events.*;
import flounder.guis.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.network.packets.*;
import kosmos.uis.*;
import kosmos.world.*;

public class ScreenPause extends ScreenObject {
	public ScreenPause(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

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
