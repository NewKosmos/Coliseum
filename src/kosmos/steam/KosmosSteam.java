package kosmos.steam;

import flounder.framework.*;

public class KosmosSteam extends Module {
	private static final KosmosSteam INSTANCE = new KosmosSteam();
	public static final String PROFILE_TAB_NAME = "Kosmos Steam";

	public KosmosSteam() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME);
	}

	@Override
	public void init() {
		/*try {
			if (!SteamAPI.init()) {
				// Steamworks initialization error, e.g. Steam client not running
			}
		} catch (SteamException e) {
			FlounderLogger.exception(e);
		}

		SteamAPI.printDebugInfo(System.out);*/
	}

	@Override
	public void update() {
	/*	if (SteamAPI.isSteamRunning()) {
			SteamAPI.runCallbacks();
		}*/
	}

	@Override
	public void profile() {
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		//	SteamAPI.shutdown();
	}
}
