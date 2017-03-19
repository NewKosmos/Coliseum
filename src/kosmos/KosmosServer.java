package kosmos;

import flounder.devices.*;
import flounder.framework.*;
import flounder.framework.updater.*;
import flounder.logger.*;
import flounder.networking.*;
import flounder.standards.*;
import kosmos.network.packets.*;
import org.lwjgl.glfw.*;

import java.util.*;

public class KosmosServer extends Framework {
	public static void main(String[] args) {
		KosmosServer server = new KosmosServer();
		server.run();
		System.exit(0);
	}

	public KosmosServer() {
		super("Server", new UpdaterDefault(GLFW::glfwGetTime), -1, new ServerInterface());
	}

	public static class ServerInterface extends Standard {
		public static int serverPort;
		public static int serverSeed;

		public ServerInterface() {
			super(FlounderNetwork.class, FlounderDisplay.class);
		}

		@Override
		public void init() {
			ServerInterface.serverPort = KosmosConfigs.HOST_PORT.setReference(() -> serverPort).getInteger();
			ServerInterface.serverSeed = KosmosConfigs.HOST_SEED.setReference(() -> serverSeed).getInteger();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				FlounderLogger.log(e);
			}

			FlounderLogger.log("Server seed: " + serverSeed);
			FlounderNetwork.startServer(serverPort);

			// Remind the clients the time, acts as a "are your there" ping as well.
			Timer timerPing = new Timer();
			timerPing.schedule(new TimerTask() {
				@Override
				public void run() {
					new PacketWorld(serverSeed, Framework.getTimeSec()).writeData(FlounderNetwork.getSocketServer());
				}
			}, 10000, 45000);
		}

		@Override
		public void update() {
		}

		@Override
		public void profile() {

		}

		@Override
		public void dispose() {
			new PacketDisconnect("server").writeData(FlounderNetwork.getSocketServer());
			KosmosConfigs.closeConfigs();
		}

		@Override
		public boolean isActive() {
			return true;
		}
	}
}
