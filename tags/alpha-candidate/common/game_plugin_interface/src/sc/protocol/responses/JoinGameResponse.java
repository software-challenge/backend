package sc.protocol.responses;

public class JoinGameResponse
{
	public JoinGameResponse(String id)
	{
		this.roomId = id;
	}

	private String	roomId;

	public String getRoomId()
	{
		return this.roomId;
	}
}
