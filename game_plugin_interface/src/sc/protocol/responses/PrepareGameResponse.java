package sc.protocol.responses;

import java.util.List;

public class PrepareGameResponse
{
	private List<String>	reservations;
	private String roomId;

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
