package sc.server.network;

import sc.api.plugins.exceptions.RescueableClientException;

public class AuthenticationFailedException extends RescueableClientException
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8849650876076674212L;

	public AuthenticationFailedException()
	{
		super("Failed to authenticate as administrator");
	}
}
