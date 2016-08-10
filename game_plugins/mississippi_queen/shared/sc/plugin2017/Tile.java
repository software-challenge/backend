package sc.plugin2017;

import java.util.ArrayList;

public class Tile {

  private ArrayList<Field> fields;
  
  private boolean visible;
  
  private int index;
  
  private int direction;
  
  public Tile(int index, int direction, int x, int y, int passengers) {
    this.index = index;
    this.direction = direction;
    this.visible = false;
    fields = generateFields(x, y, passengers);
    
  }
  
  protected Tile(ArrayList<Field> fields) {
    fields = new ArrayList<Field>(fields);
  }

  private ArrayList<Field> generateFields(int x, int y, int passengers) {
    // TODO Auto-generated method stub
    return null;
  }

  public Field getField(int x, int y) {
    for(Field field : fields) {
      if(field.getX() == x && field.getY() == y) {
        return field;
      }
    }
    return null;
  }
  
  public boolean isVisible() {
    return visible;
  }
  
  protected void setVisibility(boolean visible) {
    this.visible = visible;
  }

  public int getIndex() {
    return index;
  }

  public int getDirection() {
    return direction;
  }
  
  public Tile clone() {
    ArrayList<Field> clonedFields = new ArrayList<Field>();
    for (Field field : fields) {
      Field clonedField = field.clone();
      clonedFields.add(clonedField);
    }
    Tile clone = new Tile(clonedFields); 
    return clone;
  }
  
  public boolean equals(Object o) {
    if(o instanceof Tile) {
      Tile tile = (Tile) o;
      ArrayList<Field> fields1 = tile.fields;
      ArrayList<Field> fields2 = this.fields;
      if(fields1.size() != fields2.size()) {
        return false;
      }
      for(int i = 0; i < fields1.size(); i++) {
        if(!fields1.get(i).equals(fields2.get(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
