package kosmos;

import flounder.framework.*;
import flounder.networking.*;
import flounder.standards.*;
import kosmos.network.packets.*;
import kosmos.world.*;

import java.util.*;

public class KosmosServer extends Framework {
	public static void main(String[] args) {
		KosmosServer server = new KosmosServer();
		server.run();
		System.exit(0);
	}

	public KosmosServer() {
		super("Server", -1, new ServerInterface());
	}

	public static class ServerInterface extends Standard {
		public ServerInterface() {
			super(FlounderNetwork.class, KosmosWorld.class);
		}

		@Override
		public void init() {
			int serverPort = KosmosConfigs.configServer.getIntWithDefault("server_port", FlounderNetwork.getPort(), FlounderNetwork::getPort);
			FlounderNetwork.startServer(serverPort);

			// Remind the clients the time, acts as a "are your there" ping as well.
			Timer timerPing = new Timer();
			timerPing.schedule(new TimerTask() {
				@Override
				public void run() {
					new PacketWorld(KosmosWorld.getNoise().getSeed(), Framework.getTimeSec(), KosmosWorld.getSkyCycle().getDayFactor()).writeData(FlounderNetwork.getSocketServer());
				}
			}, 0, 5000);
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
		}

		@Override
		public boolean isActive() {
			return false;
		}
	}
}
