package sc.plugin2017;

import java.util.List;

public class Tile {

  private List<Field> fields;
  
  private boolean visible;

  public Field getField(int x, int y) {
    if(visible == false) {
      return null;
    }
    for(Field field : fields) {
      if(field.getX() == x && field.getY() == y) {
        return field;
      }
    }
    return null;
  }

}
