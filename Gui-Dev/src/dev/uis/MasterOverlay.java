/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package dev.uis;

import flounder.fonts.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.visual.*;

import java.util.*;
import java.util.Timer;

public class MasterOverlay extends GuiComponent {
	private ValueDriver mainDriver;

	private Text fpsText;
	private Text upsText;
	private boolean updateText;

	public MasterOverlay() {
		mainDriver = new ConstantDriver(-MasterSlider.SLIDE_SCALAR);

		fpsText = createStatus("FPS: 0", 0.02f);
		upsText = createStatus("UPS: 0", 0.06f);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateText = true;
			}
		}, 0, 100);

		super.show(true);
	}

	private Text createStatus(String content, float yPos) {
		Text text = Text.newText(content).textAlign(GuiAlign.LEFT).create();
		text.setColour(MasterSlider.TEXT_COLOUR);
		text.setBorderColour(0.15f, 0.15f, 0.15f);
		text.setBorder(new ConstantDriver(0.04f));
		super.addText(text, 0.01f, 0.01f + yPos, 0.5f);
		return text;
	}

	@Override
	public void show(boolean visible) {
		mainDriver = new SlideDriver(getRelativeX(), visible ? 0.0f : -MasterSlider.SLIDE_SCALAR, MasterMenu.SLIDE_TIME);
	}

	@Override
	protected void updateSelf() {
		float mainValue = mainDriver.update(Framework.getDelta());

		if (updateText) {
			fpsText.setText("FPS: " + Maths.roundToPlace(1.0f / Framework.getDeltaRender(), 1));
			upsText.setText("UPS: " + Maths.roundToPlace(1.0f / Framework.getDelta(), 1));
			updateText = false;
		}

		if (mainValue == -MasterSlider.SLIDE_SCALAR) {
			super.show(true);
		} else {
			super.show(true);
		}

		super.setRelativeX(mainValue);
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
	}
}
