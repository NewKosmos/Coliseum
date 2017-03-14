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

public class NewKosmos extends Framework {
	public static void main(String[] args) {
		NewKosmos newKosmos = new NewKosmos();
		newKosmos.run();
		System.exit(0);
	}

	public NewKosmos() {
		super("kosmos", new UpdaterDefault(), -1, new KosmosInterface(), new KosmosRenderer(), new CameraFocus(), new PlayerBasic(), new KosmosGuis());


		FlounderDisplay.setup(KosmosConfigs.configMain.getIntWithDefault("width", 1080, FlounderDisplay::getWindowWidth),
				KosmosConfigs.configMain.getIntWithDefault("height", 720, FlounderDisplay::getWindowHeight),
				"New Kosmos", new MyFile[]{new MyFile(MyFile.RES_FOLDER, "icon", "icon.png")},
				KosmosConfigs.configMain.getBooleanWithDefault("vsync", false, FlounderDisplay::isVSync),
				KosmosConfigs.configMain.getBooleanWithDefault("antialias", true, FlounderDisplay::isAntialiasing),
				KosmosConfigs.configMain.getIntWithDefault("samples", 0, FlounderDisplay::getSamples),
				KosmosConfigs.configMain.getBooleanWithDefault("fullscreen", false, FlounderDisplay::isFullscreen),
				false
		);
		setFpsLimit(KosmosConfigs.configMain.getIntWithDefault("fps_limit", -1, Framework::getFpsLimit));
		FlounderTextures.setup(KosmosConfigs.configMain.getFloatWithDefault("anisotropy_level", 4, FlounderTextures::getAnisotropyLevel));
		FlounderBounding.toggle(KosmosConfigs.configMain.getBooleanWithDefault("boundings_render", false, FlounderBounding::renders));
		FlounderProfiler.toggle(KosmosConfigs.configMain.getBooleanWithDefault("profiler_open", false, FlounderProfiler::isOpen));
		TextBuilder.DEFAULT_TYPE = FlounderFonts.FFF_FORWARD;
	}
}
