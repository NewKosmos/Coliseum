/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos;

import com.flounder.devices.*;
import com.flounder.events.*;
import com.flounder.framework.*;
import com.flounder.guis.*;
import com.flounder.helpers.*;
import com.flounder.inputs.*;
import com.flounder.maths.*;
import com.flounder.networking.*;
import com.flounder.particles.*;
import com.flounder.renderer.*;
import com.flounder.resources.*;
import com.flounder.shadows.*;
import com.flounder.skybox.*;
import com.flounder.sounds.*;
import com.flounder.standards.*;
import com.flounder.textures.*;
import com.kosmos.network.packets.*;
import com.kosmos.post.*;
import com.kosmos.world.*;

import java.util.*;

import static com.flounder.platform.Constants.*;

public class KosmosInterface extends Standard {
	public KosmosInterface() {
		super(FlounderEvents.class, FlounderNetwork.class, FlounderShadows.class, FlounderParticles.class, FlounderSkybox.class, KosmosPost.class, KosmosWorld.class);
	}

	@Override
	public void init() {
		Framework.get().setFpsLimit(KosmosConfigs.FRAMEWORK_FPS_LIMIT.getInteger());
		FlounderTextures.get().setAnisotropyLevel(KosmosConfigs.TEXTURES_ANISOTROPY_MAX.getFloat());
		FlounderDisplay.get().setWindowSize(KosmosConfigs.DISPLAY_WIDTH.getInteger(), KosmosConfigs.DISPLAY_HEIGHT.getInteger());
		FlounderDisplay.get().setTitle("New Kosmos");
		FlounderDisplay.get().setIcons(new MyFile[]{new MyFile(MyFile.RES_FOLDER, "icon", "icon.png")});
		FlounderDisplay.get().setVSync(KosmosConfigs.DISPLAY_VSYNC.getBoolean());
		FlounderDisplay.get().setAntialiasing(KosmosConfigs.DISPLAY_ANTIALIAS.getBoolean());
		FlounderDisplay.get().setSamples(0);
		FlounderDisplay.get().setFullscreen(KosmosConfigs.DISPLAY_FULLSCREEN.getBoolean());
		FlounderDisplay.get().setHidden(false);
	
		FlounderSound.get().getMusicPlayer().setVolume(KosmosConfigs.MUSIC_VOLUME.setReference(() -> FlounderSound.get().getMusicPlayer().getVolume()).getFloat());
		FlounderSound.get().getSourcePool().setSystemVolume(KosmosConfigs.SOUND_VOLUME.setReference(() -> FlounderSound.get().getSourcePool().getSystemVolume()).getFloat());

		if (KosmosConfigs.MUSIC_ENABLED.setReference(() -> !FlounderSound.get().getMusicPlayer().isPaused()).getBoolean()) {
			FlounderSound.get().getMusicPlayer().unpauseTrack();
		}

		FlounderEvents.get().addEvent(new EventStandard() {
			KeyButton seedRandom = new KeyButton(GLFW_KEY_F6);

			@Override
			public boolean eventTriggered() {
				return seedRandom.wasDown() && FlounderNetwork.get().getSocketClient() == null;
			}

			@Override
			public void onEvent() {
				WorldDefinition d = KosmosWorld.get().getWorld();
				WorldDefinition newWorld = new WorldDefinition(d.getName(), (int) Maths.randomInRange(1.0, 1000000.0), d.getWorldSize(), d.getWorldNoiseSpread(), d.getWorldNoiseFrequency(), d.getWorldNoiseHeight(), d.getWorldIslandInside(), d.getWorldIslandOutside(), d.getWorldIslandParameter(), d.getDayNightCycle(), d.getDayNightRatio(), new HashMap<>(), new HashMap<>());
				KosmosWorld.get().setWorld(newWorld);
			}
		});

		FlounderEvents.get().addEvent(new EventStandard() {
			Sound sound = Sound.loadSoundInBackground(new MyFile(MyFile.RES_FOLDER, "sounds", "screenshot.wav"), 1.0f, 1.0f);
			KeyButton screenshot = new KeyButton(GLFW_KEY_F2);

			@Override
			public boolean eventTriggered() {
				return screenshot.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderSound.get().playSystemSound(sound);
				FlounderDisplay.get().screenshot();
			}
		});

		FlounderEvents.get().addEvent(new EventStandard() {
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

		FlounderEvents.get().addEvent(new EventStandard() {
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

		FlounderEvents.get().addEvent(new EventStandard() {
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
					KosmosWorld.get().deleteWorld(false);
				} else {
					KosmosWorld.get().deleteWorld(true);
				}

				FlounderEvents.get().addEvent(new EventTime(0.6f, false) {
					@Override
					public void onEvent() {
						Framework.get().requestClose(false);
					}
				});
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

		KosmosConfigs.saveAllConfigs();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
