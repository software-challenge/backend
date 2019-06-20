package sc.plugin2020.util;

public class Coord {
  public final int x,y,z;

  public Coord (int x, int y, int z){
    if (x + y + z != 0)
      throw new IllegalArgumentException("Constraint: (x + y + z == 0) not granted!");
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Coord (int q, int r){
    this.x = q;
    this.y = r;
    this.z = -q-r;
  }

}
