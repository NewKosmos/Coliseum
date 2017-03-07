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
import flounder.devices.*;
import flounder.fonts.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.world.*;

import java.util.*;
import java.util.Timer;

public class MasterOverlay extends GuiComponent {
	private Text fpsText;
	private Text upsText;
	private Text positionText;
	private Text timeText;
	private Text seedText;
	private boolean updateText;

	private GuiTexture crossHair;

	public MasterOverlay() {
		fpsText = createStatus("FPS: 0", 0.02f);
		upsText = createStatus("UPS: 0", 0.06f);
		positionText = createStatus("POSITION: [0, 0, 0]", 0.10f);
		timeText = createStatus("TIME: 0", 0.14f);
		seedText = createStatus("SEED: 0", 0.18f);
		createStatus("C TO TOGGLE EFFECTS", 0.23f);
		createStatus("ESC TO HIDE HUD", 0.27f);


		crossHair = new GuiTexture(TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "guis", "crosshair.png")).setNumberOfRows(4).create());
		crossHair.setSelectedRow(KosmosConfigs.configMain.getIntWithDefault("crosshair", 1, () -> crossHair.getSelectedRow()));

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
			timeText.setText("TIME: " + KosmosWorld.getSkyCycle().getDayFactor());
			seedText.setText("SEED: " + KosmosWorld.getNoise().getSeed());
			updateText = false;
		}

		float size = (66.6f / (FlounderDisplay.getWidth() + FlounderDisplay.getHeight()));
		crossHair.setPosition((FlounderDisplay.getAspectRatio() / 2.0f) + super.getPosition().x, 0.5f, size, size);
		crossHair.update();
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
		//	if (isShown()) {
		//		guiTextures.add(crossHair);
		//	}
	}
}
