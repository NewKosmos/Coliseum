/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.uis.screens;

import com.flounder.fonts.*;
import com.flounder.guis.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.networking.*;
import com.flounder.visual.*;
import com.kosmos.*;
import com.kosmos.camera.*;
import com.kosmos.network.packets.*;
import com.kosmos.uis.*;

public class ScreenMultiplayer extends ScreenObject {
	private String selectedSave;

	public ScreenMultiplayer(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Left and right Panes.
		ScreenObject paneLeft = new ScreenObjectEmpty(this, new Vector2f(0.25f, 0.5f), new Vector2f(0.5f, 1.0f), true);
		ScreenObject paneRight = new ScreenObjectEmpty(this, new Vector2f(0.75f, 0.5f), new Vector2f(0.5f, 1.0f), true);

		// Title.
		TextObject title = new TextObject(this, new Vector2f(0.5f, 0.1f), "Multiplayer", 3.0f, FlounderFonts.CANDARA, 1.0f, GuiAlign.CENTRE);
		title.setInScreenCoords(true);
		title.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		title.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		title.setBorder(new ConstantDriver(0.022f));

		selectedSave = "null";

		ScreenLoading screenLoading = new ScreenLoading(slider);
		screenLoading.setAlphaDriver(new ConstantDriver(0.0f));

		TextObject textTitle = new TextObject(paneRight, new Vector2f(0.75f, 0.20f), "No server selected!", 2.0f, FlounderFonts.CANDARA, 1.6f, GuiAlign.CENTRE);
		textTitle.setColour(new Colour(1.0f, 1.0f, 1.0f));

		TextObject textInfo = new TextObject(paneRight, new Vector2f(0.75f, 0.420f), "Not connected yet!", 1.0f, FlounderFonts.CANDARA, 0.6f, GuiAlign.CENTRE);
		textInfo.setColour(new Colour(1.0f, 1.0f, 1.0f));

		GuiButtonText buttonLoad = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.69f), "Connect To Server", GuiAlign.CENTRE);
		buttonLoad.addLeftListener(() -> {
			slider.setNewSecondaryScreen(screenLoading);
			screenLoading.load(() -> {
				String address = KosmosConfigs.SERVER_IP.setReference(() -> FlounderNetwork.get().getSocketClient() == null ? null : FlounderNetwork.get().getSocketClient().getIpAddress()).getString();
				int port = KosmosConfigs.SERVER_PORT.setReference(() -> FlounderNetwork.get().getSocketClient() == null ? null : FlounderNetwork.get().getSocketClient().getServerPort()).getInteger();

				KosmosConfigs.saveAllConfigs();

				// Connects to the server.
				FlounderNetwork.get().startClient(KosmosPlayer.getUsername(), address, port);
				PacketConnect loginPacket = new PacketConnect(KosmosPlayer.getUsername());
				loginPacket.writeData(FlounderNetwork.get().getSocketClient());

				selectedSave = "null";
				textTitle.setText("null");
				textInfo.setText("Not connected yet!");
			});
		});

		// Save slots.
		for (int i = 1; i <= 7; i++) {
			GuiButtonText button = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.20f + (0.07f * (i - 1))), "Load Server " + i, GuiAlign.CENTRE);
			final String saveName = "Server" + i;

			button.addLeftListener(() -> {
				if (!selectedSave.equals(saveName)) {
					textTitle.setText(saveName);
					selectedSave = saveName;

					textInfo.setText("Not connected yet!");
				}
			});

			if (i == 1) {
				button.getListenerLeft().eventOccurred();
			}
		}

		// Back.
		GuiButtonText back = new GuiButtonText(this, new Vector2f(0.5f, 0.9f), "Back", GuiAlign.CENTRE);
		back.addLeftListener(slider::closeSecondaryScreen);
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {

	}
}
