/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.uis.screens;

import flounder.devices.*;
import flounder.events.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import kosmos.*;
import kosmos.uis.*;
import kosmos.uis.screens.*;
import kosmos.world.*;
import kosmos.world.chunks.*;

import java.util.*;

public class ScreenSingleplayer extends ScreenObject {
	public ScreenSingleplayer(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		for (int i = 1; i <= 5; i++) {
			GuiButtonText button = new GuiButtonText(this, new Vector2f(0.5f, 0.20f + (0.07f * i)), "Load Save " + i, GuiAlign.CENTRE);

			final String saveName = "Save" + i;
			final int seed = 1000 + (i * 1000 - (i * i * 30));

			button.addLeftListener(() -> {
				FlounderLogger.get().log("Loading: " + saveName);
				WorldDefinition world = WorldDefinition.load(saveName);

				if (world == null) {
					world = new WorldDefinition(saveName, seed, 1536, 400.0f, 40.0f, 40.0f, 0.8f, 1.0f, 0.4f, 600.0f, 0.7f, new HashMap<>(), new HashMap<>());
				}

				if (!world.getPlayers().containsKey("this")) {
					world.getPlayers().put("this", new Pair<>(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f)));
				}

				loadSave(world, slider);
			});
		}

		// Back.
		GuiButtonText back = new GuiButtonText(this, new Vector2f(0.5f, 0.9f), "Back", GuiAlign.CENTRE);
		back.addLeftListener(slider::closeSecondaryScreen);
	}

	private void loadSave(WorldDefinition world, OverlaySlider slider) {
		slider.sliderStartMenu(false);
		KosmosConfigs.saveAllConfigs();

		Vector3f thisPosition = world.getPlayerPosition("this");
		Vector3f thisChunk = world.getPlayerChunk("this");

		// Generates the world.
		KosmosWorld.get().generateWorld(world, new Vector3f(thisPosition), new Vector3f(thisChunk));

		// Forces slider to close after loading the save.
		slider.closeSecondaryScreen();
		((KosmosGuis) FlounderGuis.get().getGuiMaster()).togglePause(true);
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {

	}
}
