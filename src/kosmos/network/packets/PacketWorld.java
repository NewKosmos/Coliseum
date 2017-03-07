package kosmos.network.packets;

import flounder.logger.*;
import flounder.networking.*;

import java.net.*;

public class PacketWorld extends Packet {
	private int seed;
	private float timeSec;
	private float dayFactor;

	public PacketWorld(byte[] data) {
		String[] d = readData(data).split(",");
		this.seed = Integer.parseInt(d[0].trim());
		this.timeSec = Float.parseFloat(d[1].trim());
		this.dayFactor = Float.parseFloat(d[2].trim());
	}

	public PacketWorld(int seed, float timeSec, float dayFactor) {
		this.seed = seed;
		this.timeSec = timeSec;
		this.dayFactor = dayFactor;
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
		FlounderLogger.log("[" + address.getHostAddress() + ":" + port + "]: world seed=" + seed + ", framework time=" + timeSec + ", day factor: " + dayFactor);
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + seed + "," + timeSec + "," + dayFactor).getBytes();
	}

	public int getSeed() {
		return seed;
	}

	public float getTimeSec() {
		return timeSec;
	}

	public float getDayFactor() {
		return dayFactor;
	}
}
