package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("freeReservation")
public class FreeReservationRequest implements ILobbyRequest
{
	@XStreamAsAttribute
	private String	reservation;

	public FreeReservationRequest(String reservation)
	{
		this.reservation = reservation;
	}
	
	public String getReservation()
	{
		return this.reservation;
	}
}
