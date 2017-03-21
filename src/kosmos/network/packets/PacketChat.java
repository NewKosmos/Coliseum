package kosmos.network.packets;

import flounder.logger.*;
import flounder.networking.*;

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
		FlounderLogger.log(message);
	//	OverlayChat.newMessages.add(message);
	}

	@Override
	public void serverHandlePacket(Server server, InetAddress address, int port) {
		FlounderLogger.log("[" + address.getHostAddress() + ":" + port + "] " + username + " has said: " + chatMessage);
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
