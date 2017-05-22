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
import flounder.guis.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;

public class OverlayMap extends ScreenObject {
	private GuiObject textureView;
	private TextObject positionText;

	public OverlayMap(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.textureView = new GuiObject(this, new Vector2f(0.5f, 0.5f), new Vector2f(1.05f, 0.7f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "maptest.png")).create(), 1);
		this.textureView.setInScreenCoords(true);

		this.positionText = new TextObject(this, new Vector2f(0.31f, 0.38f), "POSITION: [0, 0, 0]", 0.75f, FlounderFonts.CANDARA, 0.5f, GuiAlign.LEFT);
		positionText.setInScreenCoords(true);
		positionText.setColour(new Colour(1.0f, 1.0f, 1.0f));
		positionText.setBorderColour(new Colour(0.15f, 0.15f, 0.15f));
		positionText.setBorder(new ConstantDriver(0.04f));
	}

	@Override
	public void updateObject() {
		if (!isVisible()) {
			return;
		}

		positionText.setText("POSITION: [" + (FlounderCamera.get().getPlayer() == null ? "NULL" : Maths.roundToPlace(FlounderCamera.get().getPlayer().getPosition().x, 1) + ", " + Maths.roundToPlace(FlounderCamera.get().getPlayer().getPosition().y, 1) + ", " + Maths.roundToPlace(FlounderCamera.get().getPlayer().getPosition().z, 1) + "]"));
	}

	@Override
	public void deleteObject() {
	}
}