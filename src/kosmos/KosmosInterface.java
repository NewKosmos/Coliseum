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
import flounder.events.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.networking.*;
import flounder.particles.*;
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.shadows.*;
import flounder.skybox.*;
import flounder.sounds.*;
import flounder.standards.*;
import flounder.steam.*;
import kosmos.chunks.*;
import kosmos.network.packets.*;
import kosmos.post.*;
import kosmos.water.*;
import kosmos.world.*;

import static org.lwjgl.glfw.GLFW.*;

public class KosmosInterface extends Standard {
	private Playlist gamePlaylist;

	public KosmosInterface() {
		super(FlounderDisplay.class, FlounderKeyboard.class, FlounderNetwork.class, FlounderSteam.class, FlounderBounding.class, KosmosPost.class, FlounderShadows.class, FlounderParticles.class, KosmosWater.class, FlounderSkybox.class, KosmosWorld.class, KosmosChunks.class);
	}

	@Override
	public void init() {
		FlounderSound.getMusicPlayer().setVolume(KosmosConfigs.MUSIC_VOLUME.setReference(() -> FlounderSound.getMusicPlayer().getVolume()).getFloat());
		FlounderSound.getSourcePool().setSystemVolume(KosmosConfigs.SOUND_VOLUME.setReference(() -> FlounderSound.getSourcePool().getSystemVolume()).getFloat());

		gamePlaylist = new Playlist();
		gamePlaylist.addMusic(Sound.loadSoundInBackground(new MyFile(MyFile.RES_FOLDER, "music", "09-hitori-bocchi-1b.wav"), 0.80f, 1.0f));
		FlounderSound.getMusicPlayer().playMusicPlaylist(gamePlaylist, true, 4.0f, 10.0f);

		if (KosmosConfigs.MUSIC_ENABLED.setReference(() -> !FlounderSound.getMusicPlayer().isPaused()).getBoolean()) {
			FlounderSound.getMusicPlayer().unpauseTrack();
		}

		FlounderEvents.addEvent(new IEvent() {
			KeyButton screenshot = new KeyButton(GLFW_KEY_F2);

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
			KeyButton fullscreen = new KeyButton(GLFW_KEY_F11);

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
			KeyButton polygons = new KeyButton(GLFW_KEY_P);

			@Override
			public boolean eventTriggered() {
				return polygons.wasDown() && !FlounderGuis.getGuiMaster().isGamePaused();
			}

			@Override
			public void onEvent() {
				OpenGlUtils.goWireframe(!OpenGlUtils.isInWireframe());
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			KeyButton aabbs = new KeyButton(GLFW_KEY_O);

			@Override
			public boolean eventTriggered() {
				return aabbs.wasDown() && !FlounderGuis.getGuiMaster().isGamePaused();
			}

			@Override
			public void onEvent() {
				FlounderBounding.toggle(!FlounderBounding.renders());
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			KeyButton closeWindow = new KeyButton(GLFW_KEY_DELETE);

			@Override
			public boolean eventTriggered() {
				return closeWindow.wasDown() && !FlounderGuis.getGuiMaster().isGamePaused();
			}

			@Override
			public void onEvent() {
				if (FlounderNetwork.getSocketClient() != null) {
					new PacketDisconnect(FlounderNetwork.getUsername()).writeData(FlounderNetwork.getSocketClient());
					FlounderNetwork.closeClient();
				}

				Framework.requestClose();
			}
		});
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {

	}

	@Override
	public void dispose() {
		if (FlounderNetwork.getSocketClient() != null) {
			new PacketDisconnect(FlounderNetwork.getUsername()).writeData(FlounderNetwork.getSocketClient());
			FlounderNetwork.closeClient();
		}

		KosmosConfigs.saveAllConfigs();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
