package sc.plugin2019;

public class Separator {
  private Direction direction;

  private int x;

  private int y;

  public Separator(Direction direction, int x, int y) {
    if (direction != Direction.TOP && direction != Direction.RIGHT) {
      throw new IllegalArgumentException("Direction has to be top or right");
    }
    this.x = x;
    this.y = y;
    this.direction = direction;
  }

  public Direction getDirection() {
    return direction;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
}
