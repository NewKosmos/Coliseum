/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.uis.screens.settings;

import flounder.events.*;
import flounder.guis.*;
import flounder.maths.vectors.*;
import kosmos.uis.*;
import kosmos.uis.screens.*;
import kosmos.world.chunks.*;

public class ScreenSettingGeneral extends ScreenObject {
	public ScreenSettingGeneral(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Slider Chunk Distance.
		GuiSliderText sliderChunkDistance = new GuiSliderText(this, new Vector2f(0.5f, 0.20f), "Chunk Distance: ", 1.0f, 16.0f, KosmosChunks.get().getChunkDistance(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Integer>(KosmosChunks.get()::getChunkDistance) {
			@Override
			public void onEvent(Integer newValue) {
				sliderChunkDistance.setText("Chunk Distance: " + newValue);
			}
		});
		sliderChunkDistance.addChangeListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				if (KosmosChunks.get().getChunkDistance() != (int) sliderChunkDistance.getProgress()) {
					KosmosChunks.get().setChunkDistance((int) sliderChunkDistance.getProgress());
					KosmosChunks.get().clear(true);
				}
			}
		});

		// Back.
		GuiButtonText back = new GuiButtonText(this, new Vector2f(0.5f, 0.9f), "Back", GuiAlign.CENTRE);
		back.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(settings);
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
