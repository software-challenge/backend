package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

@XStreamAlias("freeReservation")
public class FreeReservationRequest extends ProtocolMessage implements ILobbyRequest
{
	@XStreamAsAttribute
	private String	reservation;

        /**
         * might be needed by XStream
         */
        public FreeReservationRequest() {
        }

	public FreeReservationRequest(String reservation)
	{
		this.reservation = reservation;
	}
	
	public String getReservation()
	{
		return this.reservation;
	}
}
