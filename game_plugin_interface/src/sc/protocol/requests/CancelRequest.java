package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


public class CancelRequest implements ILobbyRequest
{
	@XStreamAsAttribute
	public String	roomId;

	public CancelRequest(String roomId)
	{
		this.roomId = roomId;
	}
}
