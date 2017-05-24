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
import flounder.networking.*;
import kosmos.chunks.*;
import kosmos.world.*;

import java.net.*;

public class PacketWorld extends Packet {
	private int seed;
	private float timeSec;

	public PacketWorld(byte[] data) {
		String[] d = readData(data).split(",");
		this.seed = Integer.parseInt(d[0].trim());
		this.timeSec = Float.parseFloat(d[1].trim());
	}

	public PacketWorld(int seed, float timeSec) {
		this.seed = seed;
		this.timeSec = timeSec;
	}

	@Override
	public void writeData(Client client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(Server server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public void clientHandlePacket(Client client, InetAddress address, int port) {
		boolean offServerTime = Math.abs(timeSec - Framework.getTimeSec()) > 0.866f;

		//	FlounderLogger.log("[" + address.getHostAddress() + ":" + port + "]: world seed=" + seed + ", off server time=" + offServerTime +
		//			", server time=" + timeSec + ", client time: " + Framework.getTimeSec() + ", client offset: " + Framework.getTimeOffset() +
		//			", client original time: " + (Framework.getTimeSec() - Framework.getTimeOffset())
		//	);

		if (KosmosWorld.get().getNoise().getSeed() != seed) {
			KosmosWorld.get().getNoise().setSeed(seed);
			KosmosChunks.get().clear(true);
		}

		if (offServerTime) {
			Framework.setTimeOffset(timeSec - Framework.getTimeSec() - Framework.getTimeOffset());
		}
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		// Fully client sided packet.
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + seed + "," + timeSec).getBytes();
	}

	public int getSeed() {
		return seed;
	}

	public float getTimeSec() {
		return timeSec;
	}
}
