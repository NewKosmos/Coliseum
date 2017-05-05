/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis.screens.settings;

import flounder.events.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;
import kosmos.uis.*;
import kosmos.uis.screens.*;

public class ScreenSettingDebug extends ScreenObject {
	public ScreenSettingDebug(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Toggle Profiler.
		GuiButtonText toggleProfiler = new GuiButtonText(this, new Vector2f(0.5f, 0.20f), "Developer Profiler: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(FlounderProfiler.get()::isOpen) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleProfiler.setText("Developer Profiler: " + newValue);
			}
		});
		toggleProfiler.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				FlounderProfiler.get().toggle(!FlounderProfiler.get().isOpen());
			}
		});

		// Toggle Wireframe.
		GuiButtonText toggleWireframe = new GuiButtonText(this, new Vector2f(0.5f, 0.27f), "Wireframe Mode: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> FlounderOpenGL.get().isInWireframe()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleWireframe.setText("Wireframe Mode: " + newValue);
			}
		});
		toggleWireframe.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				FlounderOpenGL.get().goWireframe(!FlounderOpenGL.get().isInWireframe());
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
