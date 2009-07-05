package sc.plugin2010;

/**
 * Ein Spieler wurde aktualisiert. Kann sowohl den eigenen- als auch andere
 * Spieler enthalten.
 * 
 * @author rra
 * @since Jul 5, 2009
 * 
 */
public final class PlayerUpdated
{
	private Player	player;
	private boolean	ownPlayer;

	public PlayerUpdated(final Player p, final boolean own)
	{
		player = p;
		ownPlayer = own;
	}

	public Player getPlayer()
	{
		return player;
	}

	public boolean isOwnPlayer()
	{
		return ownPlayer;
	}
}
