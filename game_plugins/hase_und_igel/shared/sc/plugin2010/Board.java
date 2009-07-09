package sc.plugin2010;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sc.plugin2010.Player.Action;
import sc.plugin2010.Player.FigureColor;
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
	
	protected Player		red;
	protected Player		blue;

	private Board()
	{
		track = new LinkedList<FieldTyp>();
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
	private final void initialize()
	{
		List<FieldTyp> segment = new LinkedList<FieldTyp>();

		track.add(FieldTyp.START);
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.CARROT, FieldTyp.RABBIT, FieldTyp.CARROT,
				FieldTyp.CARROT, FieldTyp.RABBIT, FieldTyp.POSITION_1,
				FieldTyp.POSITION_2, FieldTyp.CARROT }));
		Collections.shuffle(segment);
		track.addAll(segment); segment.clear();
		track.add(FieldTyp.SALAD);
		track.add(FieldTyp.HEDGEHOG);
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.CARROT,
				FieldTyp.CARROT, FieldTyp.RABBIT }));
		Collections.shuffle(segment);
		track.addAll(segment); segment.clear();
		track.add(FieldTyp.HEDGEHOG);
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.POSITION_1,
				FieldTyp.POSITION_2, FieldTyp.CARROT }));
		Collections.shuffle(segment);
		track.addAll(segment); segment.clear();
		track.add(FieldTyp.HEDGEHOG);
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.CARROT,
				FieldTyp.CARROT, FieldTyp.POSITION_2 }));
		for (int i = 0; i < 2; i++)
		{
			track.add(segment.remove((int) (Math.random() * segment.size())));
		}
		track.add(FieldTyp.SALAD);
		track.add(segment.remove(0));
		track.add(FieldTyp.HEDGEHOG);
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.CARROT, FieldTyp.CARROT, FieldTyp.CARROT,
				FieldTyp.POSITION_2 }));
		Collections.shuffle(segment);
		track.addAll(segment); segment.clear();
		track.add(FieldTyp.HEDGEHOG);
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.POSITION_1, FieldTyp.CARROT, FieldTyp.RABBIT,
				FieldTyp.POSITION_2, FieldTyp.CARROT }));
		Collections.shuffle(segment);
		track.addAll(segment); segment.clear();
		track.add(FieldTyp.HEDGEHOG);
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.CARROT,
				FieldTyp.RABBIT, FieldTyp.CARROT, FieldTyp.POSITION_2 }));
		Collections.shuffle(segment);
		track.addAll(segment); segment.clear();
		track.add(FieldTyp.SALAD);
		track.add(FieldTyp.HEDGEHOG);
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.CARROT,
				FieldTyp.CARROT, FieldTyp.RABBIT, FieldTyp.POSITION_2,
				FieldTyp.POSITION_1, FieldTyp.CARROT }));
		Collections.shuffle(segment);
		track.addAll(segment); segment.clear();
		track.add(FieldTyp.HEDGEHOG);
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.CARROT, FieldTyp.POSITION_2, FieldTyp.CARROT,
				FieldTyp.CARROT }));
		Collections.shuffle(segment);
		track.addAll(segment); segment.clear();
		track.add(FieldTyp.HEDGEHOG);
		track.add(FieldTyp.SALAD);
		assert segment.size() == 0;
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.CARROT, FieldTyp.POSITION_1, FieldTyp.CARROT,
				FieldTyp.RABBIT, FieldTyp.CARROT }));
		Collections.shuffle(segment);
		track.addAll(segment); segment.clear();
		track.add(FieldTyp.GOAL);
	}

	protected final void addPlayer(final Player player)
	{
		if (player.getColor().equals(FigureColor.RED))
			red = player;
		else
			blue = player;
	}

	protected final void setPlayerRed(final Player player)
	{
		red = player;
	}

	protected final void setPlayerBlue(final Player player)
	{
		blue = player;
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
		return red.getPosition() == pos || blue.getPosition() == pos;
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
		if (red.getPosition() == pos)
			player = red;
		if (blue.getPosition() == pos)
			player = blue;
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
			case PLAY_CARD_DROP_20_CARROTS:
			{
				valid = valid && player.ownsCardOfTyp(Action.DROP_20_CARROTS);
				valid = valid && (move.getN() == 0 || move.getN() == 20);
			}
			case PLAY_CARD_TAKE_20_CARROTS:
			{
				valid = valid && player.ownsCardOfTyp(Action.TAKE_20_CARROTS);
				valid = valid && (move.getN() == 0 || move.getN() == 20);
			}
			case PLAY_CARD_EAT_SALAD:
			{
				valid = valid && player.ownsCardOfTyp(Action.EAT_SALAD);
				valid = valid && player.getSaladsToEat() > 0;
				break;
			}
			case PLAY_CARD_FALL_BACK:
			{
				valid = valid && player.ownsCardOfTyp(Action.FALL_BACK);
				valid = valid && isFirst(player);
				final Player o = getOtherPlayer(player);
				valid = valid && o.getPosition() != 0;
				int previousHedgehog = getPreviousFieldByTyp(FieldTyp.HEDGEHOG,
						o.getPosition());
				valid = valid && ((o.getPosition() - previousHedgehog) != 1);
				break;
			}
			case PLAY_CARD_HURRY_AHEAD:
			{
				valid = valid && player.ownsCardOfTyp(Action.HURRY_AHEAD);
				valid = valid && !isFirst(player);
				final Player o = getOtherPlayer(player);
				valid = valid && o.getPosition() != 64;
				int nextHedgehog = getNextFieldByTyp(FieldTyp.HEDGEHOG, o
						.getPosition());
				valid = valid && ((nextHedgehog - o.getPosition()) != 1);

				if (o.getPosition() == 63)
				{
					valid = valid && canEnterGoal(player);
				}
			}
				break;
			default:
				valid = false;
				break;
		}
		return valid;
	}

	/**
	 * Findet das nächste Spielfeld vom Typ <code>field</code> beginnend an
	 * Position <code>pos</code> auf diesem Spielbrett.
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

	/**
	 * Überprüft ob der angegebene Spieler an erster Stelle ist
	 * 
	 * @param player
	 * @return
	 */
	public final boolean isFirst(final Player player)
	{
		return getOtherPlayer(player).getPosition() < player.getPosition();
	}

	public final boolean canEnterGoal(final Player player)
	{
		return player.getCarrotsAvailable() <= 10
				&& player.getSaladsToEat() == 0;
	}

	protected final Player getOtherPlayer(final Player player)
	{
		assert blue != null;
		assert red != null;
		return player.getColor().equals(FigureColor.RED) ? blue : red;
	}
}
