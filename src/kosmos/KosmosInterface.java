/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos;

import flounder.devices.*;
import flounder.events.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.networking.*;
import flounder.particles.*;
import flounder.shadows.*;
import flounder.skybox.*;
import flounder.standards.*;
import flounder.steam.*;
import kosmos.chunks.*;
import kosmos.network.packets.*;
import kosmos.post.*;
import kosmos.water.*;
import kosmos.world.*;

import static flounder.platform.Constants.*;

public class KosmosInterface extends Standard {
	public KosmosInterface() {
		super(FlounderEvents.class, FlounderNetwork.class, FlounderSteam.class, FlounderShadows.class, FlounderParticles.class, FlounderSkybox.class, KosmosWater.class, KosmosPost.class, KosmosWorld.class, KosmosChunks.class);
	}

	@Override
	public void init() {
		FlounderSound.get().getMusicPlayer().setVolume(KosmosConfigs.MUSIC_VOLUME.setReference(() -> FlounderSound.get().getMusicPlayer().getVolume()).getFloat());
		FlounderSound.get().getSourcePool().setSystemVolume(KosmosConfigs.SOUND_VOLUME.setReference(() -> FlounderSound.get().getSourcePool().getSystemVolume()).getFloat());

		if (KosmosConfigs.MUSIC_ENABLED.setReference(() -> !FlounderSound.get().getMusicPlayer().isPaused()).getBoolean()) {
			FlounderSound.get().getMusicPlayer().unpauseTrack();
		}

		FlounderEvents.get().addEvent(new IEvent() {
			KeyButton seedRandom = new KeyButton(GLFW_KEY_F6);

			@Override
			public boolean eventTriggered() {
				return seedRandom.wasDown() && FlounderNetwork.get().getSocketClient() == null;
			}

			@Override
			public void onEvent() {
				KosmosChunks.get().getNoise().setSeed((int) Maths.randomInRange(1.0, 1000000.0));
			}
		});

		FlounderEvents.get().addEvent(new IEvent() {
			KeyButton screenshot = new KeyButton(GLFW_KEY_F2);

			@Override
			public boolean eventTriggered() {
				return screenshot.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderDisplay.get().screenshot();
			}
		});

		FlounderEvents.get().addEvent(new IEvent() {
			KeyButton fullscreen = new KeyButton(GLFW_KEY_F11);

			@Override
			public boolean eventTriggered() {
				return fullscreen.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderDisplay.get().setFullscreen(!FlounderDisplay.get().isFullscreen());
			}
		});

		FlounderEvents.get().addEvent(new IEvent() {
			KeyButton wireframe = new KeyButton(GLFW_KEY_P);

			@Override
			public boolean eventTriggered() {
				return wireframe.wasDown() && !FlounderGuis.get().getGuiMaster().isGamePaused();
			}

			@Override
			public void onEvent() {
				FlounderOpenGL.get().goWireframe(!FlounderOpenGL.get().isInWireframe());
			}
		});

		FlounderEvents.get().addEvent(new IEvent() {
			KeyButton closeWindow = new KeyButton(GLFW_KEY_DELETE);

			@Override
			public boolean eventTriggered() {
				return closeWindow.wasDown() && !FlounderGuis.get().getGuiMaster().isGamePaused();
			}

			@Override
			public void onEvent() {
				if (FlounderNetwork.get().getSocketClient() != null) {
					new PacketDisconnect(FlounderNetwork.get().getUsername()).writeData(FlounderNetwork.get().getSocketClient());
					FlounderNetwork.get().closeClient();
				}

				KosmosConfigs.saveAllConfigs();
				Framework.get().requestClose(false);
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
		if (FlounderNetwork.get().getSocketClient() != null) {
			new PacketDisconnect(FlounderNetwork.get().getUsername()).writeData(FlounderNetwork.get().getSocketClient());
			FlounderNetwork.get().closeClient();
		}

		KosmosConfigs.fixConfigRefs();
		KosmosConfigs.saveAllConfigs();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
