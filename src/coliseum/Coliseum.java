package coliseum;

import coliseum.camera.*;
import flounder.devices.*;
import flounder.fonts.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.parsing.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.textures.*;

public class Coliseum extends FlounderFramework {
	public static void main(String[] args) {
		Coliseum coliseum = new Coliseum();
		coliseum.run();
		System.exit(0);
	}

	public static Config configMain;

	public Coliseum() {
		super("Coliseum", -1, new ColiseumInterface(), new ColiseumRenderer(), new CameraIsographic(), new PlayerBasic(), new ColiseumGuis()); // , new ColiseumMusic()

		configMain = new Config(new MyFile(FlounderFramework.getRoamingFolder(), "configs", "settings.conf"));

		FlounderDisplay.setup(configMain.getIntWithDefault("width", 1080, FlounderDisplay::getWindowWidth),
				configMain.getIntWithDefault("height", 720, FlounderDisplay::getWindowHeight),
				"Coliseum", new MyFile[]{new MyFile(MyFile.RES_FOLDER, "icon", "icon.png")},
				configMain.getBooleanWithDefault("vsync", false, FlounderDisplay::isVSync),
				configMain.getBooleanWithDefault("antialias", true, FlounderDisplay::isAntialiasing),
				configMain.getIntWithDefault("samples", 0, FlounderDisplay::getSamples),
				configMain.getBooleanWithDefault("fullscreen", false, FlounderDisplay::isFullscreen),
				false
		);
		FlounderBounding.toggle(Coliseum.configMain.getBooleanWithDefault("boundings_render", false, FlounderBounding::renders));
		FlounderProfiler.toggle(Coliseum.configMain.getBooleanWithDefault("profiler_open", false, FlounderProfiler::isOpen));
		FlounderTextures.setAnisotropyLevel(Coliseum.configMain.getFloatWithDefault("anisotropy_level", 4, FlounderTextures::getAnisotropyLevel));
		TextBuilder.DEFAULT_TYPE = FlounderFonts.FFF_FORWARD;
	}

	protected static void closeConfigs() {
		configMain.dispose();
	}
}
