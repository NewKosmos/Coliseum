/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis.screens;

import flounder.fonts.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.network.packets.*;
import kosmos.uis.*;
import kosmos.world.*;

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

		// Load Save.
		GuiButtonText loadSave = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Load Save", GuiAlign.CENTRE);
		loadSave.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				slider.sliderStartMenu(false);
				((KosmosGuis) FlounderGuis.getGuiMaster()).togglePause(true);
				KosmosConfigs.saveAllConfigs();

				// Generates the player and the world.
				KosmosWorld.generatePlayer();
			}
		});

		// Multiplayer.
		GuiButtonText multiplayer = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Multiplayer", GuiAlign.CENTRE);
		multiplayer.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				slider.sliderStartMenu(false);
				((KosmosGuis) FlounderGuis.getGuiMaster()).togglePause(true);
				KosmosConfigs.saveAllConfigs();

				// Connects to the server.
				String username = KosmosConfigs.CLIENT_USERNAME.getString();
				String serverIP = KosmosConfigs.SERVER_IP.setReference(() -> FlounderNetwork.getSocketClient() == null ? null : FlounderNetwork.getSocketClient().getIpAddress()).getString();
				int serverPort = KosmosConfigs.SERVER_PORT.setReference(() -> FlounderNetwork.getSocketClient() == null ? null : FlounderNetwork.getSocketClient().getServerPort()).getInteger();
				FlounderNetwork.startClient(username, serverIP, serverPort);
				PacketConnect loginPacket = new PacketConnect(username);
				loginPacket.writeData(FlounderNetwork.getSocketClient());

				// Generates the player and the world.
				KosmosWorld.generatePlayer();
			}
		});

		// Settings.
		ScreenSettings screenSettings = new ScreenSettings(slider);
		screenSettings.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText settings = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Settings", GuiAlign.CENTRE);
		settings.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenSettings);
			}
		});

		// About.
		ScreenAbout screenAbout = new ScreenAbout(slider);
		screenAbout.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText about = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "About", GuiAlign.CENTRE);
		about.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenAbout);
			}
		});

		// Exit.
		GuiButtonText exitGame = new GuiButtonText(this, new Vector2f(0.5f, yPosition += 1.2f * ySpacing), "Exit To Desktop", GuiAlign.CENTRE);
		exitGame.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				Framework.requestClose();
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
