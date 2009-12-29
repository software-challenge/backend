package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("step")
public class StepRequest implements ILobbyRequest
{
	@XStreamAsAttribute
	public String	roomId;

	@XStreamAsAttribute
	public boolean	forced;

	public StepRequest(String roomId)
	{
		this(roomId, false);
	}

	public StepRequest(String roomId, boolean forced)
	{
		this.roomId = roomId;
		this.forced = forced;
	}
}
