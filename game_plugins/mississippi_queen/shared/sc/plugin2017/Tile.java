package sc.plugin2017;

import java.util.ArrayList;
import java.util.Random;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.plugin2017.util.Configuration;
import sc.plugin2017.util.Constants;
import sc.protocol.LobbyProtocol;

@XStreamAlias(value = "tile")
public class Tile {

  public ArrayList<Field> fields;
  @XStreamOmitField
  private boolean visible;
  
  /**
   * Index des Spielsegments
   */
  @XStreamAsAttribute
  private int index;
  
  /**
   * Richtung in die das Spielsegment zeigt
   */
  @XStreamAsAttribute
  private int direction;
  
  /**
   * Nur fuer den Server relevant
   * generates a new tile
   * @param index index of tile
   * @param direction direction of tile
   * @param x x coordinate of middle
   * @param y y coordinate of middle
   * @param passengers passengers on tile
   * @param blockedFields blocked fields in tile
   * @param specialFields special fields (log, sandbar) on tile
   */
  protected Tile(int index, int direction, int x, int y, int passengers, int blockedFields, int specialFields) {
    this.index = index;
    this.direction = direction;
    this.visible = index < 2; // at the beginning only the first 2 Tiles are visible
    System.out.println("*************** Beginning to generate a new tile " + index + " with passenger " + passengers);
    generateFields(x, y, passengers, blockedFields, specialFields, (index == Constants.NUMBER_OF_TILES - 1));
  }
  
  protected Tile(ArrayList<Field> fields) {
    fields = new ArrayList<Field>(fields);
  }

