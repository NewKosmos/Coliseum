/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.network.packets;

import com.flounder.logger.*;
import com.flounder.maths.*;
import com.flounder.networking.*;
import com.kosmos.uis.*;

import java.net.*;

public class PacketChat extends Packet {
	private String username;
	private String chatMessage;

	public PacketChat(byte[] data) {
		String[] d = readData(data).split(",");
		this.username = d[0].trim();
		this.chatMessage = d[1].trim();
	}

	public PacketChat(String username, String chatMessage) {
		this.username = username;
		this.chatMessage = chatMessage;
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
		String message = username + " has said: " + chatMessage;
		FlounderLogger.get().log(message);
		OverlayChat.addText(message, new Colour(1.0f, 1.0f, 1.0f));
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		FlounderLogger.get().log("[" + address.getHostAddress() + ":" + port + "] " + username + " has said: " + chatMessage);
		this.writeData(server);
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + username + "," + chatMessage).getBytes();
	}

	public String getUsername() {
		return username;
	}

	public String getChatMessage() {
		return chatMessage;
	}
}
