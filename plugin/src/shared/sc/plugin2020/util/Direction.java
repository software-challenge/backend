package sc.plugin2020.util;

public enum Direction {
  RIGHT,
  LEFT,
  UPRIGHT,
  UPLEFT,
  DOWNRIGHT,
  DOWNLEFT;

  public Coord shift() {
    int shiftX = 0;
    int shiftY = 0;
    int shiftZ = 0;
    switch (this) {
      case RIGHT:
        shiftX = 1;
        shiftY = -1;
        break;
      case LEFT:
        shiftX = -1;
        shiftY = 1;
        break;
      case UPRIGHT:
        shiftX = 1;
        shiftZ = -1;
        break;
      case UPLEFT:
        shiftY = 1;
        shiftZ = -1;
        break;
      case DOWNRIGHT:
        shiftY = -1;
        shiftZ = 1;
        break;
      case DOWNLEFT:
        shiftX = -1;
        shiftZ = 1;
        break;
    }
    return new Coord(shiftX, shiftY, shiftZ);
  }
}
