package kosmos;

import flounder.framework.*;
import flounder.parsing.*;
import flounder.resources.*;

public class KosmosConfigs {
	public static final Config configMain = new Config(new MyFile(Framework.getRoamingFolder(), "configs", "settings.conf"));
	public static final Config configSave = new Config(new MyFile(Framework.getRoamingFolder(), "saves", "save0.conf"));

	protected static void closeConfigs() {
		configMain.dispose();
		configSave.dispose();
	}
}
