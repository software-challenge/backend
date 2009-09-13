package sc.plugin2010;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author rra
 * @since Jul 1, 2009
 * 
 */
@XStreamAlias(value = "hui:board")
public class Board
{
	@XStreamImplicit(itemFieldName = "field")
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
	 * werden permutiert. Außerdem werden auch die Abschnitte zwischen Start-
	 * und Ziel und dem ersten bzw. letzten Igelfeld permutiert.
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
		track.addAll(segment);
		segment.clear();
		track.add(FieldTyp.SALAD);
		track.add(FieldTyp.HEDGEHOG);
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.CARROT,
				FieldTyp.CARROT, FieldTyp.RABBIT }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(FieldTyp.HEDGEHOG);
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.POSITION_1,
				FieldTyp.POSITION_2, FieldTyp.CARROT }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(FieldTyp.HEDGEHOG);
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.CARROT,
				FieldTyp.CARROT, FieldTyp.POSITION_2 }));
		Collections.shuffle(segment);
		track.add(segment.remove(0));
		track.add(segment.remove(0));
		track.add(FieldTyp.SALAD);
		track.add(segment.remove(0));
		track.add(FieldTyp.HEDGEHOG);
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.CARROT, FieldTyp.CARROT, FieldTyp.CARROT,
				FieldTyp.POSITION_2 }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(FieldTyp.HEDGEHOG);
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.POSITION_1, FieldTyp.CARROT, FieldTyp.RABBIT,
				FieldTyp.POSITION_2, FieldTyp.CARROT }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(FieldTyp.HEDGEHOG);
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.CARROT,
				FieldTyp.RABBIT, FieldTyp.CARROT, FieldTyp.POSITION_2 }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(FieldTyp.SALAD);
		track.add(FieldTyp.HEDGEHOG);
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.CARROT,
				FieldTyp.CARROT, FieldTyp.RABBIT, FieldTyp.POSITION_2,
				FieldTyp.POSITION_1, FieldTyp.CARROT }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(FieldTyp.HEDGEHOG);
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.CARROT, FieldTyp.POSITION_2, FieldTyp.CARROT,
				FieldTyp.CARROT }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(FieldTyp.HEDGEHOG);
		track.add(FieldTyp.SALAD);
		segment.addAll(Arrays.asList(new FieldTyp[] { FieldTyp.RABBIT,
				FieldTyp.CARROT, FieldTyp.POSITION_1, FieldTyp.CARROT,
				FieldTyp.RABBIT, FieldTyp.CARROT }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
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
		return (red.getFieldNumber() == pos || blue.getFieldNumber() == pos)
				&& (pos != 64 || pos == 0);
	}

	public final int nextFreeFieldFor(Player player, int off)
	{
		int offset = off;
		Move m = new Move(MoveTyp.MOVE, player.getFieldNumber() + offset);
		while(isValid(m, player)) {
			offset++;
			m = new Move(MoveTyp.MOVE, player.getFieldNumber() + offset);
		}
		return offset;
	}

	public final int nextFreeFieldFor(Player player)
	{
		return nextFreeFieldFor(player, 1);
	}

	/**
	 * Überprüft ob man auf ein Feld mit <code>MoveTyp.MOVE</code> ziehen darf.
	 * !!DEPRECATED!!
	 * use <code>isValid</code> instead
	 * @param pos
	 * @return
	 */
	@Deprecated
	public final boolean isMoveable(final int pos, Player p)
	{
		boolean moveable = !isOccupied(pos);
		final FieldTyp ft = getTypeAt(pos);
		switch (ft)
		{
			case HEDGEHOG:
				moveable = false;
				break;
			case RABBIT:
				moveable = moveable && p.getActions().size() > 0;
				break;
			case SALAD:
				moveable = moveable && p.getSaladsToEat() > 0;
				break;
			case GOAL:
				moveable = moveable && p.getSaladsToEat() == 0
						&& p.getCarrotsAvailable() <= 10;
				break;
		}
		return moveable;
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
			field = track.get(pos);
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
		if (red.getFieldNumber() == pos)
			player = red;
		else if (blue.getFieldNumber() == pos)
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
				valid = GameUtil.isValidToMove(this, player, move.getN());
				valid = valid && !player.mustPlayCard();
				break;
			case EAT:
				valid = GameUtil.isValidToEat(this, player);
				valid = valid && !player.mustPlayCard();
				break;
			case TAKE_OR_DROP_CARROTS:
				valid = GameUtil.isValidToTakeOrDrop10Carrots(this, player,
						move.getN());
				valid = valid && !player.mustPlayCard();
				break;
			case FALL_BACK:
				valid = GameUtil.isValidToFallBack(this, player);
				valid = valid && !player.mustPlayCard();
				break;
			case PLAY_CARD:
				valid = GameUtil.isValidToPlayCard(this, player,
						move.getCard(), move.getN());
				break;
			case SKIP:
				valid = !GameUtil.isValidToFallBack(this, player) || 
					!GameUtil.canPlayCard(this, player) ||
					 !GameUtil.canMove(this, player);
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
			if (track.get(i).equals(field))
			{
				ret = i;
				break;
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
			if (track.get(i).equals(field))
			{
				ret = i;
				break;
			}
		return ret;
	}

	/**
	 * Überprüft ob der angegebene Spieler an erster Stelle ist. Wenn sich beide
	 * Spieler im Ziel befinden wird zusätzlich überprüft, ob <code>p</code>
	 * weniger Karotten besitzt als der Gegenspieler.
	 * 
	 * @param player
	 * @return
	 */
	public final boolean isFirst(final Player p)
	{
		Player o = getOtherPlayer(p);
		boolean isFirst = o.getFieldNumber() <= p.getFieldNumber();
		if (p.inGoal() && o.getFieldNumber() == p.getFieldNumber())
			isFirst = isFirst
					&& p.getCarrotsAvailable() < o.getCarrotsAvailable();
		return isFirst;
	}

	public final boolean canEnterGoal(final Player player)
	{
		return player.getCarrotsAvailable() <= 10
				&& player.getSaladsToEat() == 0;
	}

	public final Player getOtherPlayer(final Player player)
	{
		assert blue != null;
		assert red != null;
		return player.getColor().equals(FigureColor.RED) ? blue : red;
	}
}
