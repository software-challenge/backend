package sc.protocol.clients;

import sc.protocol.ILobbyClientListener;
import sc.protocol.LobbyClient;

public abstract class SingleRoomClient implements ILobbyClientListener
{
	protected final LobbyClient	client;
	protected final String		roomId;

	public SingleRoomClient(LobbyClient client, String roomId)
	{
		this.client = client;
		this.roomId = roomId;
	}
}
