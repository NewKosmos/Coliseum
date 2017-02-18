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
import flounder.parsing.*;
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

	public static Config configMain;

	public NewKosmos() {
		super("kosmos", -1, new KosmosInterface(), new KosmosRenderer(), new CameraFocus(), new PlayerBasic(), new KosmosGuis());

		configMain = new Config(new MyFile(Framework.getRoamingFolder(), "configs", "settings.conf"));

		FlounderDisplay.setup(configMain.getIntWithDefault("width", 1080, FlounderDisplay::getWindowWidth),
				configMain.getIntWithDefault("height", 720, FlounderDisplay::getWindowHeight),
				"New Kosmos", new MyFile[]{new MyFile(MyFile.RES_FOLDER, "icon", "icon.png")},
				configMain.getBooleanWithDefault("vsync", false, FlounderDisplay::isVSync),
				configMain.getBooleanWithDefault("antialias", true, FlounderDisplay::isAntialiasing),
				configMain.getIntWithDefault("samples", 0, FlounderDisplay::getSamples),
				configMain.getBooleanWithDefault("fullscreen", false, FlounderDisplay::isFullscreen),
				false
		);
		FlounderTextures.setup(NewKosmos.configMain.getFloatWithDefault("anisotropy_level", 4, FlounderTextures::getAnisotropyLevel));
		FlounderBounding.toggle(NewKosmos.configMain.getBooleanWithDefault("boundings_render", false, FlounderBounding::renders));
		FlounderProfiler.toggle(NewKosmos.configMain.getBooleanWithDefault("profiler_open", false, FlounderProfiler::isOpen));
		TextBuilder.DEFAULT_TYPE = FlounderFonts.FFF_FORWARD;
	}

	protected static void closeConfigs() {
		configMain.dispose();
	}
}
