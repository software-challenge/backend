package sc.plugin2018;

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
		for (Field field : track) {
      field.setIndex(i);
      i++;
    }
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
		if (pos >= 0 && pos < this.track.size())
			field = this.track.get(pos).getType();
		return field;
	}
	
 /**
  * Findet das nächste Spielfeld vom Typ <code>type</code> beginnend an
  * Position <code>pos</code> auf diesem Spielbrett.
  * 
  * @param type
  * @param pos
  * @return
  */
 public final int getNextFieldByType(FieldType type, int pos)
 {
   int ret = -1;
   for (int i = pos + 1; i < this.track.size(); i++)
     if (this.track.get(i).getType().equals(type))
     {
       ret = i;
       break;
     }
   return ret;
 }

 /**
  * @param type
  * @param pos
  * @return
  */
 public final int getPreviousFieldByType(FieldType type, int pos)
 {
   int ret = -1;
   for (int i = pos - 1; i >= 0; i--)
     if (track.get(i).getType().equals(type))
     {
       ret = i;
       break;
     }
   return ret;
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

