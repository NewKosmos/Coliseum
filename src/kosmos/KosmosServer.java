/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos;

import flounder.camera.*;
import flounder.devices.*;
import flounder.fonts.*;
import flounder.framework.*;
import flounder.framework.updater.*;
import flounder.guis.*;
import flounder.logger.*;
import flounder.lwjgl3.*;
import flounder.maths.*;
import flounder.maths.Timer;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.parsing.*;
import flounder.physics.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.standards.*;
import flounder.textures.*;
import flounder.visual.*;
import kosmos.chunks.*;
import kosmos.network.packets.*;
import org.lwjgl.glfw.*;
import sun.reflect.generics.reflectiveObjects.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class KosmosServer extends Framework {
	public static void main(String[] args) {
		new KosmosServer().run();
		System.exit(0);
	}

	public KosmosServer() {
		super("kosmos", new UpdaterDefault(GLFW::glfwGetTime), 10,
				new Extension[]{new ServerInterface(), new ServerRenderer(), new ServerCamera(), new ServerGuis()},
				new Module[]{new PlatformLwjgl(
						870,
						940,
						"New Kosmos Server", new MyFile[]{new MyFile(MyFile.RES_FOLDER, "icon", "icon.png")},
						false,
						false,
						0,
						false,
						true,
						false,
						1
				)});
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
			super(FlounderDisplayJPanel.class, FlounderNetwork.class, KosmosChunks.class);
		}

		@Override
		public void init() {
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
						Framework.requestClose(false);
					} else {
						frame.setVisible(true);
					}
				}
			});

			mainPanel = new JPanel();

			JButton buttonRandomSeed = new JButton("Random Seed");
			buttonRandomSeed.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					KosmosChunks.get().getNoise().setSeed((int) Maths.randomInRange(1.0, 1000000.0));
					new PacketWorld(KosmosChunks.get().getNoise().getSeed(), Framework.getTimeSec()).writeData(FlounderNetwork.get().getSocketServer());
				}
			});
			mainPanel.add(buttonRandomSeed);

			JButton buttonShutdown = new JButton("Shutdown");
			buttonShutdown.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new PacketDisconnect("server").writeData(FlounderNetwork.get().getSocketServer());
					Framework.requestClose(false);
				}
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
			KosmosChunks.get().getNoise().setSeed(ServerConfigs.HOST_SEED.setReference(() -> KosmosChunks.get().getNoise().getSeed()).getInteger());
		}

		@Override
		public void update() {
			// Remind the clients the time, acts as a "are your there" ping as well.
			if (timerWorld.isPassedTime()) {
				new PacketWorld(KosmosChunks.get().getNoise().getSeed(), Framework.getTimeSec()).writeData(FlounderNetwork.get().getSocketServer());
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
				mapViewTexture.setTexture(KosmosChunks.get().getMapGenerator().getMapTexture());

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
			throw new NotImplementedException();
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
			throw new NotImplementedException();
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
