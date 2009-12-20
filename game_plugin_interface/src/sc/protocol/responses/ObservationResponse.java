package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value="observed")
public class ObservationResponse
{
	@XStreamAsAttribute
	private String	roomId;
	
	public ObservationResponse(String roomId)
	{
		this.roomId = roomId;
	}

	public String getRoomId()
	{
		return this.roomId;
	}
}
