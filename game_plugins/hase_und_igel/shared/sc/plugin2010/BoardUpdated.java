package sc.plugin2010;

/**
 * Das Spielbrett wurde aktualisiert. Enthält ein mit XStream serialisiertes
 * Board sowie den aktuellen Rundezähler
 *
 * @author rra
 * @since Jul 5, 2009
 * 
 */
public final class BoardUpdated
{
	private Board	board;
	private int		round;

	public BoardUpdated(final Board b, final int round)
	{
		board = b;
		this.round = round;
	}

	public Board getBoard()
	{
		return board;
	}
	
	public int getRound()
	{
		return round;
	}
}
