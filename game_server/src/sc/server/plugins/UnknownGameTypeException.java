package sc.server.plugins;

import sc.api.plugins.exceptions.RescueableClientException;

public class UnknownGameTypeException extends RescueableClientException
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6520842646313711672L;

	public UnknownGameTypeException(String string)
	{
		super(string);
	}

}
