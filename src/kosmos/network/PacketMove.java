package kosmos.network;

import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.networking.packets.*;
import kosmos.entities.components.*;
import kosmos.entities.instances.*;

import java.net.*;

public class PacketMove extends Packet {
	private String username;
	private float x;
	private float y;
	private float z;
	private float w;

	public PacketMove(byte[] data) {
		String[] d = readData(data).split(",");
		this.username = d[0];
		this.x = Float.parseFloat(d[1].trim());
		this.y = Float.parseFloat(d[2].trim());
		this.z = Float.parseFloat(d[3].trim());
		this.w = Float.parseFloat(d[4].trim());
	}

	public PacketMove(String username, Vector3f position, Vector3f rotation) {
		this.username = username;
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
		this.w = rotation.y;
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
		//	FlounderLogger.log("[" + client + "]: moved to: " + x + "," + y + "," + z + " : " + w);

		//	if (!FlounderNetwork.getUsername().equals(username)) {
		if (!ComponentMultiplayer.players.containsKey(username)) {
			new InstanceMuliplayer(FlounderEntities.getEntities(), new Vector3f(x, y, z), new Vector3f(), username);
		}

		ComponentMultiplayer.players.get(username).move(x, y, z, w);
		//	}
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		//	FlounderLogger.log("[" + port + "]: moved to: " + x + "," + y + "," + z + " : " + w);
		this.writeData(server);
	}

	@Override
	public byte[] getData() {
		return (getDataPrefix() + username + "," + x + "," + y + "," + z + "," + w).getBytes();
	}

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
}
