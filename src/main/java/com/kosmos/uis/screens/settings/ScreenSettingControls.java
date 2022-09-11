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
import com.kosmos.camera.*;
import com.kosmos.uis.*;
import com.kosmos.uis.screens.*;

public class ScreenSettingControls extends ScreenObject {
	public ScreenSettingControls(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Title.
		TextObject title = new TextObject(this, new Vector2f(0.5f, 0.1f), "Control Settings", 3.0f, FlounderFonts.CANDARA, 1.0f, GuiAlign.CENTRE);
		title.setInScreenCoords(true);
		title.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		title.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		title.setBorder(new ConstantDriver(0.022f));

		// Slider Crosshair HUD.
		GuiSliderText sliderCrosshairHUD = new GuiSliderText(this, new Vector2f(0.5f, 0.20f), "Crosshair HUD: ", 1.0f, 9.0f, OverlayHUD.getCrosshairSelected(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Integer>(OverlayHUD::getCrosshairSelected) {
			@Override
			public void onEvent(Integer newValue) {
				sliderCrosshairHUD.setText("Crosshair HUD: " + newValue);
				sliderCrosshairHUD.setValue(newValue);
			}
		});
		sliderCrosshairHUD.addChangeListener(() -> OverlayHUD.setCrosshairSelected((int) sliderCrosshairHUD.getValue()));

		// Slider Camera Field Of View.
		GuiSliderText sliderFieldOfView = new GuiSliderText(this, new Vector2f(0.5f, 0.27f), "FOV: ", 30.0f, 120.0f, KosmosCamera.getFieldOfView(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Float>(KosmosCamera::getFieldOfView) {
			@Override
			public void onEvent(Float newValue) {
				sliderFieldOfView.setText("FOV: " + Maths.roundToPlace(newValue, 1));
				sliderFieldOfView.setValue(newValue);
			}
		});
		sliderFieldOfView.addChangeListener(() -> KosmosCamera.setFieldOfView(sliderFieldOfView.getValue()));

		// Slider Camera Sensitivity.
		GuiSliderText sliderSensitivity = new GuiSliderText(this, new Vector2f(0.5f, 0.34f), "Sensitivity: ", 0.1f, 7.0f, KosmosCamera.getSensitivity(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Float>(KosmosCamera::getSensitivity) {
			@Override
			public void onEvent(Float newValue) {
				sliderSensitivity.setText("Sensitivity: " + Maths.roundToPlace(newValue, 2));
				sliderSensitivity.setValue(newValue);
			}
		});
		sliderSensitivity.addChangeListener(() -> KosmosCamera.setSensitivity(sliderSensitivity.getValue()));

		// Key Select Mouse Angle.
		GuiGrabMouse grabAngle = new GuiGrabMouse(this, new Vector2f(0.5f, 0.41f), "Camera Angle Mouse: ", KosmosCamera.getAngleButton(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Integer>(KosmosCamera::getAngleButton) {
			@Override
			public void onEvent(Integer newValue) {
				grabAngle.setValue(newValue);
			}
		});
		grabAngle.addChangeListener(() -> KosmosCamera.setAngleButton(grabAngle.getValue()));

		// Toggle Mouse Lock.
		GuiButtonText toggleMouseLock = new GuiButtonText(this, new Vector2f(0.5f, 0.48f), "Mouse Locked: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(KosmosCamera::isMouseLocked) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleMouseLock.setText("Mouse Locked: " + newValue);
				//	toggleMouseLock.setValue(newValue);
			}
		});
		toggleMouseLock.addLeftListener(() -> KosmosCamera.setMouseLocked(!KosmosCamera.isMouseLocked()));

		// Slider Camera Sensitivity.
		GuiSliderText sliderGuiScale = new GuiSliderText(this, new Vector2f(0.5f, 0.55f), "GUI Scale: ", 0.5f, 2.0f, FlounderGuis.get().getGuiScale(), GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Float>(FlounderGuis.get()::getGuiScale) {
			@Override
			public void onEvent(Float newValue) {
				sliderGuiScale.setText("GUI Scale: " + Maths.roundToPlace(newValue, 2));
				sliderGuiScale.setValue(newValue);
			}
		});
		sliderGuiScale.addChangeListener(() -> FlounderGuis.get().setGuiScale(sliderGuiScale.getValue()));

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
