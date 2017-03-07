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
import flounder.sounds.*;
import flounder.standards.*;
import kosmos.chunks.*;
import kosmos.network.packets.*;
import kosmos.particles.*;
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

	private String username;
	private String serverIP;
	private int serverPort;

	public KosmosInterface() {
		super(FlounderDisplay.class, FlounderKeyboard.class, FlounderNetwork.class, FlounderBounding.class, KosmosParticles.class, KosmosWater.class, KosmosWorld.class, KosmosChunks.class);
	}

	@Override
	public void init() {
		gamePlaylist = new Playlist();
		//gamePlaylist.addMusic(Sound.loadSoundInBackground(new MyFile(MyFile.RES_FOLDER, "music", "09-hitori-bocchi-1b.wav"), 0.80f, 1.0f));
		//FlounderSound.getMusicPlayer().playMusicPlaylist(gamePlaylist, true, 4.0f, 10.0f);
		//FlounderSound.getMusicPlayer().unpauseTrack();

		this.screenshot = new KeyButton(GLFW_KEY_F2);
		this.fullscreen = new KeyButton(GLFW_KEY_F11);
		this.polygons = new KeyButton(GLFW_KEY_P);
		this.aabbs = new KeyButton(GLFW_KEY_O);
		this.closeWindow = new KeyButton(GLFW_KEY_DELETE);

		this.username = KosmosConfigs.configServer.getStringWithDefault("username", "USERNAME" + ((int) (Math.random() * 10000)), this::getUsername);
		this.serverIP = KosmosConfigs.configServer.getStringWithDefault("connect_ip", "localhost", this::getServerIP);
		this.serverPort = KosmosConfigs.configServer.getIntWithDefault("connect_port", FlounderNetwork.getPort(), this::getServerPort);
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
				return polygons.wasDown();
			}

			@Override
			public void onEvent() {
				if (FlounderGuis.getGuiMaster() != null && !FlounderGuis.getGuiMaster().isGamePaused()) {
					OpenGlUtils.goWireframe(!OpenGlUtils.isInWireframe());
				}
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return aabbs.wasDown();
			}

			@Override
			public void onEvent() {
				if (FlounderGuis.getGuiMaster() != null && !FlounderGuis.getGuiMaster().isGamePaused()) {
					FlounderBounding.toggle(!FlounderBounding.renders());
				}
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return closeWindow.wasDown();
			}

			@Override
			public void onEvent() {
				if (FlounderGuis.getGuiMaster() != null && !FlounderGuis.getGuiMaster().isGamePaused()) {
					Framework.requestClose();
				}
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

	public String getUsername() {
		return username;
	}

	public String getServerIP() {
		return serverIP;
	}

	public int getServerPort() {
		return serverPort;
	}

	@Override
	public void dispose() {
		new PacketDisconnect(FlounderNetwork.getUsername()).writeData(FlounderNetwork.getSocketClient());

		//	SteamAPI.shutdown();
		KosmosConfigs.closeConfigs();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
