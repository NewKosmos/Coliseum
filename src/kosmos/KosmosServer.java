package kosmos;

import flounder.framework.*;
import flounder.framework.updater.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.networking.*;
import flounder.standards.*;
import kosmos.network.packets.*;

import java.util.*;
import java.util.Timer;

public class KosmosServer extends Framework {
	public static void main(String[] args) {
		KosmosServer server = new KosmosServer();
		server.run();
		System.exit(0);
	}

	public KosmosServer() {
		super("Server", new UpdaterDefault(), -1, new ServerInterface());
	}

	public static class ServerInterface extends Standard {
		public static int serverPort;
		public static int serverSeed;
		public static Scanner scanner;

		public ServerInterface() {
			super(FlounderNetwork.class);
		}

		@Override
		public void init() {
			serverPort = KosmosConfigs.configServer.getIntWithDefault("server_port", FlounderNetwork.getPort(), () -> serverPort);
			serverSeed = KosmosConfigs.configServer.getIntWithDefault("server_seed", (int) Maths.randomInRange(1.0, 10000.0), () -> serverSeed);
			scanner = new Scanner(System.in);

			try {
				Thread.sleep(10);
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
			if (scanner.hasNext()) {
				String string = scanner.next().trim();

				if (string.toLowerCase().equals("exit") || string.toLowerCase().equals("q")) {
					Framework.requestClose();
				}
			}
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
