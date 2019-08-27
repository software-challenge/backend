package sc.plugin2020.util;

import org.jetbrains.annotations.NotNull;

public class CubeCoordinates implements Comparable {
  public final int x,y,z;

  public CubeCoordinates(int x, int y, int z){
    if (x + y + z != 0)
      throw new IllegalArgumentException("Constraint: (x + y + z == 0) not granted!");
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public CubeCoordinates(int q, int r){
    this.x = q;
    this.y = r;
    this.z = -q-r;
  }

  public CubeCoordinates(CubeCoordinates position) {
    this.x = position.x;
    this.y = position.y;
    this.z = position.z;
  }

  @Override
  public String toString() {
    return String.format("(%d,%d,%d)", this.x, this.y, this.z);
  }

  @Override
  public int compareTo(@NotNull Object o) {
    CubeCoordinates to = (CubeCoordinates)o;
    if (this.x > to.x) return 1;
    if (this.x < to.x) return -1;
    if (this.y > to.y) return 1;
    if (this.y < to.y) return -1;
    if (this.z > to.z) return 1;
    if (this.z < to.z) return -1;
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    CubeCoordinates to = (CubeCoordinates)obj;
    return (this.x == to.x && this.y == to.y && this.z == to.z);
  }
}
