/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos;

import com.flounder.camera.*;
import com.flounder.devices.*;
import com.flounder.fonts.*;
import com.flounder.framework.*;
import com.flounder.framework.updater.*;
import com.flounder.guis.*;
import com.flounder.logger.*;
import com.flounder.maths.*;
import com.flounder.maths.Timer;
import com.flounder.maths.matrices.*;
import com.flounder.maths.vectors.*;
import com.flounder.networking.*;
import com.flounder.parsing.config.*;
import com.flounder.physics.*;
import com.flounder.renderer.*;
import com.flounder.resources.*;
import com.flounder.standards.*;
import com.flounder.textures.*;
import com.flounder.visual.*;
import com.kosmos.network.packets.*;
import com.kosmos.world.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class KosmosServer extends Framework {
	public static void main(String[] args) {
		new KosmosServer().run();
		System.exit(0);
	}

	public KosmosServer() {
		super(
				"kosmos", new UpdaterDefault(), 5,
				new Extension[]{new ServerInterface(), new ServerRenderer(), new ServerCamera(), new ServerGuis()}
		);
		//Framework.get().addOverrides(new PlatformLwjgl(
		//		870,
		//		940,
		//		"New Kosmos Server", new MyFile[]{new MyFile(MyFile.RES_FOLDER, "icon", "icon.png")},
		//		false,
		//		false,
		//		0,
		//		false,
		//		true,
		//		false,
		//		1
		//));
	}

	public static class ServerConfigs {
		// Host server configs.
		private static final Config CONFIG_HOST = new Config(new MyFile(Framework.getRoamingFolder("kosmos"), "configs", "host.conf"));
		public static final ConfigData HOST_PORT = CONFIG_HOST.getData(ConfigSection.SEVER, "hostPort", FlounderNetwork.DEFAULT_PORT, () -> FlounderNetwork.get().getPort()); // Reference set in server interface.
		public static final ConfigData HOST_SEED = CONFIG_HOST.getData(ConfigSection.WORLD, "hostSeed", (int) Maths.randomInRange(1.0, 1000000.0)); // Reference set in server interface.

		/**
		 * Saves the configs when closing the game.
		 */
		public static void saveAllConfigs() {
			CONFIG_HOST.save();
		}
	}

	public static class ServerInterface extends Standard {
		private static JFrame frame;
		private static JPanel mainPanel;
		private static JPanel renderPanel;

		public static int serverPort;

		private Timer timerWorld;

		public ServerInterface() {
			super(FlounderDisplayJPanel.class, FlounderNetwork.class, KosmosWorld.class);
		}

		@Override
		public void init() {
			WorldDefinition world = WorldDefinition.load("Server1");

			if (world == null) {
				world = new WorldDefinition("Server1", (int) Maths.randomInRange(1.0, 1000000.0), 2048, 350.0f, 40.0f, 20.0f, 0.8f, 1.0f, 0.2f, 500.0f, 0.5f, new HashMap<>(), new HashMap<>());
			}

			// Generates the world.
			KosmosWorld.get().generateWorld(
					world,
					new Vector3f(),
					new Vector3f()
			);

			frame = new JFrame();
			frame.setTitle(FlounderDisplay.get().getTitle());
			frame.setSize(FlounderDisplay.get().getWidth(), FlounderDisplay.get().getHeight());
			frame.setLayout(new BorderLayout());
			frame.setResizable(true);

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
				ex.printStackTrace();
			}

			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent windowEvent) {
					if (JOptionPane.showConfirmDialog(frame,
							"Are you sure to close this editor?", "Any unsaved work will be lost!",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
						Framework.get().requestClose(false);
					} else {
						frame.setVisible(true);
					}
				}
			});

			mainPanel = new JPanel();

			JButton buttonRandomSeed = new JButton("Random Seed");
			buttonRandomSeed.addActionListener(e -> {
				WorldDefinition d = KosmosWorld.get().getWorld();
				KosmosWorld.get().setWorld(new WorldDefinition(d.getName(), (int) Maths.randomInRange(1.0, 1000000.0), d.getWorldSize(), d.getWorldNoiseSpread(), d.getWorldNoiseFrequency(), d.getWorldNoiseHeight(), d.getWorldIslandInside(), d.getWorldIslandOutside(), d.getWorldIslandParameter(), d.getDayNightCycle(), d.getDayNightRatio(), d.getPlayers(), d.getChunkData()));
				new PacketWorld(Framework.get().getTimeSec(), KosmosWorld.get().getWorld()).writeData(FlounderNetwork.get().getSocketServer());
			});
			mainPanel.add(buttonRandomSeed);

			JButton buttonSave = new JButton("Save");
			buttonSave.addActionListener(e -> {
				if (KosmosWorld.get().getWorld() != null) {
					KosmosWorld.get().getWorld().save();
				}

				ServerConfigs.saveAllConfigs();
			});
			mainPanel.add(buttonSave);

			JButton buttonShutdown = new JButton("Shutdown");
			buttonShutdown.addActionListener(e -> {
				new PacketDisconnect("server").writeData(FlounderNetwork.get().getSocketServer());
				Framework.get().requestClose(false);
			});
			mainPanel.add(buttonShutdown);

			frame.add(mainPanel, BorderLayout.SOUTH);

			renderPanel = FlounderDisplayJPanel.get().createPanel();
			frame.add(renderPanel, BorderLayout.CENTER);

			frame.setLocationByPlatform(true);
			frame.setVisible(true);
			frame.toFront();

			ServerInterface.serverPort = ServerConfigs.HOST_PORT.setReference(() -> serverPort).getInteger();
			//ServerInterface.serverSeed = ServerConfigs.HOST_SEED.setReference(() -> serverSeed).getInteger();

			this.timerWorld = new Timer(10.0f);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				FlounderLogger.get().log(e);
			}

			FlounderNetwork.get().startServer(serverPort);
			//	KosmosWorld.get().getWorld().getNoise().setSeed(ServerConfigs.HOST_SEED.setReference(() -> KosmosWorld.get().getWorld().getNoise().getSeed()).getInteger());
		}

		@Override
		public void update() {
			// Remind the clients the time, acts as a "are your there" ping as well.
			if (timerWorld.isPassedTime()) {
				new PacketWorld(Framework.get().getTimeSec(), KosmosWorld.get().getWorld()).writeData(FlounderNetwork.get().getSocketServer());
				timerWorld.resetStartTime();
			}
		}

		@Override
		public void profile() {

		}

		@Override
		public void dispose() {
			if (FlounderNetwork.get().getSocketServer() != null) {
				new PacketDisconnect("server").writeData(FlounderNetwork.get().getSocketServer());
				FlounderNetwork.get().closeServer();
			}

			ServerConfigs.saveAllConfigs();
		}

		@Override
		public boolean isActive() {
			return true;
		}
	}

	public static class ServerGuis extends GuiMaster {
		private ServerMap serverMap;

		public ServerGuis() {
			super();
		}

		@Override
		public void init() {
			this.serverMap = new ServerMap(FlounderGuis.get().getContainer());
		}

		@Override
		public void update() {
		}

		@Override
		public void profile() {

		}

		@Override
		public boolean isGamePaused() {
			return false;
		}

		@Override
		public float getBlurFactor() {
			return 0;
		}

		@Override
		public Colour getPrimaryColour() {
			return null;
		}

		@Override
		public void dispose() {

		}

		@Override
		public boolean isActive() {
			return true;
		}

		public class ServerMap extends ScreenObject {
			private static final float VIEW_SIZE_X = 1.0f;
			private static final float VIEW_SIZE_Y = 1.0f;
			private static final float VIEW_POSITION_X = 0.5f;
			private static final float VIEW_POSITION_Y = 0.5f;

			private GuiObject backgroundView;
			private GuiObject mapViewTexture;
			//private GuiObject playerPosition;

			public ServerMap(ScreenObject parent) {
				super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
				super.setInScreenCoords(false);

				this.backgroundView = new GuiObject(this, new Vector2f(VIEW_POSITION_X, VIEW_POSITION_Y), new Vector2f(VIEW_SIZE_X, VIEW_SIZE_Y), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "map.png")).create(), 1);
				this.backgroundView.setAlphaDriver(new ConstantDriver(0.9f));
				this.backgroundView.setInScreenCoords(true);

				this.mapViewTexture = new GuiObject(this, new Vector2f(VIEW_POSITION_X, VIEW_POSITION_Y), new Vector2f(VIEW_SIZE_X, VIEW_SIZE_Y), null, 1);
				this.mapViewTexture.setAlphaDriver(new ConstantDriver(0.8f));
				this.mapViewTexture.setInScreenCoords(true);

				//	this.playerPosition = new GuiObject(this, new Vector2f(0.5f, 0.5f), new Vector2f(0.02f, 0.02f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "pointer.png")).create(), 1);
				//	this.playerPosition.setInScreenCoords(false);
			}

			@Override
			public void updateObject() {
				mapViewTexture.setTexture(KosmosWorld.get().getMapTexture());

			/*	Entity player = KosmosWorld.get().getEntityPlayer();

				if (player != null) {
					float px = player.getPosition().x / Chunk.WORLD_SIZE;
					float pz = player.getPosition().z / Chunk.WORLD_SIZE;
					playerPosition.getPosition().set(
							(((mapViewTexture.isInScreenCoords() ? FlounderDisplay.get().getAspectRatio() : 1.0f) * VIEW_POSITION_X) - (VIEW_SIZE_X * 0.5f)) + (VIEW_SIZE_X * px) + (VIEW_SIZE_X * 0.5f),
							(VIEW_POSITION_Y - (VIEW_SIZE_Y * 0.5f)) + (VIEW_SIZE_Y * pz) + (VIEW_SIZE_Y * 0.5f)
					);
					playerPosition.setRotationDriver(new ConstantDriver(-player.getRotation().y + 180.0f));
				}*/
			}

			@Override
			public void deleteObject() {
			}
		}
	}

	public static class ServerCamera extends Camera {
		private Vector3f position;
		private Vector3f rotation;
		private Frustum viewFrustum;
		private Matrix4f viewMatrix;
		private Matrix4f projectionMatrix;

		public ServerCamera() {
			this.position = new Vector3f();
			this.rotation = new Vector3f();
			this.viewFrustum = new Frustum();
			this.viewMatrix = new Matrix4f();
			this.projectionMatrix = new Matrix4f();
		}

		@Override
		public void init() {

		}

		@Override
		public float getNearPlane() {
			return 0.1f;
		}

		@Override
		public float getFarPlane() {
			return 512.0f;
		}

		@Override
		public float getFOV() {
			return 45.0f;
		}

		@Override
		public void update(Player player) {

		}

		@Override
		public Frustum getViewFrustum() {
			return viewFrustum;
		}

		@Override
		public Ray getViewRay() {
			throw new IllegalStateException("Not implemented");
		}

		@Override
		public Matrix4f getViewMatrix() {
			return viewMatrix;
		}

		@Override
		public Matrix4f getProjectionMatrix() {
			return projectionMatrix;
		}

		@Override
		public void reflect(float waterHeight) {
			throw new IllegalStateException("Not implemented");
		}

		@Override
		public Vector3f getPosition() {
			return position;
		}

		@Override
		public Vector3f getRotation() {
			return rotation;
		}

		@Override
		public void setRotation(Vector3f rotation) {
			this.rotation.set(rotation);
		}

		@Override
		public boolean isActive() {
			return true;
		}
	}

	public static class ServerRenderer extends RendererMaster {
		private GuisRenderer guisRenderer;
		private FontRenderer fontRenderer;

		public ServerRenderer() {
			super(FlounderDisplay.class);
		}

		@Override
		public void init() {
			this.guisRenderer = new GuisRenderer();
			this.fontRenderer = new FontRenderer();
		}

		@Override
		public void render() {
			guisRenderer.render(null, null);
			fontRenderer.render(null, null);
		}

		@Override
		public void profile() {
		}

		@Override
		public void dispose() {
			guisRenderer.dispose();
			fontRenderer.dispose();
		}

		@Override
		public boolean isActive() {
			return true;
		}
	}
}
