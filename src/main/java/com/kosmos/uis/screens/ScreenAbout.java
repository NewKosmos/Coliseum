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

public class ScreenAbout extends ScreenObject {
	public ScreenAbout(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		// Title.
		TextObject title = new TextObject(this, new Vector2f(0.5f, 0.1f), "About Screen", 3.0f, FlounderFonts.CANDARA, 1.0f, GuiAlign.CENTRE);
		title.setInScreenCoords(true);
		title.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		title.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		title.setBorder(new ConstantDriver(0.022f));

		// Text 1.
		String t1 = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?";
		TextObject text1 = new TextObject(this, new Vector2f(0.5f, 0.5f), t1, 1.4f, FlounderFonts.CANDARA, 0.7f, GuiAlign.CENTRE);
		text1.setInScreenCoords(true);
		text1.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
		text1.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		text1.setBorder(new ConstantDriver(0.175f));
		text1.setAlphaDriver(new ConstantDriver(0.75f));

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
