package kosmos.network.packets;

import flounder.framework.*;
import flounder.guis.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import kosmos.*;
import kosmos.camera.*;
import kosmos.uis.*;
import kosmos.world.*;

import java.net.*;

/**
 * A packet that is used when a client connects.
 */
public class PacketLogin extends Packet {
	private String username;

	/**
	 * Creates a new connect packet.
	 *
	 * @param data The data to create from.
	 */
	public PacketLogin(byte[] data) {
		this.username = readData(data);
	}

	/**
	 * Creates a new connect packet.
	 *
	 * @param username The username that is connecting.
	 */
	public PacketLogin(String username) {
		this.username = username;
	}

	@Override
	public void writeData(Client client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(Server server) {
		server.sentDataToOtherClient(getData(), username);
	}

	@Override
	public void clientHandlePacket(Client client, InetAddress address, int port) {
		FlounderLogger.log("[" + address.getHostAddress() + ":" + port + "] " + username + " has joined the game.");
		OverlayChat.addText(username + " has joined the game.", new Colour(0.1f, 0.7f, 0.1f));
		OverlayUsernames.addMultiplayer(username);
		KosmosWorld.quePlayer(username, new Vector3f(), new Vector3f());
		KosmosPlayer.askSendData();
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		FlounderLogger.log("[" + address.getHostAddress() + ":" + port + "] " + username + " has connected.");
		ClientInfo player = new ClientInfo(username, address, port);
		server.addConnection(player);
		this.writeData(server);

		// Sends current world data to the new client.
		new PacketWorld(KosmosServer.ServerInterface.serverSeed, Framework.getTimeSec()).writeData(server);

		// If new client connects tell them the connected clients.
		for (ClientInfo info : FlounderNetwork.getSocketServer().getConnected()) {
			new PacketLogin(info.getUsername()).writeData(server);
		}
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + username).getBytes();
	}

	/**
	 * Gets the username of the client that connected.
	 *
	 * @return The username.
	 */
	public String getUsername() {
		return username;
	}
}