  private void generateFields(int x, int y, int passengers, int blocked, int special, boolean end) {
    System.out.println("Begin to generate tiles for tile" + index);
    fields = new ArrayList<Field>();
    if(direction == 0) {
      fields.add(new Field(FieldType.WATER, x-2, y+2, 0));
      fields.add(new Field(FieldType.WATER, x-2, y-2, 0));
      fields.add(new Field(FieldType.WATER, x-1, y+2, 1));
      fields.add(new Field(FieldType.WATER, x-1, y+1, 1));
      fields.add(new Field(FieldType.WATER, x-1, y  , 1));
      fields.add(new Field(FieldType.WATER, x-1, y-1, 1));
      fields.add(new Field(FieldType.WATER, x-1, y-2, 1));
      fields.add(new Field(FieldType.WATER, x  , y+2, 2));
      fields.add(new Field(FieldType.WATER, x  , y+1, 2));
      fields.add(new Field(FieldType.WATER, x  , y  , 2));
      fields.add(new Field(FieldType.WATER, x  , y-1, 2));
      fields.add(new Field(FieldType.WATER, x  , y-2, 2));
      fields.add(new Field(FieldType.WATER, x+1, y+1, 3));
      fields.add(new Field(FieldType.WATER, x+1, y  , 3));
      fields.add(new Field(FieldType.WATER, x+1, y-1, 3));
      //last 5 fields
      fields.add(new Field(FieldType.WATER, x+1, y-2, 3));
      fields.add(new Field(FieldType.WATER, x+1, y+2, 3));
      if(end) {
        fields.add(new Field(FieldType.GOAL, x+2, y+1, 4));
        fields.add(new Field(FieldType.GOAL, x+2, y, 4));
        fields.add(new Field(FieldType.GOAL, x+2, y-1, 4));
      } else {
        fields.add(new Field(FieldType.WATER, x+2, y+1, 4));
        fields.add(new Field(FieldType.WATER, x+2, y, 4));
        fields.add(new Field(FieldType.WATER, x+2, y-1, 4));
      }
    } else if(direction == 1) {
      fields.add(new Field(FieldType.WATER, x+1, y-3, 0));
      fields.add(new Field(FieldType.WATER, x-2, y+1, 0));
      fields.add(new Field(FieldType.WATER, x+1, y+2, 1));
      fields.add(new Field(FieldType.WATER, x  , y+2, 1));
      fields.add(new Field(FieldType.WATER, x  , y+1, 1));
      fields.add(new Field(FieldType.WATER, x-1, y+1, 1));
      fields.add(new Field(FieldType.WATER, x-2, y  , 1));
      fields.add(new Field(FieldType.WATER, x+2, y+1, 2));
      fields.add(new Field(FieldType.WATER, x+1, y+1, 2));
      fields.add(new Field(FieldType.WATER, x  , y  , 2));
      fields.add(new Field(FieldType.WATER, x-1, y  , 2));
      fields.add(new Field(FieldType.WATER, x-1, y-1, 2));
      fields.add(new Field(FieldType.WATER, x+1, y  , 3));
      fields.add(new Field(FieldType.WATER, x+1, y-1, 3));
      fields.add(new Field(FieldType.WATER, x  , y-1, 3));
      // last 5 fields
      fields.add(new Field(FieldType.WATER, x-1, y-2, 3));
      fields.add(new Field(FieldType.WATER, x+2, y  , 3));
      if(end) {
        fields.add(new Field(FieldType.GOAL, x+2, y-1, 4));
        fields.add(new Field(FieldType.GOAL, x+1, y-2, 4));
        fields.add(new Field(FieldType.GOAL, x  , y-2, 4));
      } else {
        fields.add(new Field(FieldType.WATER, x+2, y-1, 4));
        fields.add(new Field(FieldType.WATER, x+1, y-2, 4));
        fields.add(new Field(FieldType.WATER, x  , y-2, 4));
      }
    } else if(direction == 2) {
      fields.add(new Field(FieldType.WATER, x+3, y+1, 0));
      fields.add(new Field(FieldType.WATER, x  , y+3, 0));
      fields.add(new Field(FieldType.WATER, x+2, y  , 1));
      fields.add(new Field(FieldType.WATER, x+2, y+1, 1));
      fields.add(new Field(FieldType.WATER, x+1, y+1, 1));
      fields.add(new Field(FieldType.WATER, x  , y+2, 1));
      fields.add(new Field(FieldType.WATER, x-1, y+2, 1));
      fields.add(new Field(FieldType.WATER, x+2, y-1, 2));
      fields.add(new Field(FieldType.WATER, x+1, y  , 2));
      fields.add(new Field(FieldType.WATER, x  , y  , 2));
      fields.add(new Field(FieldType.WATER, x  , y+1, 2));
      fields.add(new Field(FieldType.WATER, x-1, y+1, 2));
      fields.add(new Field(FieldType.WATER, x+1, y-1, 3));
      fields.add(new Field(FieldType.WATER, x  , y-1, 3));
      fields.add(new Field(FieldType.WATER, x-1, y  , 3));
      // last 5 fields
      fields.add(new Field(FieldType.WATER, x-2, y  , 3));
      fields.add(new Field(FieldType.WATER, x+1, y-2, 3));
      if(end) {
        fields.add(new Field(FieldType.GOAL, x  , y-2, 4));
        fields.add(new Field(FieldType.GOAL, x-1, y-2, 4));
        fields.add(new Field(FieldType.GOAL, x-1, y-1, 4));
      } else {
        fields.add(new Field(FieldType.WATER, x  , y-2, 4));
        fields.add(new Field(FieldType.WATER, x-1, y-2, 4));
        fields.add(new Field(FieldType.WATER, x-1, y-1, 4));
      }
    } else if(direction == 3) {
      fields.add(new Field(FieldType.WATER, x+2, y-2, 0));
      fields.add(new Field(FieldType.WATER, x+2, y+2, 0));
      fields.add(new Field(FieldType.WATER, x+1, y-2, 1));
      fields.add(new Field(FieldType.WATER, x+2, y-1, 1));
      fields.add(new Field(FieldType.WATER, x+1, y  , 1));
      fields.add(new Field(FieldType.WATER, x+2, y+1, 1));
      fields.add(new Field(FieldType.WATER, x+1, y+2, 1));
      fields.add(new Field(FieldType.WATER, x  , y-2, 2));
      fields.add(new Field(FieldType.WATER, x+1, y-1, 2));
      fields.add(new Field(FieldType.WATER, x  , y  , 2));
      fields.add(new Field(FieldType.WATER, x+1, y+1, 2));
      fields.add(new Field(FieldType.WATER, x  , y+2, 2));
      fields.add(new Field(FieldType.WATER, x  , y-1, 3));
      fields.add(new Field(FieldType.WATER, x-1, y  , 3));
      fields.add(new Field(FieldType.WATER, x  , y+1, 3));
      // last 5 fields
      fields.add(new Field(FieldType.WATER, x-1, y+2, 3));
      fields.add(new Field(FieldType.WATER, x-1, y-2, 3));
      if(end) {
        fields.add(new Field(FieldType.GOAL, x-1, y-1, 4));
        fields.add(new Field(FieldType.GOAL, x-2, y, 4));
        fields.add(new Field(FieldType.GOAL, x-1, y+1, 4));
      } else {
        fields.add(new Field(FieldType.WATER, x-1, y-1, 4));
        fields.add(new Field(FieldType.WATER, x-2, y, 4));
        fields.add(new Field(FieldType.WATER, x-1, y+1, 4));
      }
    } else  if(direction == 4) {
      fields.add(new Field(FieldType.WATER, x  , y+3, 0));
      fields.add(new Field(FieldType.WATER, x+3, y-1, 0));
      fields.add(new Field(FieldType.WATER, x-1, y-2, 1));
      fields.add(new Field(FieldType.WATER, x  , y-2, 1));
      fields.add(new Field(FieldType.WATER, x+1, y-1, 1));
      fields.add(new Field(FieldType.WATER, x+2, y-1, 1));
      fields.add(new Field(FieldType.WATER, x+2, y  , 1));
      fields.add(new Field(FieldType.WATER, x-1, y-1, 2));
      fields.add(new Field(FieldType.WATER, x  , y-1, 2));
      fields.add(new Field(FieldType.WATER, x  , y  , 2));
      fields.add(new Field(FieldType.WATER, x+1, y  , 2));
      fields.add(new Field(FieldType.WATER, x+2, y+1, 2));
      fields.add(new Field(FieldType.WATER, x-1, y  , 3));
      fields.add(new Field(FieldType.WATER, x  , y+1, 3));
      fields.add(new Field(FieldType.WATER, x+1, y+1, 3));
      // last 5 fields
      fields.add(new Field(FieldType.WATER, x+1, y+2, 3));
      fields.add(new Field(FieldType.WATER, x+2, y  , 3));
      if(end) {
        fields.add(new Field(FieldType.GOAL, x-1, y+1, 4));
        fields.add(new Field(FieldType.GOAL, x-1, y+2, 4));
        fields.add(new Field(FieldType.GOAL, x  , y+2, 4));
      } else {
        fields.add(new Field(FieldType.WATER, x-1, y+1, 4));
        fields.add(new Field(FieldType.WATER, x-1, y+2, 4));
        fields.add(new Field(FieldType.WATER, x  , y+2, 4));
      }
    } else if(direction == 5) {
      fields.add(new Field(FieldType.WATER, x+2, y-1, 0));
      fields.add(new Field(FieldType.WATER, x+1, y+3, 0));
      fields.add(new Field(FieldType.WATER, x+2, y  , 1));
      fields.add(new Field(FieldType.WATER, x-1, y-1, 1));
      fields.add(new Field(FieldType.WATER, x  , y-1, 1));
      fields.add(new Field(FieldType.WATER, x  , y-2, 1));
      fields.add(new Field(FieldType.WATER, x+1, y-2, 1));
      fields.add(new Field(FieldType.WATER, x-1, y+1, 2));
      fields.add(new Field(FieldType.WATER, x-1, y  , 2));
      fields.add(new Field(FieldType.WATER, x  , y  , 2));
      fields.add(new Field(FieldType.WATER, x+1, y-1, 2));
      fields.add(new Field(FieldType.WATER, x+2, y-1, 2));
      fields.add(new Field(FieldType.WATER, x  , y+1, 3));
      fields.add(new Field(FieldType.WATER, x+1, y+1, 3));
      fields.add(new Field(FieldType.WATER, x+1, y  , 3));
      // last 5 fields
      fields.add(new Field(FieldType.WATER, x+2, y  , 3));
      fields.add(new Field(FieldType.WATER, x-1, y+2, 3));
      if(end) {
        fields.add(new Field(FieldType.GOAL, x  , y+2, 4));
        fields.add(new Field(FieldType.GOAL, x+1, y+2, 4));
        fields.add(new Field(FieldType.GOAL, x+2, y+1, 4));
      } else {
        fields.add(new Field(FieldType.WATER, x  , y+2, 4));
        fields.add(new Field(FieldType.WATER, x+1, y+2, 4));
        fields.add(new Field(FieldType.WATER, x+2, y+1, 4));
      }
    }
    System.out.println("Generated fields");
    placeBlocked(blocked);
    System.out.println("Placed islands");
    placeSpecial(special);
    System.out.println("Placed special fields");
    placePassengers(passengers);
    System.out.println("PLaced passengers");
  }

