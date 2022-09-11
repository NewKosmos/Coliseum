/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.uis;

import com.flounder.camera.*;
import com.flounder.events.*;
import com.flounder.fonts.*;
import com.flounder.framework.*;
import com.flounder.guis.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.visual.*;
import com.kosmos.world.*;
import com.kosmos.world.biomes.*;
import com.kosmos.world.chunks.*;

public class OverlayDebug extends ScreenObject {
	private TextObject fpsText;
	private TextObject upsText;
	private TextObject positionText;
	private TextObject timeText;
	private TextObject seedText;
	private TextObject moistureText;
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
		this.moistureText = createStatus("MOISTURE: 1", 0.16f);
		this.biomeText = createStatus("BIOME: NULL", 0.19f);

		FlounderEvents.get().addEvent(new EventTime(0.333f, true) {
			@Override
			public void onEvent() {
				updateText = true;
			}
		});
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
			fpsText.setText("FPS: " + Maths.roundToPlace(1.0f / Framework.get().getDeltaRender(), 1));
			upsText.setText("UPS: " + Maths.roundToPlace(1.0f / Framework.get().getDelta(), 1));
			positionText.setText("POSITION: [" + (FlounderCamera.get().getPlayer() == null ? "NULL" : Maths.roundToPlace(FlounderCamera.get().getPlayer().getPosition().x, 1) + ", " + Maths.roundToPlace(FlounderCamera.get().getPlayer().getPosition().y, 1) + ", " + Maths.roundToPlace(FlounderCamera.get().getPlayer().getPosition().z, 1) + "]"));
			timeText.setText("TIME: " + Maths.roundToPlace(KosmosWorld.get().getDayFactor(), 3));
			seedText.setText("SEED: " + (KosmosWorld.get().getWorld() == null ? "NULL" : KosmosWorld.get().getWorld().getSeed()));

			if (FlounderCamera.get().getPlayer() != null && KosmosWorld.get().getWorld() != null) {
				IBiome.Biomes biome = KosmosChunks.getBiomeMap(FlounderCamera.get().getPlayer().getPosition().x, FlounderCamera.get().getPlayer().getPosition().z);

				moistureText.setText("MOISTURE: " + Maths.roundToPlace(KosmosChunks.getMoistureMap(FlounderCamera.get().getPlayer().getPosition().x, FlounderCamera.get().getPlayer().getPosition().z), 2));
				biomeText.setText("BIOME: " + biome.name());
			} else {
				moistureText.setText("MOISTURE: 1.0");
				biomeText.setText("BIOME: NULL");
			}

			updateText = false;
		}
	}

	@Override
	public void deleteObject() {
	}
}