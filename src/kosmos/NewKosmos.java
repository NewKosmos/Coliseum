/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos;

import flounder.framework.*;
import flounder.framework.updater.*;
import flounder.lwjgl3.*;
import flounder.resources.*;
import kosmos.camera.*;

public class NewKosmos extends Framework {
	public static final String VERSION = "6.06";

	public static void main(String[] args) {
		new NewKosmos().run();
		System.exit(0);
	}

	public NewKosmos() {
		super(
				"kosmos", new UpdaterDefault(null), -1,
				new Extension[]{new KosmosInterface(), new KosmosRenderer(), new KosmosCamera(), new KosmosPlayer(), new KosmosGuis()}
		);
		Framework.get().addOverrides(new PlatformLwjgl(
				KosmosConfigs.DISPLAY_WIDTH.getInteger(),
				KosmosConfigs.DISPLAY_HEIGHT.getInteger(),
				"New Kosmos", new MyFile[]{new MyFile(MyFile.RES_FOLDER, "icon", "icon.png")},
				KosmosConfigs.DISPLAY_VSYNC.getBoolean(),
				KosmosConfigs.DISPLAY_ANTIALIAS.getBoolean(),
				0,
				KosmosConfigs.DISPLAY_FULLSCREEN.getBoolean(),
				false,
				false,
				KosmosConfigs.TEXTURES_ANISOTROPY_MAX.getFloat()
		));
		Framework.get().setFpsLimit(KosmosConfigs.FRAMEWORK_FPS_LIMIT.getInteger());
	}
}
