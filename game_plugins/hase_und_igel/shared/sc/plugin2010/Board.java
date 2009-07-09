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
	 * Erstellt ein neues Spielfeld
	 * 
	 * @return
	 */
	protected static Board create()
	{
		Board b = new Board();
		b.initialize();
		return b;
	}

	/**
	 * Erstellt eine zufällige Rennstrecke. Die Positionen der Salat- und
	 * Igelfelder bleiben unverändert - nur die Felder zwischen zwei Igelfeldern
	 * werden permutiert.
	 * Außerdem werden auch die Abschnitte zwischen Start- und Ziel und dem
	 * ersten bzw. letzten Igelfeld permutiert.
	 */
	private void initialize()
	{
		FieldTyp[] t = new FieldTyp[65];
		List<FieldTyp> segment = new LinkedList<FieldTyp>();

		t[0] = FieldTyp.START;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.CARROT, FieldTyp.RABBIT, FieldTyp.CARROT,
				FieldTyp.CARROT, FieldTyp.RABBIT, FieldTyp.POSITION_1,
				FieldTyp.POSITION_2, FieldTyp.CARROT }));
		addRandom(t, segment, 1, 10);
		t[10] = FieldTyp.SALAD;
		t[11] = FieldTyp.HEDGEHOG;
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.CARROT,
				FieldTyp.CARROT, FieldTyp.RABBIT }));
		addRandom(t, segment, 12, 15);
		t[15] = FieldTyp.HEDGEHOG;
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.POSITION_1,
				FieldTyp.POSITION_2, FieldTyp.CARROT }));
		addRandom(t, segment, 16, 19);
		t[19] = FieldTyp.HEDGEHOG;
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.CARROT,
				FieldTyp.CARROT, FieldTyp.POSITION_2 }));
		addRandom(t, segment, 20, 22);
		t[22] = FieldTyp.SALAD;
		t[23] = segment.remove(0);
		t[24] = FieldTyp.HEDGEHOG;
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.CARROT, FieldTyp.CARROT, FieldTyp.CARROT,
				FieldTyp.POSITION_2 }));
		addRandom(t, segment, 25, 30);
		t[30] = FieldTyp.HEDGEHOG;
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.POSITION_1, FieldTyp.CARROT, FieldTyp.RABBIT,
				FieldTyp.POSITION_2, FieldTyp.CARROT }));
		addRandom(t, segment, 31, 37);
		t[37] = FieldTyp.HEDGEHOG;
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.CARROT,
				FieldTyp.RABBIT, FieldTyp.CARROT, FieldTyp.POSITION_2 }));
		addRandom(t, segment, 38, 42);
		t[42] = FieldTyp.SALAD;
		t[43] = FieldTyp.HEDGEHOG;
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.CARROT,
				FieldTyp.CARROT, FieldTyp.RABBIT, FieldTyp.POSITION_2,
				FieldTyp.POSITION_1, FieldTyp.CARROT }));
		addRandom(t, segment, 44, 50);
		t[50] = FieldTyp.HEDGEHOG;
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.CARROT, FieldTyp.POSITION_2, FieldTyp.CARROT,
				FieldTyp.CARROT }));
		addRandom(t, segment, 51, 56);
		t[56] = FieldTyp.HEDGEHOG;
		t[57] = FieldTyp.SALAD;
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.CARROT, FieldTyp.POSITION_1, FieldTyp.CARROT,
				FieldTyp.RABBIT, FieldTyp.CARROT }));
		addRandom(t, segment, 58, 64);
		t[64] = FieldTyp.GOAL;

		track.addAll(Arrays.asList(t));
	}

	private void addRandom(FieldTyp[] a, List<FieldTyp> ll, int l, int r)
	{
		for (int i = l; i < r; i++)
		{
			a[i] = ll.remove((int) (Math.random() * ll.size()));
		}
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
	 * Ist ein Zug auf diesem Spielbrett möglich? Validiert einen Zug unter der
	 * Annahme, das der angegebene Spieler am Zug ist.
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
				valid = valid && move.getN() > 0;
				valid = valid
						&& (requiredCarrots <= player.getCarrotsAvailable());

				int newPosition = player.getPosition() + move.getN();
				valid = valid && !isOccupied(newPosition);
				switch (getTypeAt(newPosition))
				{
					case INVALID:
						valid = false;
						break;
					case SALAD:
						valid = valid && player.getSaladsToEat() > 0;
						break;
					case RABBIT:
						valid = valid && player.getActions().size() > 0;
						break;
					case GOAL:
						int carrotsLeft = player.getCarrotsAvailable()
								- requiredCarrots;
						valid = valid && carrotsLeft <= 10;
						valid = valid && player.getSaladsToEat() == 0;
						break;
				}
				break;
			}
			case EAT:
			{
				FieldTyp currentField = getTypeAt(player.getPosition());
				valid = valid && (currentField.equals(FieldTyp.SALAD));
				valid = valid && (player.getSaladsToEat() > 0);
				break;
			}
			case FALL_BACK:
			{
				int newPosition = getPreviousFieldByTyp(FieldTyp.HEDGEHOG,
						player.getPosition());
				valid = valid && (newPosition != -1);
				valid = valid && !isOccupied(newPosition);
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

	/**
	 * Findet das nächste Spielfeld vom Typ <code>field</code> beginnend an Position
	 * <code>pos</code> auf diesem Spielbrett.
	 * 
	 * @param field
	 * @param pos
	 * @return
	 */
	public final int getNextFieldByTyp(FieldTyp field, int pos)
	{
		int ret = -1;
		for (int i = pos + 1; i < track.size(); i++)
		{
			if (track.get(i).equals(field))
			{
				ret = i;
				break;
			}
		}
		return ret;
	}

	/**
	 * @param field
	 * @param pos
	 * @return
	 */
	public final int getPreviousFieldByTyp(FieldTyp field, int pos)
	{
		int ret = -1;
		for (int i = pos - 1; i >= 0; i--)
		{
			if (track.get(i).equals(field))
			{
				ret = i;
				break;
			}
		}
		return ret;
	}
}
