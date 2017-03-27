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

		ScreenObject paneLeft = new ScreenObject(this, new Vector2f(0.25f, 0.5f), new Vector2f(0.5f, 1.0f)) {
			@Override
			public void updateObject() {
			}

			@Override
			public void deleteObject() {
			}
		};
		paneLeft.setInScreenCoords(true);

		ScreenObject paneRight = new ScreenObject(this, new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 1.0f)) {
			@Override
			public void updateObject() {
			}

			@Override
			public void deleteObject() {
			}
		};
		paneRight.setInScreenCoords(true);

		ScreenSettingGeneral screenGeneral = new ScreenSettingGeneral(slider, this);
		screenGeneral.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText general = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.2f), "General", GuiAlign.CENTRE);
		general.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenGeneral);
			}
		});

		ScreenSettingDebug screenDebug = new ScreenSettingDebug(slider, this);
		screenDebug.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText debug = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.27f), "Debug", GuiAlign.CENTRE);
		debug.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenDebug);
			}
		});

		// TODO: Textbox
		GuiButtonText client = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.34f), "Client", GuiAlign.CENTRE);
		client.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
			}
		});

		ScreenSettingAudio screenAudio = new ScreenSettingAudio(slider, this);
		screenAudio.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText audio = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.20f), "Audio", GuiAlign.CENTRE);
		audio.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenAudio);
			}
		});

		// TODO: Slider, textbox
		GuiButtonText graphics = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.27f), "Graphics", GuiAlign.CENTRE);
		graphics.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
			}
		});

		// TODO: Slider, key grab
		GuiButtonText controls = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.34f), "Controls", GuiAlign.CENTRE);
		controls.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
			}
		});

		GuiButtonText back = new GuiButtonText(this, new Vector2f(0.5f, 0.9f), "Back", GuiAlign.CENTRE);
		back.addLeftListener(new GuiButtonText.ListenerBasic() {
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
