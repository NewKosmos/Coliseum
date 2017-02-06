/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package dev;

import flounder.devices.*;
import flounder.events.*;
import flounder.framework.*;
import flounder.inputs.*;
import flounder.standard.*;

import static org.lwjgl.glfw.GLFW.*;

public class DevInterface extends IStandard {
	private KeyButton closeWindow;

	public DevInterface() {
		super(FlounderDisplay.class, FlounderKeyboard.class);
	}

	@Override
	public void init() {
		this.closeWindow = new KeyButton(GLFW_KEY_DELETE);

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return closeWindow.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderFramework.requestClose();
			}
		});
	}

	@Override
	public void update() {

	}

	@Override
	public void profile() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
