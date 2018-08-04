package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Ein Spielbrett bestehend aus 65 Feldern. */
@XStreamAlias(value = "board")
public class Board {
  @XStreamImplicit(itemFieldName = "fields")
  private final List<Field> track;
  
  private Board(List<Field> track) {
    this.track = track;
  }
  
  public Board() {
    this(generateTrack());
  }
  
  /**
   * Nur für Testfälle relevant
   * @param i Testparameter für spezielles Board
   */
  Board(int i) {
    this();
    switch (i) {
      case 0:
        track.set(40, new Field(FieldType.HARE, 40));
        track.set(45, new Field(FieldType.HARE, 45));
        track.set(41, new Field(FieldType.POSITION_2, 41));
        break;
    }
  }
  
  /**
   * Erstellt eine zufällige Rennstrecke.
   * Die Indizes der Salat- und Igelfelder sind immer gleich,
   * nur die Felder zwischen zwei Igelfeldern werden permutiert. Außerdem werden auch die Abschnitte zwischen Start-
   * und Ziel und dem ersten bzw. letzten Igelfeld permutiert.
   */
  private static List<Field> generateTrack() {
    List<Field> track = new ArrayList<>();
    List<Field> segment = new ArrayList<>();
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
    
    for (int i = 0; i < track.size(); i++)
      track.get(i).setIndex(i);
    return track;
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
   */
  @Override
  public Board clone() {
    List<Field> clonedTrack = new ArrayList<>();
    for (Field field : track) {
      clonedTrack.add(field.clone());
    }
    return new Board(clonedTrack);
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
  
  /** gibt eine read-only list aller Felder zurück */
  public List<Field> getTrack() {
    return Collections.unmodifiableList(track);
  }
  
}

