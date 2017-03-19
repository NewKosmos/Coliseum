/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.framework.*;
import flounder.framework.updater.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.camera.*;
import org.lwjgl.glfw.*;

public class NewKosmos extends Framework {
	public static void main(String[] args) {
		NewKosmos newKosmos = new NewKosmos();
		newKosmos.run();
		System.exit(0);
	}

	public NewKosmos() {
		super("kosmos", new UpdaterDefault(GLFW::glfwGetTime), -1, new KosmosInterface(), new KosmosRenderer(), new KosmosGuis(), new KosmosCamera(), new KosmosPlayer());
		FlounderDisplay.setup(KosmosConfigs.DISPLAY_WIDTH.getInteger(),
				KosmosConfigs.DISPLAY_HEIGHT.getInteger(),
				"New Kosmos", new MyFile[]{new MyFile(MyFile.RES_FOLDER, "icon", "icon.png")},
				KosmosConfigs.DISPLAY_VSYNC.getBoolean(),
				KosmosConfigs.DISPLAY_ANTIALIAS.getBoolean(),
				0,
				KosmosConfigs.DISPLAY_FULLSCREEN.getBoolean(),
				false
		);
		setFpsLimit(KosmosConfigs.FRAMEWORK_FPS_LIMIT.getInteger());
		FlounderTextures.setup(KosmosConfigs.TEXTURES_ANISOTROPY_MAX.getFloat());
		FlounderBounding.toggle(KosmosConfigs.BOUNDINGS_RENDER.getBoolean());
		FlounderProfiler.toggle(KosmosConfigs.PROFILER_OPEN.getBoolean());
		TextBuilder.DEFAULT_TYPE = FlounderFonts.SEGOE_UI_SEMIBOLD;
	}
}
