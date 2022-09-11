/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.uis.screens;

import com.flounder.fonts.*;
import com.flounder.guis.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.visual.*;
import com.kosmos.uis.*;
import com.kosmos.uis.screens.settings.*;

public class ScreenSettings extends ScreenObject {
	public ScreenSettings(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Left and right Panes.
		ScreenObject paneLeft = new ScreenObjectEmpty(this, new Vector2f(0.25f, 0.5f), new Vector2f(0.5f, 1.0f), true);
		ScreenObject paneRight = new ScreenObjectEmpty(this, new Vector2f(0.75f, 0.5f), new Vector2f(0.5f, 1.0f), true);

		// Title.
		TextObject title = new TextObject(this, new Vector2f(0.5f, 0.1f), "Settings", 3.0f, FlounderFonts.CANDARA, 1.0f, GuiAlign.CENTRE);
		title.setInScreenCoords(true);
		title.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		title.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		title.setBorder(new ConstantDriver(0.022f));

		// Screen General.
		ScreenSettingGeneral screenGeneral = new ScreenSettingGeneral(slider, this);
		screenGeneral.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText general = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.2f), "General", GuiAlign.CENTRE);
		general.addLeftListener(() -> slider.setNewSecondaryScreen(screenGeneral));

		// Screen Client.
		ScreenSettingClient screenClient = new ScreenSettingClient(slider, this);
		screenClient.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText client = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.27f), "Client", GuiAlign.CENTRE);
		client.addLeftListener(() -> slider.setNewSecondaryScreen(screenClient));

		// Screen Controls.
		ScreenSettingControls screenControls = new ScreenSettingControls(slider, this);
		screenControls.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText controls = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.34f), "Controls", GuiAlign.CENTRE);
		controls.addLeftListener(() -> slider.setNewSecondaryScreen(screenControls));

		// Screen Audio.
		ScreenSettingAudio screenAudio = new ScreenSettingAudio(slider, this);
		screenAudio.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText audio = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.20f), "Audio", GuiAlign.CENTRE);
		audio.addLeftListener(() -> slider.setNewSecondaryScreen(screenAudio));

		// Screen Graphics.
		ScreenSettingGraphics screenGraphics = new ScreenSettingGraphics(slider, this);
		screenGraphics.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText graphics = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.27f), "Graphics", GuiAlign.CENTRE);
		graphics.addLeftListener(() -> slider.setNewSecondaryScreen(screenGraphics));

		// Screen Post.
		ScreenSettingPost settingPost = new ScreenSettingPost(slider, this);
		settingPost.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText post = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.34f), "Post Effects", GuiAlign.CENTRE);
		post.addLeftListener(() -> slider.setNewSecondaryScreen(settingPost));

		// Back.
		GuiButtonText back = new GuiButtonText(this, new Vector2f(0.5f, 0.9f), "Back", GuiAlign.CENTRE);
		back.addLeftListener(slider::closeSecondaryScreen);
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {

	}
}
