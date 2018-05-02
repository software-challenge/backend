package sc.api.plugins.exceptions;


public class TooManyPlayersException extends GameRoomException
{

	public TooManyPlayersException()
	{
		super("This game is full already.");
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID = 1800416641852939259L;

}
