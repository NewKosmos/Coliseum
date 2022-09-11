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
import com.kosmos.post.*;
import com.kosmos.uis.*;
import com.kosmos.uis.screens.*;

public class ScreenSettingPost extends ScreenObject {
	public ScreenSettingPost(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Title.
		TextObject title = new TextObject(this, new Vector2f(0.5f, 0.1f), "Post-Effect Settings", 3.0f, FlounderFonts.CANDARA, 1.0f, GuiAlign.CENTRE);
		title.setInScreenCoords(true);
		title.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		title.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		title.setBorder(new ConstantDriver(0.022f));

		// Toggle Effects.
		GuiButtonText toggleEffects = new GuiButtonText(this, new Vector2f(0.5f, 0.20f), "Post Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isEffectsEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleEffects.setText("Post Enabled: " + newValue);
				//	toggleEffects.setValue(newValue);
			}
		});
		toggleEffects.addLeftListener(() -> KosmosPost.get().setEffectsEnabled(!KosmosPost.get().isEffectsEnabled()));

		// Toggle Bloom.
		GuiButtonText toggleBloom = new GuiButtonText(this, new Vector2f(0.5f, 0.27f), "Bloom Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isBloomEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleBloom.setText("Bloom Enabled: " + newValue);
				//	toggleBloom.setValue(newValue);
			}
		});
		toggleBloom.addLeftListener(() -> KosmosPost.get().setBloomEnabled(!KosmosPost.get().isBloomEnabled()));

		// Toggle Motion Blur.
		GuiButtonText toggleMotionBlur = new GuiButtonText(this, new Vector2f(0.5f, 0.34f), "Motion Blur Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isMotionBlurEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleMotionBlur.setText("Motion Blur Enabled: " + newValue);
				//	toggleMotionBlur.setValue(newValue);
			}
		});
		toggleMotionBlur.addLeftListener(() -> KosmosPost.get().setMotionBlurEnabled(!KosmosPost.get().isMotionBlurEnabled()));

		// Toggle Tilt Shift.
		GuiButtonText toggleTiltShift = new GuiButtonText(this, new Vector2f(0.5f, 0.41f), "Tilt Shift Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isTiltShiftEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleTiltShift.setText("Tilt Shift Enabled: " + newValue);
				//	toggleTiltShift.setValue(newValue);
			}
		});
		toggleTiltShift.addLeftListener(() -> KosmosPost.get().setTiltShiftEnabled(!KosmosPost.get().isTiltShiftEnabled()));

		// Toggle Lens Flare.
		GuiButtonText toggleLensFlare = new GuiButtonText(this, new Vector2f(0.5f, 0.48f), "Lens Flare Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isLensFlareEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleLensFlare.setText("Lens Flare Enabled: " + newValue);
				//	toggleLensFlare.setValue(newValue);
			}
		});
		toggleLensFlare.addLeftListener(() -> KosmosPost.get().setLensFlareEnabled(!KosmosPost.get().isLensFlareEnabled()));

		// Toggle Effect CRT.
		GuiButtonText toggleEffectCRT = new GuiButtonText(this, new Vector2f(0.5f, 0.55f), "Effect CRT Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isCrtEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleEffectCRT.setText("Effect CRT Enabled: " + newValue);
				//	toggleEffectCRT.setValue(newValue);
			}
		});
		toggleEffectCRT.addLeftListener(() -> KosmosPost.get().setCrtEnabled(!KosmosPost.get().isCrtEnabled()));

		// Toggle Effect Grain.
		GuiButtonText toggleGrain = new GuiButtonText(this, new Vector2f(0.5f, 0.62f), "Grain Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isGrainEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleGrain.setText("Grain Enabled: " + newValue);
				//	toggleGrain.setValue(newValue);
			}
		});
		toggleGrain.addLeftListener(() -> KosmosPost.get().setGrainEnabled(!KosmosPost.get().isGrainEnabled()));

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
