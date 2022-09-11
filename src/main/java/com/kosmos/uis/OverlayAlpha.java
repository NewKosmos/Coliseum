/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.uis;

import com.flounder.devices.*;
import com.flounder.fonts.*;
import com.flounder.guis.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.resources.*;
import com.flounder.textures.*;
import com.flounder.visual.*;
import com.kosmos.*;

public class OverlayAlpha extends ScreenObject {
	private static float SIZE = 0.045f;

	private GuiObject cornerAlpha;
	private TextObject cornerVersion;

	public OverlayAlpha(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.cornerAlpha = new GuiObject(this, new Vector2f(FlounderDisplay.get().getAspectRatio() - SIZE, SIZE), new Vector2f(0.30f, 0.06f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "cornerAlpha.png")).create(), 1);
		this.cornerAlpha.setInScreenCoords(false);
		this.cornerAlpha.setColourOffset(new Colour());
		this.cornerAlpha.setRotationDriver(new ConstantDriver(45.0f));

		this.cornerVersion = new TextObject(this, new Vector2f(FlounderDisplay.get().getAspectRatio() - SIZE, SIZE), "New Kosmos \n Alpha " + NewKosmos.VERSION, 0.70f, FlounderFonts.CANDARA, 0.2f, GuiAlign.CENTRE);
		this.cornerVersion.setInScreenCoords(false);
		this.cornerVersion.setColour(new Colour(1.0f, 1.0f, 1.0f));
		this.cornerVersion.setRotationDriver(new ConstantDriver(45.0f));
	}

	@Override
	public void updateObject() {
		this.cornerAlpha.getPosition().x = FlounderDisplay.get().getAspectRatio() - SIZE;
		this.cornerVersion.getPosition().x = FlounderDisplay.get().getAspectRatio() - SIZE;

		this.cornerAlpha.setColourOffset(FlounderGuis.get().getGuiMaster().getPrimaryColour());
	}

	@Override
	public void deleteObject() {
	}
}
