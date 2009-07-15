package sc.protocol.requests;


public class PauseGameRequest implements ILobbyRequest
{
	public String	roomId;
	public boolean	pause;

	public PauseGameRequest(String roomId, boolean pause)
	{
		this.roomId = roomId;
		this.pause = pause;
	}
}
