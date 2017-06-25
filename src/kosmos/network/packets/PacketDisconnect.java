/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.network.packets;

import flounder.guis.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.networking.*;
import kosmos.*;
import kosmos.uis.*;
import kosmos.world.*;

import java.net.*;

/**
 * A packet that is used when a client / server disconnects.
 */
public class PacketDisconnect extends Packet {
	private String username;

	/**
	 * Creates a new disconnect packet.
	 *
	 * @param data The data to create from.
	 */
	public PacketDisconnect(byte[] data) {
		this.username = readData(data);
	}

	/**
	 * Creates a new disconnect packet.
	 *
	 * @param username The username that is disconnecting.
	 */
	public PacketDisconnect(String username) {
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
		if (username.equals("server")) {
			FlounderLogger.get().log("The server is closing!");
			OverlayChat.addText("The server is closing!", new Colour(0.7f, 0.1f, 0.1f));

			if (FlounderNetwork.get().getSocketClient() != null) {
				new PacketDisconnect(FlounderNetwork.get().getUsername()).writeData(FlounderNetwork.get().getSocketClient());
				FlounderNetwork.get().closeClient();
			}

			// Deletes the clients world.
			KosmosWorld.get().deleteWorld(false);

			// Closes all huds and opens the start screen.
			((KosmosGuis) FlounderGuis.get().getGuiMaster()).forceCloseHUDs();
			((KosmosGuis) FlounderGuis.get().getGuiMaster()).getOverlaySlider().sliderStartMenu(true);
		} else {
			FlounderLogger.get().log("[" + address.getHostAddress() + ":" + port + "] " + username + " has quit the game.");
			OverlayChat.addText(username + " has quit the game.", new Colour(0.7f, 0.1f, 0.1f));

			// Removes the username from the player list.
			KosmosWorld.get().removePlayer(username);
		}
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		FlounderLogger.get().log("[" + address.getHostAddress() + ":" + port + "] " + username + " has disconnected.");
		server.removeConnection(username);
		this.writeData(server);
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + username).getBytes();
	}

	/**
	 * Gets the username of the client that disconnected.
	 *
	 * @return The username.
	 */
	public String getUsername() {
		return username;
	}
}
