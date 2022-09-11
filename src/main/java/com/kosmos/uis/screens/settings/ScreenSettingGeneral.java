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
import com.kosmos.post.*;
import com.kosmos.uis.*;
import com.kosmos.uis.screens.*;
import com.kosmos.world.chunks.*;

public class ScreenSettingGeneral extends ScreenObject {
	public ScreenSettingGeneral(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Title.
		TextObject title = new TextObject(this, new Vector2f(0.5f, 0.1f), "General Settings", 3.0f, FlounderFonts.CANDARA, 1.0f, GuiAlign.CENTRE);
		title.setInScreenCoords(true);
		title.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		title.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		title.setBorder(new ConstantDriver(0.022f));

		// Slider Chunk Distance.
		GuiSliderText sliderChunkDistance = new GuiSliderText(this, new Vector2f(0.5f, 0.20f), "Chunk Distance: ", 1.0f, 16.0f, KosmosChunks.get().getChunkDistance(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Integer>(KosmosChunks.get()::getChunkDistance) {
			@Override
			public void onEvent(Integer newValue) {
				sliderChunkDistance.setText("Chunk Distance: " + newValue);
				sliderChunkDistance.setValue(newValue);
			}
		});
		sliderChunkDistance.addChangeListener(() -> {
			if (KosmosChunks.get().getChunkDistance() != (int) sliderChunkDistance.getValue()) {
				KosmosChunks.get().setChunkDistance((int) sliderChunkDistance.getValue());
				KosmosChunks.get().clear(true);
			}
		});

		// Toggle Music.
		GuiButtonText toggleBranding = new GuiButtonText(this, new Vector2f(0.5f, 0.27f), "Branding Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(KosmosPost.get()::isBrandingEnabled) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleBranding.setText("Branding Enabled: " + !newValue);
				//	toggleBranding.setValue(newValue);
			}
		});
		toggleBranding.addLeftListener(() -> {
			KosmosPost.get().setBrandingEnabled(!KosmosPost.get().isBrandingEnabled());
		});

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
