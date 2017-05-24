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
import flounder.framework.*;
import flounder.framework.updater.*;
import flounder.logger.*;
import flounder.lwjgl3.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.physics.*;
import flounder.resources.*;
import flounder.standards.*;
import kosmos.network.packets.*;
import org.lwjgl.glfw.*;
import sun.reflect.generics.reflectiveObjects.*;

public class KosmosServer extends Framework {
	public static void main(String[] args) {
		new KosmosServer().run();
		System.exit(0);
	}

	public KosmosServer() {
		super("kosmos", new UpdaterDefault(GLFW::glfwGetTime), 30,
				new Extension[]{new ServerInterface(), new EmptyCamera()},
				new Module[]{new PlatformLwjgl(
						100,
						100,
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

	public static class ServerInterface extends Standard {
		public static int serverPort;
		public static int serverSeed;

		private Timer timerWorld;

		public ServerInterface() {
			super(FlounderNetwork.class);
		}

		@Override
		public void init() {
			ServerInterface.serverPort = KosmosConfigs.HOST_PORT.setReference(() -> serverPort).getInteger();
			ServerInterface.serverSeed = KosmosConfigs.HOST_SEED.setReference(() -> serverSeed).getInteger();

			this.timerWorld = new Timer(15.0f);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				FlounderLogger.get().log(e);
			}

			FlounderLogger.get().log("Server seed: " + serverSeed);
			FlounderNetwork.get().startServer(serverPort);
		}

		@Override
		public void update() {
			// Remind the clients the time, acts as a "are your there" ping as well.
			if (timerWorld.isPassedTime()) {
				new PacketWorld(serverSeed, Framework.getTimeSec()).writeData(FlounderNetwork.get().getSocketServer());
				timerWorld.resetStartTime();
			}
		}

		@Override
		public void profile() {

		}

		@Override
		public void dispose() {
			new PacketDisconnect("server").writeData(FlounderNetwork.get().getSocketServer());
			KosmosConfigs.saveAllConfigs();
		}

		@Override
		public boolean isActive() {
			return true;
		}
	}

	public static class EmptyCamera extends Camera {
		private Vector3f position;
		private Vector3f rotation;
		private Frustum viewFrustum;
		private Matrix4f viewMatrix;
		private Matrix4f projectionMatrix;

		public EmptyCamera() {
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
}
