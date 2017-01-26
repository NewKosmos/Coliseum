package coliseum;

import coliseum.world.*;
import flounder.devices.*;
import flounder.fonts.*;
import flounder.framework.*;
import flounder.maths.vectors.*;
import flounder.parsing.*;
import flounder.resources.*;

public class Coliseum extends FlounderFramework {
	public static void main(String[] args) {
		Coliseum coliseum = new Coliseum();
		coliseum.run();
		System.exit(0);
	}

	public static Config configMain;

	public Coliseum() {
		super("Coliseum", -1, new ColiseumInterface(), new ColiseumRenderer(), new ColiseumGuis()); // , new ColiseumMusic()

		configMain = new Config(new MyFile(FlounderFramework.getRoamingFolder(), "configs", "settings.conf"));

		FlounderDisplay.setup(configMain.getIntWithDefault("width", 1080, FlounderDisplay::getWindowWidth),
				configMain.getIntWithDefault("height", 720, FlounderDisplay::getWindowHeight),
				"Coliseum", new MyFile[]{new MyFile(MyFile.RES_FOLDER, "icon.png")},
				configMain.getBooleanWithDefault("vsync", true, FlounderDisplay::isVSync),
				configMain.getBooleanWithDefault("antialias", false, FlounderDisplay::isAntialiasing),
				configMain.getIntWithDefault("samples", 0, FlounderDisplay::getSamples),
				configMain.getBooleanWithDefault("fullscreen", false, FlounderDisplay::isFullscreen),
				false
		);
		TextBuilder.DEFAULT_TYPE = FlounderFonts.FFF_FORWARD;

	//	new Chunk(new Vector2f());
	//	System.exit(0);
	}

	protected static void closeConfigs() {
		configMain.dispose();
	}
}
