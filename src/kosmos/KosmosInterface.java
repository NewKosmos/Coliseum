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
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.sounds.*;
import flounder.standards.*;
import kosmos.chunks.*;
import kosmos.network.packets.*;
import kosmos.particles.*;
import kosmos.shadows.*;
import kosmos.skybox.*;
import kosmos.water.*;
import kosmos.world.*;

import static org.lwjgl.glfw.GLFW.*;

public class KosmosInterface extends Standard {
	private Playlist gamePlaylist;

	private KeyButton screenshot;
	private KeyButton fullscreen;
	private KeyButton polygons;
	private KeyButton aabbs;
	private KeyButton closeWindow;

	private String serverIP;
	private int serverPort;

	public KosmosInterface() {
		super(FlounderDisplay.class, FlounderKeyboard.class, FlounderNetwork.class, FlounderBounding.class, KosmosShadows.class, KosmosParticles.class, KosmosWater.class, KosmosSkybox.class, KosmosWorld.class, KosmosChunks.class);
	}

	@Override
	public void init() {
		gamePlaylist = new Playlist();
		gamePlaylist.addMusic(Sound.loadSoundInBackground(new MyFile(MyFile.RES_FOLDER, "music", "09-hitori-bocchi-1b.wav"), 0.80f, 1.0f));
		FlounderSound.getMusicPlayer().playMusicPlaylist(gamePlaylist, true, 4.0f, 10.0f);

		if (KosmosConfigs.MUSIC_ENABLED.getBoolean()) {
			FlounderSound.getMusicPlayer().unpauseTrack();
		}

		this.screenshot = new KeyButton(GLFW_KEY_F2);
		this.fullscreen = new KeyButton(GLFW_KEY_F11);
		this.polygons = new KeyButton(GLFW_KEY_P);
		this.aabbs = new KeyButton(GLFW_KEY_O);
		this.closeWindow = new KeyButton(GLFW_KEY_DELETE);

		String username = KosmosConfigs.CLIENT_USERNAME.getString();
		this.serverIP = KosmosConfigs.SERVER_IP.setReference(() -> serverIP).getString();
		this.serverPort = KosmosConfigs.SERVER_PORT.setReference(() -> serverPort).getInteger();
		FlounderNetwork.startClient(username, serverIP, serverPort);
		PacketLogin loginPacket = new PacketLogin(username);
		loginPacket.writeData(FlounderNetwork.getSocketClient());

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
				return polygons.wasDown() && !FlounderGuis.getGuiMaster().isGamePaused();
			}

			@Override
			public void onEvent() {
				OpenGlUtils.goWireframe(!OpenGlUtils.isInWireframe());
			}
		});

		/*FlounderEvents.addEvent(new IEvent() {
			private KeyButton key = new KeyButton(GLFW_KEY_E);

			@Override
			public boolean eventTriggered() {
				return key.wasDown() && !FlounderGuis.getGuiMaster().isGamePaused();
			}

			@Override
			public void onEvent() {
				for (Entity entity : FlounderEntities.getEntities().getAll()) {
					String[] path = entity.getClass().getName().split("\\.");
					String name = path[path.length - 1].trim();

					List<IComponentEditor> editorList = new ArrayList<>();

					for (IComponentEntity ce : entity.getComponents()) {
						if (ce instanceof IComponentEditor) {
							editorList.add((IComponentEditor) ce);
						}
					}

					FlounderEntities.save("kosmos.entities.instances", editorList, name);
				}
			}
		});*/

		FlounderEvents.addEvent(new IEvent() {
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
			@Override
			public boolean eventTriggered() {
				return closeWindow.wasDown() && !FlounderGuis.getGuiMaster().isGamePaused();
			}

			@Override
			public void onEvent() {
				Framework.requestClose();
			}
		});

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
	public void dispose() {
		new PacketDisconnect(FlounderNetwork.getUsername()).writeData(FlounderNetwork.getSocketClient());

		//	SteamAPI.shutdown();
		KosmosConfigs.saveAllConfigs();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
