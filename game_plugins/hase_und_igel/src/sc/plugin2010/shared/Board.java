package sc.plugin2010.shared;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author rra
 * @since Jul 1, 2009
 * 
 */
public class Board
{
	/**
	 * Die unterschiedlichen Spielfelder aus dem Hase und Igel Original
	 */
	public enum FieldTyp
	{
		/**
		 * Zahl- und Flaggenfelder
		 */
		POSITION_1, POSITION_2, POSITION_3, POSITION_4, POSITION_5, POSITION_6,
		/**
		 * Igelfeld
		 */
		HEDGEHOG,
		/**
		 * Salatfeld
		 */
		SALAD,
		/**
		 * Karottenfeld
		 */
		CARROT,
		/**
		 * Hasenfeld
		 */
		RABBIT,
		/**
		 * Außerhalb des Spielfeldes
		 */
		INVALID,
		/**
		 * Das Zielfeld
		 */
		GOAL,
		/**
		 * Das Startfeld
		 */
		START,
	}

	private List<FieldTyp>	track;
	protected List<Player>	players;

	protected Board()
	{
		track = new LinkedList<FieldTyp>();
		players = new LinkedList<Player>();
	}
	
	/**
	 * Erstellt und initialisiert ein Spielbrett
	 * @param length
	 * @return
	 */
	protected static Board create(final int length)
	{
		Board b = new Board();
		b.initialize(length);
		return b;
	}

	/**
	 * Erstellt eine zufällige Rennstrecke
	 * 
	 * @param length
	 *            Die Länge der Rennstrecke, inklusive Start- und Zielfeld
	 */
	protected void initialize(final int length)
	{
		// TODO Algorithmus
		track.add(FieldTyp.START);
		for (int i = 2; i < length; i++)
		{
			track.add(FieldTyp.CARROT);
		}
		track.add(FieldTyp.GOAL);
	}

	protected void addPlayer(final Player player)
	{
		players.add(player);
	}

	protected void addPlayers(final Player[] players)
	{
		this.players.addAll(Arrays.asList(players));
	}

	/**
	 * Überprüft ob ein Feld durch einen anderen Spieler belegt ist.
	 * 
	 * @param pos
	 *            die Position auf der Rennstrecke
	 * @return
	 */
	public final boolean isOccupied(final int pos)
	{
		boolean occupied = false;
		for (final Player f : players)
		{
			occupied = occupied || f.getPosition() == pos;
		}
		return occupied;
	}

	/**
	 * Gibt den Feldtypen an einer bestimmten Position zurück. Liegt die
	 * gewählte Position vor dem Startpunkt oder hinter dem Ziel, so wird
	 * <code>INVALID</code> zurückgegeben.
	 * 
	 * @param pos
	 *            die Position auf der Rennstrecke
	 * @return
	 */
	public final FieldTyp getTypeAt(final int pos)
	{
		FieldTyp field = FieldTyp.INVALID;
		if (pos >= 0 && pos < track.size())
		{
			field = track.get(pos);
		}
		return field;
	}

	/**
	 * Gibt den Spieler an einer bestimmten Position zurück. Sollte das
	 * angegebene Feld außerhalb des Spielfeldes liegen, oder durch keinen
	 * Spieler besetzt sein, wird <code>null</code> zurückgegeben.
	 * 
	 * @param pos
	 *            die Position auf der Rennstrecke
	 * @return
	 */
	public final Player getPlayerAt(final int pos)
	{
		Player player = null;
		for (final Player f : players)
		{
			if (f.getPosition() == pos)
				player = f;
		}
		return player;
	}
}
