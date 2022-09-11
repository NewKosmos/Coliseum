/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.network.packets;

import com.flounder.maths.vectors.*;
import com.flounder.networking.*;
import com.kosmos.world.*;

import java.net.*;

public class PacketLoad extends Packet {
	private String username;
	private float x;
	private float y;
	private float z;
	private float chunkX;
	private float chunkZ;

	public PacketLoad(byte[] data) {
		String[] d = readData(data).split(",");
		this.username = d[0].trim();
		this.x = Float.parseFloat(d[1].trim());
		this.y = Float.parseFloat(d[2].trim());
		this.z = Float.parseFloat(d[3].trim());
		this.chunkX = Float.parseFloat(d[4].trim());
		this.chunkZ = Float.parseFloat(d[5].trim());
	}

	public PacketLoad(String username, Vector3f position, float chunkX, float chunkZ) {
		this.username = username;
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	@Override
	public void writeData(Client client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(Server server) {
		ClientInfo ci = FlounderNetwork.get().getSocketServer().getPlayerMP(username);
		server.sendData(getData(), ci.getIpAddress(), ci.getPort());
	}

	@Override
	public void clientHandlePacket(Client client, InetAddress address, int port) {
		KosmosWorld.get().generateWorld(null, new Vector3f(x, y, z), new Vector3f(chunkX, 0.0f, chunkZ));
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		// Fully client sided packet.
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + username + "," + x + "," + y + "," + z + "," + chunkX + "," + chunkZ).getBytes();
	}

	/**
	 * Gets the username of the client that connected.
	 *
	 * @return The username.
	 */
	public String getUsername() {
		return username;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public float getChunkX() {
		return chunkX;
	}

	public float getChunkZ() {
		return chunkZ;
	}
}
