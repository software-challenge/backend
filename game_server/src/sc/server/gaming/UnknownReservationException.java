package sc.server.gaming;

import sc.api.plugins.exceptions.RescuableClientException;

public class UnknownReservationException extends RescuableClientException
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5547150944276458449L;

	public UnknownReservationException()
	{
		super("Couldn't find a reservation for the provided token.");
	}
}
