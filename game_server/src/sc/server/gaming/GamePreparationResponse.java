package sc.server.gaming;

import java.util.List;

public class GamePreparationResponse
{
	private List<String>	reservations;

	public GamePreparationResponse(List<String> reservations)
	{
		this.reservations = reservations;
	}

	public List<String> getReservations()
	{
		return reservations;
	}

}
