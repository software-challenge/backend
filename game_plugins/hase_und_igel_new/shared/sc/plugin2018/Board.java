package sc.plugin2018;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import sc.plugin2018.util.GameUtil;

/**
 * @author rra
 * @since Jul 1, 2009
 * 
 */
@XStreamAlias(value = "board")
public class Board
{
	@XStreamImplicit(itemFieldName = "fields")
	private List<Field>	track;

	public Board()
	{
		track = new LinkedList<Field>();
		initialize();
	}

	/**
	 * Erstellt eine zufällige Rennstrecke. Die Positionen der Salat- und
	 * Igelfelder bleiben unverändert - nur die Felder zwischen zwei Igelfeldern
	 * werden permutiert. Außerdem werden auch die Abschnitte zwischen Start-
	 * und Ziel und dem ersten bzw. letzten Igelfeld permutiert.
	 */
	private final void initialize()
	{
		List<Field> segment = new LinkedList<Field>();

		track.add(new Field(FieldType.START));
		segment.addAll(Arrays.asList(new Field[] { new Field(FieldType.RABBIT),
				new Field(FieldType.CARROT), new Field(FieldType.RABBIT), new Field(FieldType.CARROT),
				new Field(FieldType.CARROT), new Field(FieldType.RABBIT), new Field(FieldType.POSITION_1),
				new Field(FieldType.POSITION_2), new Field(FieldType.CARROT) }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(new Field(FieldType.SALAD));
		track.add(new Field(FieldType.HEDGEHOG));
		segment.addAll(Arrays.asList(new Field[] { new Field(FieldType.CARROT),
				new Field(FieldType.CARROT), new Field(FieldType.RABBIT) }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(new Field(FieldType.HEDGEHOG));
		segment.addAll(Arrays.asList(new Field[] { new Field(FieldType.POSITION_1),
				new Field(FieldType.POSITION_2), new Field(FieldType.CARROT) }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(new Field(FieldType.HEDGEHOG));
		segment.addAll(Arrays.asList(new Field[] { new Field(FieldType.CARROT),
				new Field(FieldType.CARROT), new Field(FieldType.POSITION_2) }));
		Collections.shuffle(segment);
		track.add(segment.remove(0));
		track.add(segment.remove(0));
		track.add(new Field(FieldType.SALAD));
		track.add(segment.remove(0));
		track.add(new Field(FieldType.HEDGEHOG));
		segment.addAll(Arrays.asList(new Field[] { new Field(FieldType.RABBIT),
				new Field(FieldType.CARROT), new Field(FieldType.CARROT), new Field(FieldType.CARROT),
				new Field(FieldType.POSITION_2) }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(new Field(FieldType.HEDGEHOG));
		segment.addAll(Arrays.asList(new Field[] { new Field(FieldType.RABBIT),
				new Field(FieldType.POSITION_1), new Field(FieldType.CARROT), new Field(FieldType.RABBIT),
				new Field(FieldType.POSITION_2), new Field(FieldType.CARROT) }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(new Field(FieldType.HEDGEHOG));
		segment.addAll(Arrays.asList(new Field[] { new Field(FieldType.CARROT),
				new Field(FieldType.RABBIT), new Field(FieldType.CARROT), new Field(FieldType.POSITION_2) }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(new Field(FieldType.SALAD));
		track.add(new Field(FieldType.HEDGEHOG));
		segment.addAll(Arrays.asList(new Field[] { new Field(FieldType.CARROT),
				new Field(FieldType.CARROT), new Field(FieldType.RABBIT), new Field(FieldType.POSITION_2),
				new Field(FieldType.POSITION_1), new Field(FieldType.CARROT) }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(new Field(FieldType.HEDGEHOG));
		segment.addAll(Arrays.asList(new Field[] { new Field(FieldType.RABBIT),
				new Field(FieldType.CARROT), new Field(FieldType.POSITION_2), new Field(FieldType.CARROT),
				new Field(FieldType.CARROT) }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(new Field(FieldType.HEDGEHOG));
		track.add(new Field(FieldType.SALAD));
		segment.addAll(Arrays.asList(new Field[] { new Field(FieldType.RABBIT),
				new Field(FieldType.CARROT), new Field(FieldType.POSITION_1), new Field(FieldType.CARROT),
				new Field(FieldType.RABBIT), new Field(FieldType.CARROT) }));
		Collections.shuffle(segment);
		track.addAll(segment);
		segment.clear();
		track.add(new Field(FieldType.GOAL));
		int i = 0;
		for (Field field : segment) {
      field.setIndex(i);
      i++;
    }
	}

	public final int nextFreeFieldFor(Player player, int off)
	{
		int offset = off;
		Move m = new Move(MoveTyp.MOVE, player.getFieldIndex() + offset);
		while(isValid(m, player)) {
			offset++;
			m = new Move(MoveTyp.MOVE, player.getFieldIndex() + offset);
		}
		return offset;
	}

	public final int nextFreeFieldFor(Player player)
	{
		return nextFreeFieldFor(player, 1);
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
	public final FieldType getTypeAt(final int pos)
	{
		FieldType field = FieldType.INVALID;
		if (pos >= 0 && pos < track.size())
			field = track.get(pos).getType();
		return field;
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
		switch (move.getType())
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
				//valid = !GameUtil.isValidToFallBack(this, player) && 
					//!GameUtil.canPlayCard(this, player) &&
					 //!GameUtil.canMove(this, player);
				valid = GameUtil.isValidToSkip(this, player);
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
	public final int getNextFieldByTyp(FieldType field, int pos)
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
	public final int getPreviousFieldByTyp(FieldType field, int pos)
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

	public final boolean canEnterGoal(final Player player)
	{
		return player.getCarrotsAvailable() <= 10
				&& player.getSaladsToEat() == 0;
	}
	
  /**
   * erzeugt eine Deepcopy dieses Objekts
   *
   * @return ein neues Objekt mit gleichen Eigenschaften
   * @throws CloneNotSupportedException falls klonen fehlschlaegt
   */
  @Override
  public Board clone() throws CloneNotSupportedException {
    Board clone = (Board) super.clone();
    for (Field field : this.track) {
      clone.track.add((Field) field.clone());
    }
    return clone;
  }
}

