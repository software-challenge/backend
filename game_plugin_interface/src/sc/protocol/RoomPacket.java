package sc.protocol;

import sc.protocol.requests.ILobbyRequest;

public class RoomPacket implements ILobbyRequest
{
	private String	roomId;
	private Object	data;

	public RoomPacket(String roomId, Object o)
	{
		this.roomId = roomId;
		this.data = o;
	}

	public String getRoomId()
	{
		return this.roomId;
	}

	public Object getData()
	{
		return this.data;
	}
}
