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
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isEffectsEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleEffects.setText("Post Enabled: " + newValue);
			}
		});
		toggleEffects.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.get().setEffectsEnabled(!KosmosPost.get().isEffectsEnabled());
			}
		});

		// Toggle Bloom.
		GuiButtonText toggleBloom = new GuiButtonText(this, new Vector2f(0.5f, 0.27f), "Bloom Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isBloomEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleBloom.setText("Bloom Enabled: " + newValue);
			}
		});
		toggleBloom.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.get().setBloomEnabled(!KosmosPost.get().isBloomEnabled());
			}
		});

		// Toggle Motion Blur.
		GuiButtonText toggleMotionBlur = new GuiButtonText(this, new Vector2f(0.5f, 0.34f), "Motion Blur Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isMotionBlurEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleMotionBlur.setText("Motion Blur Enabled: " + newValue);
			}
		});
		toggleMotionBlur.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.get().setMotionBlurEnabled(!KosmosPost.get().isMotionBlurEnabled());
			}
		});

		// Toggle Tilt Shift.
		GuiButtonText toggleTiltShift = new GuiButtonText(this, new Vector2f(0.5f, 0.41f), "Tilt Shift Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isTiltShiftEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleTiltShift.setText("Tilt Shift Enabled: " + newValue);
			}
		});
		toggleTiltShift.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.get().setTiltShiftEnabled(!KosmosPost.get().isTiltShiftEnabled());
			}
		});

		// Toggle Lens Flare.
		GuiButtonText toggleLensFlare = new GuiButtonText(this, new Vector2f(0.5f, 0.48f), "Lens Flare Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isLensFlareEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleLensFlare.setText("Lens Flare Enabled: " + newValue);
			}
		});
		toggleLensFlare.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.get().setLensFlareEnabled(!KosmosPost.get().isLensFlareEnabled());
			}
		});

		// Toggle Effect CRT.
		GuiButtonText toggleEffectCRT = new GuiButtonText(this, new Vector2f(0.5f, 0.55f), "Effect CRT Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isCrtEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleEffectCRT.setText("Effect CRT Enabled: " + newValue);
			}
		});
		toggleEffectCRT.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.get().setCrtEnabled(!KosmosPost.get().isCrtEnabled());
			}
		});

		// Toggle Effect Grain.
		GuiButtonText toggleGrain = new GuiButtonText(this, new Vector2f(0.5f, 0.62f), "Grain Enabled: ", GuiAlign.CENTRE);
		FlounderEvents.get().addEvent(new EventChange<Boolean>(() -> KosmosPost.get().isGrainEnabled()) {
			@Override
			public void onEvent(Boolean newValue) {
				toggleGrain.setText("Grain Enabled: " + newValue);
			}
		});
		toggleGrain.addLeftListener(new ScreenListener() {
			@Override
			public void eventOccurred() {
				KosmosPost.get().setGrainEnabled(!KosmosPost.get().isGrainEnabled());
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
