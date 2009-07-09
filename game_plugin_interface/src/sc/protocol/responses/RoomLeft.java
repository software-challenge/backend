package sc.protocol.responses;

public class RoomLeft
{
	public RoomLeft(String id)
	{
		this.roomId = id;
	}

	private String	roomId;

	public String getRoomId()
	{
		return this.roomId;
	}
}
