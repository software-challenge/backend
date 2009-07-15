package sc.protocol.requests;


public class StepRequest implements ILobbyRequest
{
	public String	roomId;

	public StepRequest(String roomId)
	{
		this.roomId = roomId;
	}
}
