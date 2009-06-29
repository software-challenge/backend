package sc.protocol.requests;

public class JoinPreparedRoomRequest extends JoinRoomRequest
{
	private String	reservationCode;

	public JoinPreparedRoomRequest(String reservationCode)
	{
		this.reservationCode = reservationCode;
	}

	public String getReservationCode()
	{
		return this.reservationCode;
	}
}
