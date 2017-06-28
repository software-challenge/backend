package sc.server.network;

import sc.api.plugins.exceptions.RescuableClientException;

/**
 * This Exception will be thrown, if a authentication failed
 */
public class AuthenticationFailedException extends RescuableClientException
{
  /**
   *
   */
  private static final long	serialVersionUID	= 8849650876076674212L;

  /**
   *
   */
  public AuthenticationFailedException()
  {
    super("Failed to authenticate as administrator");
  }
}