  private void placeBlocked(int blocked) {
    Random rnd = new Random();
    while(blocked != 0) {
      int random = rnd.nextInt(fields.size() - 5); // may not be played on the last 5 fields
      while(fields.get(random).getType() != FieldType.WATER) {
        random = rnd.nextInt(fields.size() - 5);
      }
      fields.get(random).setType(FieldType.BLOCKED);
      --blocked;
    }
  }

  private void placeSpecial(int special) {
    Random rnd = new Random();
    while(special != 0) {
      int random = rnd.nextInt(fields.size() - 5); // may not be played on the last 5 fields
      while(fields.get(random).getType() != FieldType.WATER) {
        random = rnd.nextInt(fields.size() - 5);
      }
      int sandbar = rnd.nextInt(2);
      fields.get(random).setType((sandbar == 1) ? FieldType.SANDBAR : FieldType.LOG);
      --special;
    }    
  }

  private void placePassengers(int passengers) {
    Random rnd = new Random();
    while(passengers != 0) {
      int random = rnd.nextInt(fields.size() - 5); // may not be played on the last 5 fields
      int passengerDirection = rnd.nextInt(6);
      Field start = fields.get(random);
      Field dock = getFieldInDirection(passengerDirection, start);
      while(start.getType() != FieldType.WATER || 
          dock == null ||
          dock.getType() != FieldType.WATER) {
        random = rnd.nextInt(fields.size() - 5);
        passengerDirection = rnd.nextInt(6);
        start = fields.get(random);
        dock = getFieldInDirection(passengerDirection, start);
      }
      FieldType passenger;
      switch (passengerDirection) {
      case 0:
        passenger = FieldType.PASSENGER0;
        break;
      case 1:
        passenger = FieldType.PASSENGER1;
        break;
      case 2:
        passenger = FieldType.PASSENGER2;
        break;
        
      case 3:
        passenger = FieldType.PASSENGER3;
        break;
      case 4:
        passenger = FieldType.PASSENGER4;
        break;

      default: // case 5
        passenger = FieldType.PASSENGER5;
        break;
      }
      fields.get(random).setType(passenger);
      --passengers;
    }
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
  
  /**
   * Gibt das Feld in eine bestimmte Richtung zurück. Gibt null zurück, falls sich das Feld nicht auf dem Tile befindet.
   * @param direction Richtung
   * @param field Startfeld
   * @return Feld in Richtung
   */
  public Field getFieldInDirection(int direction, Field field) {
    int x = field.getX();
    int y = field.getY();
    switch (direction) {
    case 0:
      return getField(x + 1,y);
    case 1:
      return getField((y % 2 == 0) ? x + 1 : x, y - 1);
    case 2:
      return getField((y % 2 == 0) ? x : x - 1, y - 1);
    case 3:
      return getField(x - 1, y);
    case 4:
      return getField((y % 2 == 0) ? x : x - 1, y + 1);
    case 5:
      return getField((y % 2 == 0) ? x + 1 : x, y + 1);  
    default:
      break;
    }
    return null;
  }
}
