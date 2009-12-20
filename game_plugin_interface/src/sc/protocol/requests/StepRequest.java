package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("step")
public class StepRequest implements ILobbyRequest
{
	@XStreamAsAttribute
	public String	roomId;

	public StepRequest(String roomId)
	{
		this.roomId = roomId;
	}
}
