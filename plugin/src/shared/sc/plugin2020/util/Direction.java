package sc.plugin2020.util;

public enum Direction {
  RIGHT,
  LEFT,
  UPRIGHT,
  UPLEFT,
  DOWNRIGHT,
  DOWNLEFT;

  public Coord shift(int d) { // d is the distance
    int shiftX = 0;
    int shiftY = 0;
    int shiftZ = 0;
    switch (this) {
      case RIGHT:
        shiftX = 1*d;
        shiftY = -1*d;
        break;
      case LEFT:
        shiftX = -1*d;
        shiftY = 1*d;
        break;
      case UPRIGHT:
        shiftX = 1*d;
        shiftZ = -1*d;
        break;
      case UPLEFT:
        shiftY = 1*d;
        shiftZ = -1*d;
        break;
      case DOWNRIGHT:
        shiftY = -1*d;
        shiftZ = 1*d;
        break;
      case DOWNLEFT:
        shiftX = -1*d;
        shiftZ = 1*d;
        break;
    }
    return new Coord(shiftX, shiftY, shiftZ);
  }
}
