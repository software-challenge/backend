package sc.protocol.requests;

public class RoomRequest implements ILobbyRequest
{
	private String	roomId;
	private Object	data;

	public String getRoomId()
	{
		return this.roomId;
	}

	public Object getData()
	{
		return this.data;
	}
}
