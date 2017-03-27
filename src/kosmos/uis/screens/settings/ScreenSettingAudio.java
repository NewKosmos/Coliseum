/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis.screens.settings;

import flounder.devices.*;
import flounder.events.*;
import flounder.guis.*;
import flounder.maths.vectors.*;
import kosmos.uis.*;
import kosmos.uis.screens.*;

public class ScreenSettingAudio extends ScreenObject {
	public ScreenSettingAudio(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Toggle Music.
		GuiButtonText toggleMusic = new GuiButtonText(this, new Vector2f(0.5f, 0.20f), "Music Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(FlounderSound.getMusicPlayer()::isPaused) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleMusic.setText("Music Enabled: " + !newValue);
			}
		});
		toggleMusic.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				if (!FlounderSound.getMusicPlayer().isPaused()) {
					FlounderSound.getMusicPlayer().pauseTrack();
				} else {
					FlounderSound.getMusicPlayer().unpauseTrack();
				}
			}
		});

		// Slider Music Volume.

		// Slider Sound Volume.

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
