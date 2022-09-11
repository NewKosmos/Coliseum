/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.uis.screens;

import com.flounder.devices.*;
import com.flounder.fonts.*;
import com.flounder.guis.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.resources.*;
import com.flounder.textures.*;
import com.flounder.visual.*;
import com.kosmos.*;
import com.kosmos.uis.*;

public class ScreenLoading extends ScreenObject {
	private OverlaySlider slider;
	private Timer loadTimer;
	private LoadFunction function;
	private GuiObject background;
	private GuiObject logo;

	public ScreenLoading(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.slider = slider;

		// Title.
		TextObject title = new TextObject(this, new Vector2f(0.5f, 0.9f), "A world is currently being loaded, this may take a few seconds...", 1.5f, FlounderFonts.CANDARA, 1.0f, GuiAlign.CENTRE);
		title.setInScreenCoords(true);
		title.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		title.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		title.setBorder(new ConstantDriver(0.022f));

		this.background = new GuiObject(this, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "eg_background.png")).create(), 1);
		this.background.setInScreenCoords(true);

		this.logo = new GuiObject(this, new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f), TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "icon", "icon.png")).create(), 1);
		this.logo.setInScreenCoords(true);
		this.logo.setScaleDriver(new SinWaveDriver(0.75f, 1.0f, 2.0f));
		this.logo.setRotationDriver(new SinWaveDriver(-20.0f, 20.0f, 3.0f));

		this.loadTimer = new Timer(3.0f);
		this.function = null;
	}

	@Override
	public void updateObject() {
		this.background.getDimensions().x = FlounderDisplay.get().getAspectRatio();
		this.background.setVisible(true);

		if (loadTimer.isPassedTime()) {
			slider.sliderStartMenu(false);

			if (function != null) {
				function.load();
				function = null;
			}

			// Forces slider to close after loading the save.
			slider.closeSecondaryScreen();

			((KosmosGuis) FlounderGuis.get().getGuiMaster()).togglePause(true);

			loadTimer.resetStartTime();
		}
	}

	public void load(LoadFunction function) {
		this.function = function;
		loadTimer.resetStartTime();
	}

	@Override
	public void deleteObject() {

	}

	@FunctionalInterface
	public interface LoadFunction {
		void load();
	}
}
