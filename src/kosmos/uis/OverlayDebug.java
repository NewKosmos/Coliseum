/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis;

import flounder.camera.*;
import flounder.fonts.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.visual.*;
import kosmos.world.*;

import java.util.*;
import java.util.Timer;

public class OverlayDebug extends GuiComponent {
	private Text fpsText;
	private Text upsText;
	private Text positionText;
	private Text timeText;
	private Text seedText;
	private boolean updateText;

	public OverlayDebug() {
		fpsText = createStatus("FPS: 0", 0.01f);
		upsText = createStatus("UPS: 0", 0.04f);
		positionText = createStatus("POSITION: [0, 0, 0]", 0.07f);
		timeText = createStatus("TIME: 0", 0.10f);
		seedText = createStatus("SEED: 0", 0.13f);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateText = true;
			}
		}, 0, 100);

		super.show(false);
	}

	private Text createStatus(String content, float yPos) {
		Text text = Text.newText(content).setFontSize(0.75f).textAlign(GuiAlign.LEFT).create();
		text.setColour(1.0f, 1.0f, 1.0f);
		text.setBorderColour(0.15f, 0.15f, 0.15f);
		text.setBorder(new ConstantDriver(0.04f));
		super.addText(text, 0.01f, 0.01f + yPos, 0.5f);
		return text;
	}

	@Override
	protected void updateSelf() {
		if (updateText) {
			fpsText.setText("FPS: " + Maths.roundToPlace(1.0f / Framework.getDeltaRender(), 1));
			upsText.setText("UPS: " + Maths.roundToPlace(1.0f / Framework.getDelta(), 1));
			positionText.setText("POSITION: [" + (FlounderCamera.getPlayer() == null ? "NULL" : Maths.roundToPlace(FlounderCamera.getPlayer().getPosition().x, 1) + ", " + Maths.roundToPlace(FlounderCamera.getPlayer().getPosition().y, 1) + ", " + Maths.roundToPlace(FlounderCamera.getPlayer().getPosition().z, 1) + "]"));
			timeText.setText("TIME: " + Maths.roundToPlace(KosmosWorld.getDayFactor(), 3));
			seedText.setText("SEED: " + KosmosWorld.getNoise().getSeed());
			updateText = false;
		}
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
	}
}
