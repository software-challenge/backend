package sc.plugin2010;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import sc.plugin2010.util.GameUtil;

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
		 * Die veränderten Spielregeln sehen nur noch die Felder 1,2 vor. 
		 * Die Positionsfelder 3 und 4 wurden in Möhrenfelder umgewandelt,
		 * und (1,5,6) sind jetzt Position-1-Felder.
		 */
		POSITION_1, POSITION_2,
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

	private Board()
	{
		track = new LinkedList<FieldTyp>();
		players = new LinkedList<Player>();
	}

	/**
	 * Erstellt und initialisiert ein Spielbrett
	 * 
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
	private void initialize(final int length)
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

	/**
	 * Ist ein Zug auf diesem Spielbrett möglich?
	 * 
	 * @param move
	 * @param player
	 * @return
	 */
	public final boolean isValid(Move move, Player player)
	{
		boolean valid = true;
		switch (move.getTyp())
		{
			case MOVE:
			{
				int requiredCarrots = GameUtil.calculateCarrots(move.getN());
				valid = valid
						&& (requiredCarrots <= player.getCarrotsAvailable());

				int newPosition = player.getPosition() + move.getN();
				valid = valid && !isOccupied(newPosition);

				// TODO Salatfeld => muss der Spieler noch Salate haben
				// TODO Hasenfeld => muss der Spieler Hasenjoker haben.
				// TODO tests!
				break;
			}
			case EAT:
			{
				FieldTyp currentField = getTypeAt(player.getPosition());
				valid = valid && (currentField.equals(FieldTyp.SALAD));
				valid = valid && (player.getSaladsToEat() < 4);
				// TODO tests!
				break;
			}
			case FALL_BACK:
			{
				int newPosition = getPreviousFieldByTyp(FieldTyp.HEDGEHOG,
						player.getPosition());
				valid = valid && (newPosition != -1);
				valid = valid && !isOccupied(newPosition);
				// TODO tests!
				break;
			}
			case PLAY_CARD:
			{
				// TODO implement
				// TODO tests!
			}
				break;
		}
		return valid;
	}

	public final int getNextFieldByTyp(FieldTyp field, int pos)
	{
		return -1;
	}

	public final int getPreviousFieldByTyp(FieldTyp field, int pos)
	{
		return -1;
	}
}
