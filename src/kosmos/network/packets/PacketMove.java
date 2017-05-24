/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.network.packets;

import flounder.maths.vectors.*;
import flounder.networking.*;
import kosmos.world.*;

import java.net.*;

public class PacketMove extends Packet {
	private String username;
	private float x;
	private float y;
	private float z;
	private float w;
	private float chunkX;
	private float chunkZ;

	public PacketMove(byte[] data) {
		String[] d = readData(data).split(",");
		this.username = d[0].trim();
		this.x = Float.parseFloat(d[1].trim());
		this.y = Float.parseFloat(d[2].trim());
		this.z = Float.parseFloat(d[3].trim());
		this.w = Float.parseFloat(d[4].trim());
		this.chunkX = Float.parseFloat(d[5].trim());
		this.chunkZ = Float.parseFloat(d[6].trim());
	}

	public PacketMove(String username, Vector3f position, Vector3f rotation, float chunkX, float chunkZ) {
		this.username = username;
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
		this.w = rotation.y;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
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
		//	FlounderLogger.log("[" + client + "]: moved to: " + x + "," + y + "," + z + " : " + w + ", chunk[" + chunkX + "," + chunkZ + "]");
		KosmosWorld.get().movePlayer(username, x, y, z, w, chunkX, chunkZ);
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		//	FlounderLogger.log("[" + username + "]: moved to: " + x + "," + y + "," + z + " : " + w + ", chunk[" + chunkX + "," + chunkZ + "]");
		this.writeData(server);
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + username + "," + x + "," + y + "," + z + "," + w + "," + chunkX + "," + chunkZ).getBytes();
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

	public float getW() {
		return w;
	}

	public float getChunkX() {
		return chunkX;
	}

	public float getChunkZ() {
		return chunkZ;
	}
}
