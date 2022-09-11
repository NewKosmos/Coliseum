/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos;

import com.flounder.framework.*;
import com.flounder.framework.updater.*;
import com.flounder.resources.*;
import com.kosmos.camera.*;

public class NewKosmos extends Framework {
	public static final String VERSION = "6.27";

	public static void main(String[] args) {
		new NewKosmos().run();
		System.exit(0);
	}

	public NewKosmos() {
		super(
				"kosmos", new UpdaterDefault(), -1,
				new Extension[]{new KosmosInterface(), new KosmosRenderer(), new KosmosCamera(), new KosmosPlayer(), new KosmosGuis()}
		);
		//Framework.get().addOverrides(new PlatformLwjgl(
		//		KosmosConfigs.DISPLAY_WIDTH.getInteger(),
		//		KosmosConfigs.DISPLAY_HEIGHT.getInteger(),
		//		"New Kosmos", new MyFile[]{new MyFile(MyFile.RES_FOLDER, "icon", "icon.png")},
		//		KosmosConfigs.DISPLAY_VSYNC.getBoolean(),
		//		KosmosConfigs.DISPLAY_ANTIALIAS.getBoolean(),
		//		0,
		//		KosmosConfigs.DISPLAY_FULLSCREEN.getBoolean(),
		//		false,
		//		false,
		//		KosmosConfigs.TEXTURES_ANISOTROPY_MAX.getFloat()
		//));
		Framework.get().setFpsLimit(KosmosConfigs.FRAMEWORK_FPS_LIMIT.getInteger());
	}
}
