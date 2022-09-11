/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.uis.screens.settings;

import com.flounder.devices.*;
import com.flounder.events.*;
import com.flounder.fonts.*;
import com.flounder.guis.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.visual.*;
import com.kosmos.uis.*;
import com.kosmos.uis.screens.*;

public class ScreenSettingAudio extends ScreenObject {
	public ScreenSettingAudio(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Title.
		TextObject title = new TextObject(this, new Vector2f(0.5f, 0.1f), "Audio Settings", 3.0f, FlounderFonts.CANDARA, 1.0f, GuiAlign.CENTRE);
		title.setInScreenCoords(true);
		title.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		title.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		title.setBorder(new ConstantDriver(0.022f));

		// Toggle Music.
		GuiButtonText toggleMusic = new GuiButtonText(this, new Vector2f(0.5f, 0.20f), "Music Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(FlounderSound.get().getMusicPlayer()::isPaused) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleMusic.setText("Music Enabled: " + !newValue);
				//	toggleMusic.setValue(newValue);
			}
		});
		toggleMusic.addLeftListener(() -> {
			if (!FlounderSound.get().getMusicPlayer().isPaused()) {
				FlounderSound.get().getMusicPlayer().pauseTrack();
			} else {
				FlounderSound.get().getMusicPlayer().unpauseTrack();
			}
		});

		// Slider Music Volume.
		GuiSliderText sliderMusicVolume = new GuiSliderText(this, new Vector2f(0.5f, 0.27f), "Music Volume: ", 0.0f, 1.0f, FlounderSound.get().getMusicPlayer().getVolume(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Float>(FlounderSound.get().getMusicPlayer()::getVolume) {
			@Override
			public void onEvent(Float newValue) {
				sliderMusicVolume.setText("Music Volume: " + Maths.roundToPlace(newValue, 3));
				sliderMusicVolume.setValue(newValue);
			}
		});
		sliderMusicVolume.addChangeListener(() -> FlounderSound.get().getMusicPlayer().setVolume(sliderMusicVolume.getValue()));

		// Slider Sound Volume.
		GuiSliderText sliderSoundVolume = new GuiSliderText(this, new Vector2f(0.5f, 0.34f), "Sound Volume: ", 0.0f, 1.0f, FlounderSound.get().getSourcePool().getSystemVolume(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Float>(FlounderSound.get().getSourcePool()::getSystemVolume) {
			@Override
			public void onEvent(Float newValue) {
				sliderSoundVolume.setText("Sound Volume: " + Maths.roundToPlace(newValue, 3));
				sliderSoundVolume.setValue(newValue);
			}
		});
		sliderSoundVolume.addChangeListener(() -> FlounderSound.get().getSourcePool().setSystemVolume(sliderSoundVolume.getValue()));

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
