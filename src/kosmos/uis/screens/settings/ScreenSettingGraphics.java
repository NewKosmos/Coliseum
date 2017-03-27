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
import kosmos.water.*;

public class ScreenSettingGraphics extends ScreenObject {
	public ScreenSettingGraphics(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Left and right Panes.
		ScreenObject paneLeft = new ScreenObjectEmpty(this, new Vector2f(0.25f, 0.5f), new Vector2f(0.5f, 1.0f), true);
		ScreenObject paneRight = new ScreenObjectEmpty(this, new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 1.0f), true);

		// Toggle Antialiasing.
		GuiButtonText toggleAntialiasing = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.20f), "Is Antialiasing: " + FlounderDisplay.isAntialiasing(), GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(FlounderDisplay::isAntialiasing) {
			@Override
			public void onEvent() {
				toggleAntialiasing.setText("Is Antialiasing: " + FlounderDisplay.isAntialiasing());
			}
		});
		toggleAntialiasing.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				FlounderDisplay.setAntialiasing(!FlounderDisplay.isAntialiasing());
			}
		});

		// Toggle Fullscreen.
		GuiButtonText toggleFullscreen = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.27f), "Is Fullscreen: " + FlounderDisplay.isFullscreen(), GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(FlounderDisplay::isFullscreen) {
			@Override
			public void onEvent() {
				toggleFullscreen.setText("Is Fullscreen: " + FlounderDisplay.isFullscreen());
			}
		});
		toggleFullscreen.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				FlounderDisplay.setFullscreen(!FlounderDisplay.isFullscreen());
			}
		});

		// Toggle Water Reflections.
		GuiButtonText toggleWaterReflections = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.20f), "Water Reflections: " + KosmosWater.reflectionsEnabled(), GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(KosmosWater::reflectionsEnabled) {
			@Override
			public void onEvent() {
				toggleWaterReflections.setText("Water Reflections: " + KosmosWater.reflectionsEnabled());
			}
		});
		toggleWaterReflections.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				KosmosWater.setReflectionsEnabled(!KosmosWater.reflectionsEnabled());
			}
		});

		// Toggle Water Reflection Shadows.
		GuiButtonText toggleWaterReflectionShadows = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.27f), "Water Reflection Shadows: " + KosmosWater.reflectionShadows(), GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(KosmosWater::reflectionShadows) {
			@Override
			public void onEvent() {
				toggleWaterReflectionShadows.setText("Water Reflection Shadows: " + KosmosWater.reflectionShadows());
			}
		});
		toggleWaterReflectionShadows.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				KosmosWater.setReflectionShadows(!KosmosWater.reflectionShadows());
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
