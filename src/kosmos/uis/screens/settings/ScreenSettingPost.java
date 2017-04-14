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
import flounder.maths.vectors.*;
import kosmos.post.*;
import kosmos.uis.*;
import kosmos.uis.screens.*;

public class ScreenSettingPost extends ScreenObject {
	public ScreenSettingPost(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Toggle Effects.
		GuiButtonText toggleEffects = new GuiButtonText(this, new Vector2f(0.5f, 0.20f), "Post Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(KosmosPost::isEffectsEnabled) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleEffects.setText("Post Enabled: " + newValue);
			}
		});
		toggleEffects.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.setEffectsEnabled(!KosmosPost.isEffectsEnabled());
			}
		});

		// Toggle Bloom.
		GuiButtonText toggleBloom = new GuiButtonText(this, new Vector2f(0.5f, 0.27f), "Bloom Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(KosmosPost::isBloomEnabled) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleBloom.setText("Bloom Enabled: " + newValue);
			}
		});
		toggleBloom.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.setBloomEnabled(!KosmosPost.isBloomEnabled());
			}
		});

		// Toggle Motion Blur.
		GuiButtonText toggleMotionBlur = new GuiButtonText(this, new Vector2f(0.5f, 0.34f), "Motion Blur Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(KosmosPost::isMotionBlurEnabled) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleMotionBlur.setText("Motion Blur Enabled: " + newValue);
			}
		});
		toggleMotionBlur.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.setMotionBlurEnabled(!KosmosPost.isMotionBlurEnabled());
			}
		});

		// Toggle Tilt Shift.
		GuiButtonText toggleTiltShift = new GuiButtonText(this, new Vector2f(0.5f, 0.41f), "Tilt Shift Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(KosmosPost::isTiltShiftEnabled) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleTiltShift.setText("Tilt Shift Enabled: " + newValue);
			}
		});
		toggleTiltShift.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.setTiltShiftEnabled(!KosmosPost.isTiltShiftEnabled());
			}
		});

		// Toggle Lens Flare.
		GuiButtonText toggleLensFlare = new GuiButtonText(this, new Vector2f(0.5f, 0.48f), "Lens Flare Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(KosmosPost::isLensFlareEnabled) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleLensFlare.setText("Lens Flare Enabled: " + newValue);
			}
		});
		toggleLensFlare.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.setLensFlareEnabled(!KosmosPost.isLensFlareEnabled());
			}
		});

		// Toggle Effect CRT.
		GuiButtonText toggleEffectCRT = new GuiButtonText(this, new Vector2f(0.5f, 0.55f), "Effect CRT Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(KosmosPost::isCrtEnabled) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleEffectCRT.setText("Effect CRT Enabled: " + newValue);
			}
		});
		toggleEffectCRT.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.setCrtEnabled(!KosmosPost.isCrtEnabled());
			}
		});

		// Toggle Effect Grain.
		GuiButtonText toggleGrain = new GuiButtonText(this, new Vector2f(0.5f, 0.62f), "Grain Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.addEvent(new EventChange<Boolean>(KosmosPost::isGrainEnabled) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleGrain.setText("Grain Enabled: " + newValue);
			}
		});
		toggleGrain.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.setGrainEnabled(!KosmosPost.isGrainEnabled());
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
