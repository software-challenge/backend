package sc.protocol.requests;

public class FreeReservationRequest implements ILobbyRequest
{
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
