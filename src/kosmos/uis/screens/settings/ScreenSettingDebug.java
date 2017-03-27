/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis.screens.settings;

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

		GuiButtonText toggleProfiler = new GuiButtonText(this, new Vector2f(0.5f, 0.20f), "Developer Profiler: " + FlounderProfiler.isOpen(), GuiAlign.CENTRE);
		toggleProfiler.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				FlounderProfiler.toggle(!FlounderProfiler.isOpen());
				toggleProfiler.setText("Developer Profiler: " + FlounderProfiler.isOpen());
			}
		});

		GuiButtonText toggleBoundings = new GuiButtonText(this, new Vector2f(0.5f, 0.27f), "Render Boundings: " + FlounderBounding.renders(), GuiAlign.CENTRE);
		toggleBoundings.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				FlounderBounding.toggle(!FlounderBounding.renders());
				toggleBoundings.setText("Render Boundings: " + FlounderBounding.renders());
			}
		});

		GuiButtonText toggleWireframe = new GuiButtonText(this, new Vector2f(0.5f, 0.34f), "Wireframe Mode: " + OpenGlUtils.isInWireframe(), GuiAlign.CENTRE);
		toggleWireframe.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				OpenGlUtils.goWireframe(!OpenGlUtils.isInWireframe());
				toggleWireframe.setText("Wireframe Mode: " + OpenGlUtils.isInWireframe());
			}
		});

		GuiButtonText back = new GuiButtonText(this, new Vector2f(0.5f, 0.9f), "Back", GuiAlign.CENTRE);
		back.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(settings, false);
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
