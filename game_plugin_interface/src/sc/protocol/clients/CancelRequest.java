package sc.protocol.clients;

import sc.protocol.requests.ILobbyRequest;

public class CancelRequest implements ILobbyRequest
{
	public String	roomId;

	public CancelRequest(String roomId)
	{
		this.roomId = roomId;
	}
}
