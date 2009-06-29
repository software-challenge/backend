package sc.protocol.requests;

public class JoinRoomRequest implements ILobbyRequest
{
	private int		roomId		= -1;
	private String	gameType	= null;

	protected JoinRoomRequest()
	{
		// nothing to do
	}

	public JoinRoomRequest(String gameType)
	{
		this.gameType = gameType;
	}

	public JoinRoomRequest(String gameType, int roomId)
	{
		this.roomId = roomId;
	}

	public String getGameType()
	{
		return this.gameType;
	}
}
