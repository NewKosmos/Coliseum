package kosmos;

import flounder.framework.*;
import flounder.networking.*;
import flounder.standards.*;

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
			super(FlounderNetwork.class);
		}

		@Override
		public void init() {
			int serverPort = KosmosConfigs.configServer.getIntWithDefault("server_port", FlounderNetwork.getPort(), FlounderNetwork::getPort);
			FlounderNetwork.startServer(serverPort);
		}

		@Override
		public void update() {

		}

		@Override
		public void profile() {

		}

		@Override
		public void dispose() {

		}

		@Override
		public boolean isActive() {
			return false;
		}
	}
}
