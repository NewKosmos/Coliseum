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
import flounder.maths.*;
import flounder.maths.vectors.*;
import kosmos.camera.*;
import kosmos.uis.*;
import kosmos.uis.screens.*;

public class ScreenSettingControls extends ScreenObject {
	public ScreenSettingControls(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Slider Crosshair HUD.
		GuiSliderText sliderCrosshairHUD = new GuiSliderText(this, new Vector2f(0.5f, 0.20f), "Crosshair HUD: ", 1.0f, 9.0f, OverlayHUD.getCrosshairSelected(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Integer>(OverlayHUD::getCrosshairSelected) {
			@Override
			public void onEvent(Integer newValue) {
				sliderCrosshairHUD.setText("Crosshair HUD: " + newValue);
			}
		});
		sliderCrosshairHUD.addChangeListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				OverlayHUD.setCrosshairSelected((int) sliderCrosshairHUD.getProgress());
			}
		});

		// Slider Camera Field Of View.
		GuiSliderText sliderFieldOfView = new GuiSliderText(this, new Vector2f(0.5f, 0.27f), "FOV: ", 30.0f, 120.0f, KosmosCamera.getFieldOfView(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Float>(KosmosCamera::getFieldOfView) {
			@Override
			public void onEvent(Float newValue) {
				sliderFieldOfView.setText("FOV: " + Maths.roundToPlace(newValue, 1));
			}
		});
		sliderFieldOfView.addChangeListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosCamera.setFieldOfView(sliderFieldOfView.getProgress());
			}
		});

		// Slider Camera Sensitivity.
		GuiSliderText sliderSensitivity = new GuiSliderText(this, new Vector2f(0.5f, 0.34f), "Sensitivity: ", 0.1f, 7.0f, KosmosCamera.getSensitivity(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Float>(KosmosCamera::getSensitivity) {
			@Override
			public void onEvent(Float newValue) {
				sliderSensitivity.setText("Sensitivity: " + Maths.roundToPlace(newValue, 2));
			}
		});
		sliderSensitivity.addChangeListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosCamera.setSensitivity(sliderSensitivity.getProgress());
			}
		});

		// Key Select Mouse Reangle.

		// Toggle Mouse Lock.
		GuiButtonText toggleMouseLock = new GuiButtonText(this, new Vector2f(0.5f, 0.41f), "Mouse Locked: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(KosmosCamera::isMouseLocked) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleMouseLock.setText("Mouse Locked: " + newValue);
			}
		});
		toggleMouseLock.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosCamera.setMouseLocked(!KosmosCamera.isMouseLocked());
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
