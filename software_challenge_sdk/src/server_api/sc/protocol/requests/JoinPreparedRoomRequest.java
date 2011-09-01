package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("joinPrepared")
public class JoinPreparedRoomRequest extends JoinRoomRequest
{
	@XStreamAsAttribute
	private String	reservationCode;

        /**
         * might be needed by XStream
         */
        public JoinPreparedRoomRequest() {
        }

	public JoinPreparedRoomRequest(String reservationCode)
	{
		this.reservationCode = reservationCode;
	}

	public String getReservationCode()
	{
		return this.reservationCode;
	}
}
