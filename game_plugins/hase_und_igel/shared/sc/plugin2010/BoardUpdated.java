package sc.plugin2010;

/**
 * Das Spielbrett wurde aktualisiert. Enthält ein mit XStream serialisiertes
 * Board.
 * 
 * @author rra
 * @since Jul 5, 2009
 * 
 */
public final class BoardUpdated
{
	private Board	board;

	public BoardUpdated(final Board b)
	{
		board = b;
	}

	public Board getBoard()
	{
		return board;
	}
}
