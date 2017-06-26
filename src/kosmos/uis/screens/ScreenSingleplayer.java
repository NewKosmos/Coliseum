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
import flounder.guis.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.uis.*;
import kosmos.world.*;

import java.util.*;

public class ScreenSingleplayer extends ScreenObject {
	private String selectedSave;

	public ScreenSingleplayer(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Left and right Panes.
		ScreenObject paneLeft = new ScreenObjectEmpty(this, new Vector2f(0.25f, 0.5f), new Vector2f(0.5f, 1.0f), true);
		ScreenObject paneRight = new ScreenObjectEmpty(this, new Vector2f(0.75f, 0.5f), new Vector2f(0.5f, 1.0f), true);

		// Title.
		TextObject title = new TextObject(this, new Vector2f(0.5f, 0.1f), "Singleplayer", 3.0f, FlounderFonts.CANDARA, 1.0f, GuiAlign.CENTRE);
		title.setInScreenCoords(true);
		title.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		title.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		title.setBorder(new ConstantDriver(0.022f));

		selectedSave = "null";

		ScreenLoading screenLoading = new ScreenLoading(slider);
		screenLoading.setAlphaDriver(new ConstantDriver(0.0f));

		TextObject textTitle = new TextObject(paneRight, new Vector2f(0.75f, 0.20f), "No save selected!", 2.0f, FlounderFonts.CANDARA, 1.6f, GuiAlign.CENTRE);
		textTitle.setColour(new Colour(1.0f, 1.0f, 1.0f));

		TextObject textInfo = new TextObject(paneRight, new Vector2f(0.75f, 0.420f), "Not created yet!", 1.0f, FlounderFonts.CANDARA, 0.6f, GuiAlign.CENTRE);
		textInfo.setColour(new Colour(1.0f, 1.0f, 1.0f));

		GuiButtonText buttonLoad = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.69f), "Create Save", GuiAlign.CENTRE);
		buttonLoad.addLeftListener(() -> {
			slider.setNewSecondaryScreen(screenLoading);
			screenLoading.load(() -> {
				FlounderLogger.get().log("Loading: " + selectedSave);
				WorldDefinition world = WorldDefinition.load(selectedSave);

				if (world == null) {
					world = new WorldDefinition(selectedSave, (int) Maths.randomInRange(1.0, 1000000.0), 1536, 400.0f, 40.0f, 40.0f, 0.8f, 1.0f, 0.4f, 600.0f, 0.7f, new HashMap<>(), new HashMap<>());
				}

				if (!world.getPlayers().containsKey("this")) {
					world.getPlayers().put("this", new Pair<>(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f)));
				}

				// Generates the world.
				KosmosConfigs.saveAllConfigs();
				Vector3f thisPosition = world.getPlayerPosition("this");
				Vector3f thisChunk = world.getPlayerChunk("this");
				KosmosWorld.get().generateWorld(world, new Vector3f(thisPosition), new Vector3f(thisChunk));

				selectedSave = "null";
				textTitle.setText("null");
				textInfo.setText("Not created yet!");
				buttonLoad.setText("Create Save");
			});
		});

		// Save slots.
		for (int i = 1; i <= 7; i++) {
			GuiButtonText button = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.20f + (0.07f * (i - 1))), "Load Save " + i, GuiAlign.CENTRE);
			final String saveName = "Save" + i;

			button.addLeftListener(() -> {
				if (!selectedSave.equals(saveName)) {
					textTitle.setText(saveName);
					selectedSave = saveName;
					WorldDefinition world = WorldDefinition.load(selectedSave);

					if (world != null) {
						textInfo.setText(world.toString());
						buttonLoad.setText("Load Save");
					} else {
						textInfo.setText("Not created yet!");
						buttonLoad.setText("Create Save");
					}
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
