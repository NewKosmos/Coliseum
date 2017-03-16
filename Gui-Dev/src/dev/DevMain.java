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
import flounder.fonts.*;
import flounder.framework.*;
import flounder.framework.updater.*;
import flounder.resources.*;
import org.lwjgl.glfw.*;

public class DevMain extends Framework {
	public static void main(String[] args) {
		DevMain devMain = new DevMain();
		devMain.run();
		System.exit(0);
	}

	public DevMain() {
		super("devguis", new UpdaterDefault(GLFW::glfwGetTime), -1, new DevInterface(), new DevRenderer(), new DevGuis());
		FlounderDisplay.setup(1080, 720, "Dev Guis", new MyFile[]{new MyFile(MyFile.RES_FOLDER, "icon", "icon.png")}, false, false, 0, false, false);
		TextBuilder.DEFAULT_TYPE = FlounderFonts.SEGOE_UI;
	}
}
