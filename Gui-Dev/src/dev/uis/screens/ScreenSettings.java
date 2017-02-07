/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package dev.uis.screens;

import dev.uis.*;
import flounder.events.*;
import flounder.guis.*;

import java.util.*;

public class ScreenSettings extends GuiComponent {
	private MasterSlider masterSlider;

	private GuiComponent leftPane;
	private GuiComponent rightPane;

	public ScreenSettings(MasterSlider masterSlider) {
		this.masterSlider = masterSlider;

		this.leftPane = new EmptyGuiComponent();
		this.rightPane = new EmptyGuiComponent();
		addComponent(leftPane, 0.0f, 0.0f, 0.5f, 1.0f);
		addComponent(rightPane, 0.5f, 0.0f, 0.5f, 1.0f);

		createAudioOption(0.1f, leftPane);
		createDevelopersOption(0.2f, leftPane);
		createGraphicsOption(0.3f, leftPane);
		createEffectsOption(0.1f, rightPane);
		createInputsOption(0.2f, rightPane);
		createBackOption(0.9f);

		super.show(false);

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return ScreenSettings.super.isShown() && MasterSlider.BACK_KEY.wasDown();
			}

			@Override
			public void onEvent() {
				masterSlider.closeSecondaryScreen();
			}
		});
	}

	private void createAudioOption(float yPos, GuiComponent component) {
		GuiTextButton button = MasterSlider.createButton("Audio", yPos, component);
		//	button.addLeftListener(() -> masterSlider.setNewSecondaryScreen(screenOptionsAudio, true));
	}

	private void createDevelopersOption(float yPos, GuiComponent component) {
		GuiTextButton button = MasterSlider.createButton("Developer", yPos, component);
		//	button.addLeftListener(() -> masterSlider.setNewSecondaryScreen(screenOptionsDeveloper, true));
	}

	private void createGraphicsOption(float yPos, GuiComponent component) {
		GuiTextButton button = MasterSlider.createButton("Graphics", yPos, component);
		//	button.addLeftListener(() -> masterSlider.setNewSecondaryScreen(screenOptionsGraphics, true));
	}

	private void createEffectsOption(float yPos, GuiComponent component) {
		GuiTextButton button = MasterSlider.createButton("Effects", yPos, component);
		//	button.addLeftListener(() -> masterSlider.setNewSecondaryScreen(screenOptionsEffects, true));
	}

	private void createInputsOption(float yPos, GuiComponent component) {
		GuiTextButton button = MasterSlider.createButton("Inputs", yPos, component);
		//	button.addLeftListener(() -> masterSlider.setNewSecondaryScreen(screenOptionsInputs, true));
	}

	private void createBackOption(float yPos) {
		GuiTextButton button = MasterSlider.createButton("Back", yPos, this);
		button.addLeftListener(masterSlider::closeSecondaryScreen);
	}

	@Override
	protected void updateSelf() {
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
	}
}
