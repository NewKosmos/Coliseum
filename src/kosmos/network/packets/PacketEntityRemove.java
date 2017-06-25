/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.network.packets;

import flounder.entities.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import kosmos.world.*;
import kosmos.world.chunks.*;

import java.net.*;
import java.util.*;

public class PacketEntityRemove extends Packet {
	private String username;
	private Vector3f chunkPosition;
	private Vector3f entityPosition;

	public PacketEntityRemove(byte[] data) {
		String[] d = readData(data).split(",");
		this.username = d[0].trim();
		this.chunkPosition = new Vector3f(Float.parseFloat(d[1].trim()), Float.parseFloat(d[2].trim()), Float.parseFloat(d[3].trim()));
		this.entityPosition = new Vector3f(Float.parseFloat(d[4].trim()), Float.parseFloat(d[5].trim()), Float.parseFloat(d[6].trim()));
	}

	public PacketEntityRemove(String username, Vector3f chunkPosition, Vector3f entityPosition) {
		this.username = username;
		this.chunkPosition = chunkPosition;
		this.entityPosition = entityPosition;
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
		// Goes though all chunks looking for a match.
		for (Entity entity : FlounderEntities.get().getEntities().getAll(null)) {
			if (entity != null && entity instanceof Chunk) {
				Chunk chunk = (Chunk) entity;

				if (chunk.getPosition().equals(chunkPosition)) {
					chunk.entityRemove(entityPosition);
				}
			}
		}
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		String chunkKey = WorldDefinition.vectorToString(chunkPosition);

		if (!KosmosWorld.get().getWorld().getChunkData().containsKey(chunkKey)) {
			KosmosWorld.get().getWorld().getChunkData().put(chunkKey, new Pair<>(new ArrayList<>(), new ArrayList<>()));
		}

		KosmosWorld.get().getWorld().getChunkData().get(chunkKey).getFirst().add(entityPosition);

		this.writeData(server);
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + username + "," + chunkPosition.x + "," + chunkPosition.y + "," + chunkPosition.z + "," + entityPosition.x + "," + entityPosition.y + "," + entityPosition.z).getBytes();
	}

	public String getUsername() {
		return username;
	}

	public Vector3f getChunkPosition() {
		return chunkPosition;
	}

	public Vector3f getEntityPosition() {
		return entityPosition;
	}
}
