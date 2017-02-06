/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package dev;

import flounder.fonts.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.renderer.*;

public class DevRenderer extends IRendererMaster {
	private GuisRenderer guisRenderer;
	private FontRenderer fontRenderer;

	public DevRenderer() {
		super(FlounderGuis.class, FlounderFonts.class);
	}

	@Override
	public void init() {
		guisRenderer = new GuisRenderer();
		fontRenderer = new FontRenderer();
	}

	@Override
	public void render() {
		OpenGlUtils.prepareNewRenderParse(0.0f, 0.0f, 0.0f);
		guisRenderer.render(null, null);
		fontRenderer.render(null, null);
	}

	@Override
	public void profile() {
	}

	@Override
	public void dispose() {
		guisRenderer.dispose();
		fontRenderer.dispose();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
