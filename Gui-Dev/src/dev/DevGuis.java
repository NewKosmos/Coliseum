/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package dev;

import dev.uis.*;
import flounder.devices.*;
import flounder.guis.*;
import flounder.inputs.*;
import flounder.logger.*;
import flounder.profiling.*;

import static org.lwjgl.glfw.GLFW.*;

public class DevGuis extends IGuiMaster {
	private MasterMenu masterMenu;
	private MasterOverlay masterOverlay;

	private KeyButton openMenuKey;
	private boolean menuIsOpen;
	private boolean forceOpenGUIs;

	public DevGuis() {
		super(FlounderLogger.class, FlounderProfiler.class);
	}

	@Override
	public void init() {
		this.masterMenu = new MasterMenu();
		this.masterOverlay = new MasterOverlay();

		this.openMenuKey = new KeyButton(GLFW_KEY_ESCAPE);
		this.menuIsOpen = true;
		this.forceOpenGUIs = true;

		FlounderGuis.addComponent(masterMenu, 0.0f, 0.0f, 1.0f, 1.0f);
		FlounderGuis.addComponent(masterOverlay, 0.0f, 0.0f, 1.0f, 1.0f);

		//	FlounderGuis.getSelector().initJoysticks(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_GUI_LEFT, OptionsControls.JOYSTICK_GUI_RIGHT, OptionsControls.JOYSTICK_AXIS_X, OptionsControls.JOYSTICK_AXIS_Y);
		FlounderMouse.setCursorHidden(false);
	}

	@Override
	public void update() {
		if (forceOpenGUIs) {
			masterMenu.display(true);
			masterOverlay.show(false);
			forceOpenGUIs = false;
		}

		menuIsOpen = masterMenu.isDisplayed();

		if (openMenuKey.wasDown() && (!menuIsOpen || !masterMenu.getMasterSlider().onStartScreen())) {
			masterMenu.display(!masterMenu.isDisplayed());
			masterOverlay.show(!masterMenu.isDisplayed());
		}
	}

	@Override
	public void profile() {
	}

	@Override
	public boolean isGamePaused() {
		return menuIsOpen;
	}

	@Override
	public void openMenu() {
		forceOpenGUIs = true;
	}

	@Override
	public float getBlurFactor() {
		return masterMenu.getBlurFactor();
	}

	public MasterOverlay getMasterOverlay() {
		return masterOverlay;
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
