/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis.screens;

import flounder.guis.*;
import flounder.maths.vectors.*;
import flounder.visual.*;
import kosmos.uis.*;
import kosmos.uis.screens.settings.*;

public class ScreenSettings extends ScreenObject {
	public ScreenSettings(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Left and right Panes.
		ScreenObject paneLeft = new ScreenObjectEmpty(this, new Vector2f(0.25f, 0.5f), new Vector2f(0.5f, 1.0f), true);
		ScreenObject paneRight = new ScreenObjectEmpty(this, new Vector2f(0.75f, 0.5f), new Vector2f(0.5f, 1.0f), true);

		// Screen General.
		ScreenSettingGeneral screenGeneral = new ScreenSettingGeneral(slider, this);
		screenGeneral.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText general = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.2f), "General", GuiAlign.CENTRE);
		general.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenGeneral);
			}
		});

		// Screen Debug.
		ScreenSettingDebug screenDebug = new ScreenSettingDebug(slider, this);
		screenDebug.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText debug = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.27f), "Debug", GuiAlign.CENTRE);
		debug.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenDebug);
			}
		});

		// Screen Client.
		ScreenSettingClient screenClient = new ScreenSettingClient(slider, this);
		screenClient.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText client = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.34f), "Client", GuiAlign.CENTRE);
		client.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenClient);
			}
		});

		// Screen Audio.
		ScreenSettingAudio screenAudio = new ScreenSettingAudio(slider, this);
		screenAudio.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText audio = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.20f), "Audio", GuiAlign.CENTRE);
		audio.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenAudio);
			}
		});

		// Screen Graphics.
		ScreenSettingGraphics screenGraphics = new ScreenSettingGraphics(slider, this);
		screenGraphics.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText graphics = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.27f), "Graphics", GuiAlign.CENTRE);
		graphics.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenGraphics);
			}
		});

		// Screen Controls.
		ScreenSettingControls screenControls = new ScreenSettingControls(slider, this);
		screenControls.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText controls = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.34f), "Controls", GuiAlign.CENTRE);
		controls.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenControls);
			}
		});

		// Back.
		GuiButtonText back = new GuiButtonText(this, new Vector2f(0.5f, 0.9f), "Back", GuiAlign.CENTRE);
		back.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				slider.closeSecondaryScreen();
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
