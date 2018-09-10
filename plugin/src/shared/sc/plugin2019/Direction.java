package sc.plugin2019;

import sc.plugin2019.util.Point;

/** Bewegungsrichtung für Move */
public enum Direction {
  UP,
  UP_RIGHT,
  RIGHT,
  DOWN_RIGHT,
  DOWN,
  DOWN_LEFT,
  LEFT,
  UP_LEFT;

  public Point shift() {
    int shiftX = 0;
    int shiftY = 0;
    switch (this) {
      case UP_RIGHT:
        shiftX = 1;
      case UP:
        shiftY = 1;
        break;
      case DOWN_RIGHT:
        shiftY = -1;
      case RIGHT:
        shiftX = 1;
        break;
      case DOWN_LEFT:
        shiftX = -1;
      case DOWN:
        shiftY = -1;
        break;
      case UP_LEFT:
        shiftY = 1;
      case LEFT:
        shiftX = -1;
        break;
    }
    return new Point(shiftX, shiftY);
  }

}
