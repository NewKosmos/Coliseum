/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.network.packets;

import flounder.framework.*;
import flounder.maths.*;
import flounder.networking.*;
import kosmos.uis.*;
import kosmos.world.*;

import java.net.*;
import java.util.*;

public class PacketWorld extends Packet {
	private float timeSec;
	private WorldDefinition world;

	public PacketWorld(byte[] data) {
		String[] d = readData(data).split(",");

		this.timeSec = Float.parseFloat(d[0].trim());

		int seed = Integer.parseInt(d[1].trim());
		int worldSize = Integer.parseInt(d[2].trim());
		float worldNoiseSpread = Float.parseFloat(d[3].trim());
		float worldNoiseFrequency = Float.parseFloat(d[4].trim());
		float worldNoiseHeight = Float.parseFloat(d[5].trim());
		float worldIslandInside = Float.parseFloat(d[6].trim());
		float worldIslandOutside = Float.parseFloat(d[7].trim());
		float worldIslandParameter = Float.parseFloat(d[8].trim());
		float dayNightCycle = Float.parseFloat(d[9].trim());
		float dayNightRatio = Float.parseFloat(d[10].trim());
		this.world = new WorldDefinition("server", seed, worldSize, worldNoiseSpread, worldNoiseFrequency, worldNoiseHeight, worldIslandInside, worldIslandOutside, worldIslandParameter, dayNightCycle, dayNightRatio, new HashMap<>(), new ArrayList<>());
	}

	public PacketWorld(float timeSec, WorldDefinition world) {
		this.timeSec = timeSec;
		this.world = world;
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
		boolean offServerTime = Math.abs(timeSec - Framework.get().getTimeSec()) > 1.5f;

		//	FlounderLogger.get().log("[" + address.getHostAddress() + ":" + port + "]: world seed=" + world.getSeed() + ", off server time=" + offServerTime +
		//			", server time=" + timeSec + ", client time: " + Framework.getTimeSec() + ", client offset: " + Framework.getTimeOffset() +
		//			", client original time: " + (Framework.getTimeSec() - Framework.getTimeOffset())
		//	);

		if (!world.equals(KosmosWorld.get().getWorld())) {
			KosmosWorld.get().setWorld(world);
		}

		if (offServerTime) {
			OverlayChat.addText(
					"Server time=" + timeSec + ", client time: " + Framework.get().getTimeSec() + ", client offset: " + Framework.get().getTimeOffset() +
							", client original time: " + (Framework.get().getTimeSec() - Framework.get().getTimeOffset()), new Colour(0.8f, 0.8f, 0.1f)
			);
			Framework.get().setTimeOffset(timeSec - (Framework.get().getTimeSec() - Framework.get().getTimeOffset()));
		}
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		// Fully client sided packet.
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + timeSec + "," + world.getSeed() +
				"," + world.getWorldSize() +
				"," + world.getWorldNoiseSpread() +
				"," + world.getWorldNoiseFrequency() +
				"," + world.getWorldNoiseHeight() +
				"," + world.getWorldIslandInside() +
				"," + world.getWorldIslandOutside() +
				"," + world.getWorldIslandParameter() +
				"," + world.getDayNightCycle() +
				"," + world.getDayNightRatio()).getBytes();
	}

	public float getTimeSec() {
		return timeSec;
	}

	public WorldDefinition getWorld() {
		return world;
	}
}
