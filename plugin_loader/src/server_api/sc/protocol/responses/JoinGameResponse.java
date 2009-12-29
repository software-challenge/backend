package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value="joined")
public class JoinGameResponse
{
	@XStreamAsAttribute
	private String	roomId;
	
	public JoinGameResponse(String id)
	{
		this.roomId = id;
	}

	public String getRoomId()
	{
		return this.roomId;
	}
}
