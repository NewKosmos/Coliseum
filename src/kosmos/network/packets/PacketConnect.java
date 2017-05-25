/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.network.packets;

import flounder.framework.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import kosmos.*;
import kosmos.camera.*;
import kosmos.chunks.*;
import kosmos.uis.*;
import kosmos.world.*;

import java.net.*;

/**
 * A packet that is used when a client connects.
 */
public class PacketConnect extends Packet {
	private String username;

	/**
	 * Creates a new connect packet.
	 *
	 * @param data The data to create from.
	 */
	public PacketConnect(byte[] data) {
		this.username = readData(data);
	}

	/**
	 * Creates a new connect packet.
	 *
	 * @param username The username that is connecting.
	 */
	public PacketConnect(String username) {
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
		FlounderLogger.get().log("[" + address.getHostAddress() + ":" + port + "] " + username + " has joined the game.");
		OverlayChat.addText(username + " has joined the game.", new Colour(0.1f, 0.7f, 0.1f));
		OverlayUsernames.addMultiplayer(username);
		KosmosWorld.get().quePlayer(username, new Vector3f(), new Vector3f());
		KosmosPlayer.askSendData();
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		FlounderLogger.get().log("[" + address.getHostAddress() + ":" + port + "] " + username + " has connected.");
		ClientInfo player = new ClientInfo(username, address, port);
		server.addConnection(player);
		this.writeData(server);

		// Sends current world data to the new client.
		new PacketWorld(KosmosChunks.get().getNoise().getSeed(), Framework.getTimeSec()).writeData(server);

		// If new client connects tell them the connected clients.
		//	for (ClientInfo info : FlounderNetwork.get().getSocketServer().getConnected()) {
		//		if (!info.getUsername().equals(username)) {
		//			server.sendData(new PacketConnect(info.getUsername()).getData(), address, port);
		//		}
		//	}
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
