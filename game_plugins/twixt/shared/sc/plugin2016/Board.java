package sc.plugin2016;

import java.awt.geom.Line2D;

import sc.plugin2016.GameState;
import sc.plugin2016.Player;
import sc.plugin2016.util.Constants;
import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;

public class Board {

  public Field[][] fields;
  
  public HashBasedTable<Field, Field, PlayerColor> connections;
//  private HashBasedTable<Field, Field, PlayerColor> possibleConnections;

  /**
	 * 
	 */
  public Board() {
    this.init();
  }

  /**
   * 
   * @param init
   */
  public Board(Boolean init) {
    if (init)
      this.init();
    else
      this.makeClearBoard();
  }

  /**
	 * 
	 */
  private void init() {
    fields = new Field[Constants.SIZE][Constants.SIZE];
    fields[0][0] = new Field(FieldType.SWAMP, 0, 0);
    fields[0][Constants.SIZE - 1] = new Field(FieldType.SWAMP, 0, Constants.SIZE - 1);
    fields[Constants.SIZE - 1][0] = new Field(FieldType.SWAMP, Constants.SIZE - 1, 0);
    fields[Constants.SIZE - 1][Constants.SIZE - 1] = new Field(FieldType.SWAMP, Constants.SIZE - 1, Constants.SIZE - 1);
    for (int x = 1; x < Constants.SIZE - 1; x++) {
      fields[x][0] = new Field(FieldType.RED, x, 0);
      fields[x][Constants.SIZE - 1] = new Field(FieldType.RED, x, Constants.SIZE - 1);
    }
    for (int y = 1; y < Constants.SIZE - 1; y++) {
      fields[0][y] = new Field(FieldType.BLUE, 0, y);
      fields[Constants.SIZE - 1][y] = new Field(FieldType.BLUE, Constants.SIZE - 1, y);
    }
    // TODO füge Sümpfe ein
    for (int x = 1; x < Constants.SIZE - 1; x++) {
      for (int y = 1; y < Constants.SIZE - 1; y++) {
        fields[x][y] = new Field(FieldType.NORMAL, x, y);
      }
    }
    connections = HashBasedTable.create();
  }

  private void makeClearBoard() {
    fields = new Field[Constants.SIZE][Constants.SIZE];
  }

  public Field getField(int x, int y) {
    return fields[x][y];
  }

  public Player getOwner(int x, int y) {
    return fields[x][y].getOwner();
  }

  public boolean equals(Object o) {
    if (o instanceof Board) {
      Board b = (Board) o;
      for (int x = 0; x < Constants.SIZE; x++) {
        for (int y = 0; y < Constants.SIZE; y++) {
          if (!this.fields[x][y].equals(b.fields[x][y])) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
	 * 
	 */
  public Object clone() {
    Board clone = new Board(false);
    for (int x = 0; x < Constants.SIZE; x++) {
      for (int y = 0; y < Constants.SIZE; y++) {
        clone.fields[x][y] = this.fields[x][y].clone();
      }
    }
    return clone;
  }

  /**
   * Setzt einen Strommast auf das Spielfeld. Diese Methode ist nur für den
   * Server relevant, da hier keine Fehlerüberprüfung durchgeführt wird. Zum
   * Ausführen von Zügen die
   * {@link sc.plugin2016.Move#perform(GameState, Player) perform}-Methode
   * benutzen.
   * 
   * @param x
   * @param y
   *          das Feld, auf das gesetzt wird
   * @param playerColor
   *          die Farbe des Besitzers
   */
  public void put(int x, int y, Player player) {
    getField(x, y).setOwner(player);
    createNewWires(x, y);
  }

  private void createNewWires(int x, int y) {
    if (checkPossibleWire(x, y, x - 2, y - 1)) {
      createWire(x, y, x - 2, y - 1);
    }
    if (checkPossibleWire(x, y, x - 1, y - 2)) {
      createWire(x, y, x - 1, y - 2);
    }
    if (checkPossibleWire(x, y, x - 2, y + 1)) {
      createWire(x, y, x - 2, +1);
    }
    if (checkPossibleWire(x, y, x - 1, y + 2)) {
      createWire(x, y, x - 1, y + 2);
    }
    if (checkPossibleWire(x, y, x + 2, y - 1)) {
      createWire(x, y, x + 2, y - 1);
    }
    if (checkPossibleWire(x, y, x + 1, y - 2)) {
      createWire(x, y, x + 1, y - 2);
    }
    if (checkPossibleWire(x, y, x + 2, y + 1)) {
      createWire(x, y, x + 2, y + 1);
    }
    if (checkPossibleWire(x, y, x + 1, y + 2)) {
      createWire(x, y, x + 1, y + 2);
    }

  }

  private void createWire(int x1, int y1, int x2, int y2) {
    connections.put(getField(x1, y1), getField(x2, y2), getField(x1, y1).getOwner().getPlayerColor());
//    getField(x1, y1).addConnection(getField(x2, y2));
//    getField(x2, y2).addConnection(getField(x1, y1));

  }

  private boolean checkPossibleWire(int x1, int y1, int x2, int y2) {
    if (x2 < Constants.SIZE && y2 < Constants.SIZE && x2 >= 0 && y2 >= 0) {
      if (getField(x2, y2).getOwner() == getField(x1, y1).getOwner()) {
        return !existsBlockingWire(x1, y1, x2, y2);
      }
    }
    return false;
  }

  private boolean existsBlockingWire(int x1, int y1, int x2, int y2) {
    int biggerX = Math.max(x1, x2);
    int smallerX = Math.min(x1, x2);
    int biggerY = Math.max(y1, y2);
    int smallerY = Math.min(y1, y2);
    for (int x = smallerX; x <= biggerX; x++) {
      for (int y = smallerY; y <= biggerY; y++) { // checks all 6 Fields, from
                                                  // where there could be
                                                  // blocking connections
        if (getField(x, y).getOwner() != null && (x != x1 && y != y1)
            && (x != x2 && y != y2)) { // excludes the Fields with no owner and
                                       // the fields (x1, y2), (x2, y2)
                                       // themselves.
          if(isWireBlocked(x1, y1, x2, y2, x, y)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /*
   * checks for the wire (x1, y1) -> (x2, y2), if it is blocked by any connection going out from (x,y).
   */
  private boolean isWireBlocked(int x1, int y1, int x2, int y2, int x, int y) {
//    for(Field field : getField(x, y).getConnections()) {
//      if(Line2D.linesIntersect(x1, y1, x2, y2, x, y, field.getX(), field.getY())) {
//        return true;
//      }
//    }
//    return false;  
//  }
    for(Field field : connections.column(getField(x, y)).keySet()) {
      if(Line2D.linesIntersect(x1, y1, x2, y2, x, y, field.getX(), field.getY())) {
        return true;
      }
    }
    for(Field field : connections.row(getField(x, y)).keySet()) {
      if(Line2D.linesIntersect(x1, y1, x2, y2, x, y, field.getX(), field.getY())) {
        return true;
      }
    }
    return false;
  }
}
