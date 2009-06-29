package sc.api.plugins;


public class TooManyPlayersException extends RescueableClientException
{

	public TooManyPlayersException()
	{
		super("This game is full already.");
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1800416641852939259L;

}
