/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis;

import flounder.guis.*;
import flounder.inputs.*;
import flounder.maths.vectors.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.skybox.*;
import kosmos.uis.screens.*;
import org.lwjgl.glfw.*;

public class OverlaySlider extends ScreenObject {
	private ScreenPause screenPause;
	private ScreenStart screenStart;

	private ScreenObject menuActive;
	private ScreenObject secondaryScreen;
	private ScreenObject newSecondaryScreen;

	public OverlaySlider(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.screenPause = new ScreenPause(this);
		this.screenStart = new ScreenStart(this);

		sliderStartMenu(true); // Framework.isRunningFromJar()

		this.secondaryScreen = null;
		this.newSecondaryScreen = null;
	}

	@Override
	public void updateObject() {
		if (newSecondaryScreen != null && secondaryScreen.getAlpha() == 0.0f) {
			secondaryScreen = newSecondaryScreen;
			newSecondaryScreen = null;
		}

		if (menuActive.getAlpha() == 1.0f && secondaryScreen != null && secondaryScreen.getAlpha() == 0.0f && newSecondaryScreen == null) {
			secondaryScreen = null;
		}
	}

	public void setNewSecondaryScreen(ScreenObject secondScreen) {
		if (secondaryScreen == secondScreen || newSecondaryScreen == secondScreen) {
			return;
		}

		newSecondaryScreen = secondScreen;
		newSecondaryScreen.setAlphaDriver(new SlideDriver(0.0f, 1.0f, KosmosGuis.SLIDE_TIME));

		if (secondaryScreen != null) {
			secondaryScreen.setAlphaDriver(new SlideDriver(1.0f, 0.0f, KosmosGuis.SLIDE_TIME));
		} else {
			secondaryScreen = newSecondaryScreen;
			newSecondaryScreen = null;
		}

		if (menuActive.getAlpha() == 1.0f) {
			menuActive.setAlphaDriver(new SlideDriver(1.0f, 0.0f, KosmosGuis.SLIDE_TIME));
		}
	}

	public void closeSecondaryScreen() {
		if (newSecondaryScreen != null) {
			return;
		}

		if (secondaryScreen != null && secondaryScreen.getAlpha() == 1.0f) {
			secondaryScreen.setAlphaDriver(new SlideDriver(1.0f, 0.0f, KosmosGuis.SLIDE_TIME));
			menuActive.setAlphaDriver(new SlideDriver(0.0f, 1.0f, KosmosGuis.SLIDE_TIME));
		}
	}

	public void sliderStartMenu(boolean useStartMenu) {
		if (useStartMenu && menuActive != screenStart) {
			this.menuActive = screenStart;
			this.screenPause.setAlphaDriver(new ConstantDriver(0.0f));
			this.menuActive.setAlphaDriver(new ConstantDriver(1.0f));
		} else if (menuActive != screenPause) {
			this.menuActive = screenPause;
			this.screenStart.setAlphaDriver(new ConstantDriver(0.0f));
			this.menuActive.setAlphaDriver(new ConstantDriver(1.0f));
		}
	}

	public boolean inStartMenu() {
		return menuActive == screenStart;
	}

	public float getBlurFactor() {
		return getAlpha();
	}

	@Override
	public void deleteObject() {
	}
}
