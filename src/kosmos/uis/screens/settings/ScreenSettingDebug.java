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
import flounder.physics.bounding.*;
import flounder.profiling.*;
import kosmos.uis.*;
import kosmos.uis.screens.*;

public class ScreenSettingDebug extends ScreenObject {
	public ScreenSettingDebug(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Toggle Profiler.
		GuiButtonText toggleProfiler = new GuiButtonText(this, new Vector2f(0.5f, 0.20f), "Developer Profiler: " + FlounderProfiler.isOpen(), GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(FlounderProfiler::isOpen) {
			@Override
			public void onEvent() {
				toggleProfiler.setText("Developer Profiler: " + FlounderProfiler.isOpen());
			}
		});
		toggleProfiler.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				FlounderProfiler.toggle(!FlounderProfiler.isOpen());
			}
		});

		// Toggle Boundings.
		GuiButtonText toggleBoundings = new GuiButtonText(this, new Vector2f(0.5f, 0.27f), "Render Boundings: " + FlounderBounding.renders(), GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(FlounderBounding::renders) {
			@Override
			public void onEvent() {
				toggleBoundings.setText("Render Boundings: " + FlounderBounding.renders());
			}
		});
		toggleBoundings.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				FlounderBounding.toggle(!FlounderBounding.renders());
			}
		});

		// Toggle Wireframe.
		GuiButtonText toggleWireframe = new GuiButtonText(this, new Vector2f(0.5f, 0.34f), "Wireframe Mode: " + OpenGlUtils.isInWireframe(), GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(OpenGlUtils::isInWireframe) {
			@Override
			public void onEvent() {
				toggleWireframe.setText("Wireframe Mode: " + OpenGlUtils.isInWireframe());
			}
		});
		toggleWireframe.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				OpenGlUtils.goWireframe(!OpenGlUtils.isInWireframe());
			}
		});

		// Back.
		GuiButtonText back = new GuiButtonText(this, new Vector2f(0.5f, 0.9f), "Back", GuiAlign.CENTRE);
		back.addLeftListener(new GuiButtonText.ListenerBasic() {
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
