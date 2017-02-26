package kosmos.network;

import flounder.logger.*;
import flounder.networking.*;
import flounder.networking.packets.*;

import java.net.*;

public class PacketSeed extends Packet {
	private int seed;

	public PacketSeed(byte[] data) {
		this.seed = Integer.parseInt(readData(data));
	}

	public PacketSeed(int seed) {
		this.seed = seed;
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
		FlounderLogger.log("[" + address.getHostAddress() + ":" + port + "] " + seed + " is the servers seed!");
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + seed).getBytes();
	}

	public int getSeed() {
		return seed;
	}
}
