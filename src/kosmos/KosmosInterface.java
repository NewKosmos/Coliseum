/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos;

import com.codedisaster.steamworks.*;
import flounder.devices.*;
import flounder.events.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.logger.*;
import flounder.resources.*;
import flounder.sounds.*;
import flounder.standard.*;
import kosmos.world.*;

import static org.lwjgl.glfw.GLFW.*;

public class KosmosInterface extends IStandard {
	private Playlist gamePlaylist;

	private KeyButton screenshot;
	private KeyButton fullscreen;
	private KeyButton polygons;
	private KeyButton closeWindow;

	public KosmosInterface() {
		super(FlounderDisplay.class, FlounderKeyboard.class, KosmosWorld.class);
	}

	@Override
	public void init() {
		gamePlaylist = new Playlist();
		gamePlaylist.addMusic(Sound.loadSoundInBackground(new MyFile(MyFile.RES_FOLDER, "music", "09-hitori-bocchi-1b.wav"), 0.80f, 1.0f));
		FlounderSound.getMusicPlayer().playMusicPlaylist(gamePlaylist, true, 4.0f, 10.0f);
		//	FlounderSound.getMusicPlayer().unpauseTrack();

		this.screenshot = new KeyButton(GLFW_KEY_F2);
		this.fullscreen = new KeyButton(GLFW_KEY_F11);
		this.polygons = new KeyButton(GLFW_KEY_P);
		this.closeWindow = new KeyButton(GLFW_KEY_DELETE);

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return screenshot.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderDisplay.screenshot();
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return fullscreen.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderDisplay.setFullscreen(!FlounderDisplay.isFullscreen());
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return polygons.wasDown();
			}

			@Override
			public void onEvent() {
				OpenGlUtils.goWireframe(!OpenGlUtils.isInWireframe());
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return closeWindow.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderFramework.requestClose();
			}
		});

		try {
			if (!SteamAPI.init()) {
				// Steamworks initialization error, e.g. Steam client not running
			}
		} catch (SteamException e) {
			FlounderLogger.exception(e);
		}

		SteamAPI.printDebugInfo(System.out);
	}

	@Override
	public void update() {
		if (SteamAPI.isSteamRunning()) {
			SteamAPI.runCallbacks();
		}
	}

	@Override
	public void profile() {

	}

	@Override
	public void dispose() {
		SteamAPI.shutdown();
		NewKosmos.closeConfigs();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
