package sc.protocol.responses;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias(value = "prepared")
public class PrepareGameResponse
{
	@XStreamImplicit(itemFieldName = "reservation")
	private List<String>	reservations;

	@XStreamAsAttribute
	private String			roomId;

	public PrepareGameResponse(String roomId, List<String> reservations)
	{
		this.roomId = roomId;
		this.reservations = reservations;
	}

	public List<String> getReservations()
	{
		return this.reservations;
	}

	public String getRoomId()
	{
		return this.roomId;
	}

}
