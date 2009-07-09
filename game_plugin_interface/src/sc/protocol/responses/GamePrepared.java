package sc.protocol.responses;

import java.util.List;

public class GamePrepared
{
	String			gameId;
	List<String>	reservations;
	
	public String getGameId()
	{
		return this.gameId;
	}
	
	public List<String> getReservations()
	{
		return this.reservations;
	}
}
