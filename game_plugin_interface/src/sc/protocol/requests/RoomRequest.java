package sc.protocol.requests;

public class RoomRequest implements ILobbyRequest
{
	private String	roomId;
	private Object	data;

	public RoomRequest(String roomId, Object o)
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
