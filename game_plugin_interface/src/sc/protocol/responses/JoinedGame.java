package sc.protocol.responses;

public class JoinedGame
{
	public JoinedGame(String id)
	{
		this.roomId = id;
	}

	private String	roomId;

	public String getRoomId()
	{
		return this.roomId;
	}
}
