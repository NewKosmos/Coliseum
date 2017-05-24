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
import flounder.maths.vectors.*;
import flounder.visual.*;
import kosmos.chunks.*;
import kosmos.world.*;

import java.util.Timer;
import java.util.*;

public class OverlayDebug extends ScreenObject {
	private TextObject fpsText;
	private TextObject upsText;
	private TextObject positionText;
	private TextObject timeText;
	private TextObject seedText;
	private TextObject biomeText;
	private boolean updateText;

	public OverlayDebug(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.fpsText = createStatus("FPS: 0", 0.01f);
		this.upsText = createStatus("UPS: 0", 0.04f);
		this.positionText = createStatus("POSITION: [0, 0, 0]", 0.07f);
		this.timeText = createStatus("TIME: 0", 0.10f);
		this.seedText = createStatus("SEED: 0", 0.13f);
		this.biomeText = createStatus("BIOME: NULL", 0.16f);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateText = true;
			}
		}, 0, 100);
	}

	private TextObject createStatus(String content, float yPos) {
		TextObject text = new TextObject(this, new Vector2f(0.01f, 0.01f + yPos), content, 0.75f, FlounderFonts.CANDARA, 0.5f, GuiAlign.LEFT);
		text.setInScreenCoords(false);
		text.setColour(new Colour(1.0f, 1.0f, 1.0f));
		text.setBorderColour(new Colour(0.15f, 0.15f, 0.15f));
		text.setBorder(new ConstantDriver(0.04f));
		return text;
	}

	@Override
	public void updateObject() {
		if (!isVisible()) {
			return;
		}

		if (updateText) {
			fpsText.setText("FPS: " + Maths.roundToPlace(1.0f / Framework.getDeltaRender(), 1));
			upsText.setText("UPS: " + Maths.roundToPlace(1.0f / Framework.getDelta(), 1));
			positionText.setText("POSITION: [" + (FlounderCamera.get().getPlayer() == null ? "NULL" : Maths.roundToPlace(FlounderCamera.get().getPlayer().getPosition().x, 1) + ", " + Maths.roundToPlace(FlounderCamera.get().getPlayer().getPosition().y, 1) + ", " + Maths.roundToPlace(FlounderCamera.get().getPlayer().getPosition().z, 1) + "]"));
			timeText.setText("TIME: " + Maths.roundToPlace(KosmosWorld.get().getDayFactor(), 3));
			seedText.setText("SEED: " + KosmosWorld.get().getNoise().getSeed());
			biomeText.setText("BIOME: " + (KosmosChunks.get().getCurrent() == null ? "NULL" : KosmosChunks.get().getCurrent().getBiome().name()));
			updateText = false;
		}
	}

	@Override
	public void deleteObject() {
	}
}