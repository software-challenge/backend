package sc.protocol.responses;

public class LeftGameEvent
{
	public LeftGameEvent(String id)
	{
		this.roomId = id;
	}

	private String	roomId;

	public String getRoomId()
	{
		return this.roomId;
	}
}
