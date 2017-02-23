package sc.plugin2017;

public enum Direction {

  RIGHT(0, "rechts"), UP_RIGHT(1, "oben rechts"), UP_LEFT(2, "oben links"), LEFT(3, "links"), DOWN_LEFT(4, "unten links"), DOWN_RIGHT(5, "unten rechts");

  private final int value;
  private final String translation;

  Direction(final int value, final String translation) {
    this.value = value;
    this.translation = translation;
  }

  public int getValue() { return value; }

  private Direction getForValue(int val) {
    for (Direction dir : values()) {
      if (val == dir.getValue()) {
        return dir;
      }
    }
    throw new IllegalArgumentException("no direction for value "+val);
  }

  /**
   * Berechnet die gegenüberliegende Richtung, die durch Drehen um 180 Grad ensteht
   * @return opposite direction
   */
  public Direction getOpposite() {
    return getTurnedDirection(3);
  }

  /**
   * Berechnet die Richtung nach drehen von turn Schritten
   * @param turn Anzahl der zu drehenden Schritte (positive Werte gegen den Uhrzeigersinn, negative Werte im Uhrzeigersinn).
   * @return Richtung, zu der gedreht wird
   */
  public Direction getTurnedDirection(int turn) {
    return getForValue((value + turn + 6) % 6);
  }

  /**
   * Gibt die Anzahl der Drehungen bei einer Drehung von der aktuellen Richtung zu toDir zurück
   * @param toDir Endrichtung
   * @return Anzahl der Drehungen
   */
  public int turnToDir(Direction toDir) {
    int direction = ((value - toDir.getValue()) + 6) % 6;
    if(direction >= 3) {
      direction = 6 - direction;
    } else {
      direction = - direction;
    }
    return direction;
  }

  @Override
  public String toString() {
    return this.translation;
  }
}
