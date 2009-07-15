package sc.server.gaming;

import sc.api.plugins.exceptions.RescueableClientException;

public class UnknownReservationException extends RescueableClientException
{
	public UnknownReservationException()
	{
		super("Couldn't find a reservation for the provided token.");
	}
}
