package sc.plugin2018;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Ein Spielbrett bestehend aus 65 Feldern.
 */
@XStreamAlias(value = "board")
public class Board {
  @XStreamImplicit(itemFieldName = "fields")
  private List<Field> track;

  public Board() {
    track = new LinkedList<>();
    initialize();
  }

  /**
   * Erstellt eine zufällige Rennstrecke. Die Indizes der Salat- und
   * Igelfelder bleiben unverändert - nur die Felder zwischen zwei Igelfeldern
   * werden permutiert. Außerdem werden auch die Abschnitte zwischen Start-
   * und Ziel und dem ersten bzw. letzten Igelfeld permutiert.
   */
  private void initialize() {
    List<Field> segment = new LinkedList<>();

    track.add(new Field(FieldType.START));
    segment.addAll(Arrays.asList(new Field(FieldType.HARE),
            new Field(FieldType.CARROT), new Field(FieldType.HARE), new Field(FieldType.CARROT),
            new Field(FieldType.CARROT), new Field(FieldType.HARE), new Field(FieldType.POSITION_1),
            new Field(FieldType.POSITION_2), new Field(FieldType.CARROT)));
    Collections.shuffle(segment);
    track.addAll(segment);
    segment.clear();
    track.add(new Field(FieldType.SALAD));
    track.add(new Field(FieldType.HEDGEHOG));
    segment.addAll(Arrays.asList(new Field(FieldType.CARROT),
            new Field(FieldType.CARROT), new Field(FieldType.HARE)));
    Collections.shuffle(segment);
    track.addAll(segment);
    segment.clear();
    track.add(new Field(FieldType.HEDGEHOG));
    segment.addAll(Arrays.asList(new Field(FieldType.POSITION_1),
            new Field(FieldType.POSITION_2), new Field(FieldType.CARROT)));
    Collections.shuffle(segment);
    track.addAll(segment);
    segment.clear();
    track.add(new Field(FieldType.HEDGEHOG));
    segment.addAll(Arrays.asList(new Field(FieldType.CARROT),
            new Field(FieldType.CARROT), new Field(FieldType.POSITION_2)));
    Collections.shuffle(segment);
    track.add(segment.remove(0));
    track.add(segment.remove(0));
    track.add(new Field(FieldType.SALAD));
    track.add(segment.remove(0));
    track.add(new Field(FieldType.HEDGEHOG));
    segment.addAll(Arrays.asList(new Field(FieldType.HARE),
            new Field(FieldType.CARROT), new Field(FieldType.CARROT), new Field(FieldType.CARROT),
            new Field(FieldType.POSITION_2)));
    Collections.shuffle(segment);
    track.addAll(segment);
    segment.clear();
    track.add(new Field(FieldType.HEDGEHOG));
    segment.addAll(Arrays.asList(new Field(FieldType.HARE),
            new Field(FieldType.POSITION_1), new Field(FieldType.CARROT), new Field(FieldType.HARE),
            new Field(FieldType.POSITION_2), new Field(FieldType.CARROT)));
    Collections.shuffle(segment);
    track.addAll(segment);
    segment.clear();
    track.add(new Field(FieldType.HEDGEHOG));
    segment.addAll(Arrays.asList(new Field(FieldType.CARROT),
            new Field(FieldType.HARE), new Field(FieldType.CARROT), new Field(FieldType.POSITION_2)));
    Collections.shuffle(segment);
    track.addAll(segment);
    segment.clear();
    track.add(new Field(FieldType.SALAD));
    track.add(new Field(FieldType.HEDGEHOG));
    segment.addAll(Arrays.asList(new Field(FieldType.CARROT),
            new Field(FieldType.CARROT), new Field(FieldType.HARE), new Field(FieldType.POSITION_2),
            new Field(FieldType.POSITION_1), new Field(FieldType.CARROT)));
    Collections.shuffle(segment);
    track.addAll(segment);
    segment.clear();
    track.add(new Field(FieldType.HEDGEHOG));
    segment.addAll(Arrays.asList(new Field(FieldType.HARE),
            new Field(FieldType.CARROT), new Field(FieldType.POSITION_2), new Field(FieldType.CARROT),
            new Field(FieldType.CARROT)));
    Collections.shuffle(segment);
    track.addAll(segment);
    segment.clear();
    track.add(new Field(FieldType.HEDGEHOG));
    track.add(new Field(FieldType.SALAD));
    segment.addAll(Arrays.asList(new Field(FieldType.HARE),
            new Field(FieldType.CARROT), new Field(FieldType.POSITION_1), new Field(FieldType.CARROT),
            new Field(FieldType.HARE), new Field(FieldType.CARROT)));
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
   * Gibt den Feldtypen an einem bestimmten Index zurück. Liegt der
   * gewählte Index vor dem Startpunkt oder hinter dem Ziel, so wird
   * <code>INVALID</code> zurückgegeben.
   *
   * @param index die Index auf der Rennstrecke
   *
   * @return Feldtyp an Index
   */
  public final FieldType getTypeAt(final int index) {
    FieldType field = FieldType.INVALID;
    if (index >= 0 && index < this.track.size())
      field = this.track.get(index).getType();
    return field;
  }

  /**
   * Findet das nächste Spielfeld vom Typ <code>type</code> beginnend an
   * Index <code>index</code> auf diesem Spielbrett.
   *
   * @param type  Feldtyp
   * @param index Index
   *
   * @return Index des nächsten Feldes genannten Typs
   */
  public final int getNextFieldByType(FieldType type, int index) {
    int ret = -1;
    for (int i = index + 1; i < this.track.size(); i++)
      if (this.track.get(i).getType().equals(type)) {
        ret = i;
        break;
      }
    return ret;
  }

  /**
   * Findet das vorherige Spielfeld vom Typ <code>type</code> beginnend an Index
   * <code>index</code> auf diesem Spielbrett.
   *
   * @param type  Feldtyp
   * @param index Index
   *
   * @return Index des vorherigen Feldes genannten Typs
   */
  public final int getPreviousFieldByType(FieldType type, int index) {
    int ret = -1;
    for (int i = index - 1; i >= 0; i--)
      if (track.get(i).getType().equals(type)) {
        ret = i;
        break;
      }
    return ret;
  }

  /**
   * erzeugt eine Deepcopy dieses Objekts
   *
   * @return ein neues Objekt mit gleichen Eigenschaften
   *
   * @throws CloneNotSupportedException falls klonen fehlschlaegt
   */
  @Override
  public Board clone() throws CloneNotSupportedException {
    Board clone = new Board();
    clone.track.clear();
    for (Field field : this.track) {
      clone.track.add(field.clone());
    }
    return clone;
  }

  @Override
  public String toString() {
    String toString = "Board:\n";
    StringBuilder b = new StringBuilder(toString);
    for (Field field : track) {
      b.append(field.getType());
      b.append(" index ");
      b.append(field.getIndex());
      b.append("\n");
    }
    return b.toString();
  }
}

