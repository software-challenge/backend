package sc.server.gaming;

import sc.api.plugins.exceptions.RescuableClientException;

/**
 * Exception that will be thrown if a redeem Code is not known
 */
public class UnknownReservationException extends RescuableClientException
{
  /**
   *  generated serialVersionUID
   */
  private static final long	serialVersionUID	= 5547150944276458449L;

  /**
   * Constructor which creates object
   */
  public UnknownReservationException()
  {
    super("Couldn't find a reservation for the provided token.");
  }
}
