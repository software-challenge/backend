package sc.plugin2016;

import java.awt.geom.Line2D;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import sc.plugin2016.util.BoardConverter;
import sc.plugin2016.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;


@XStreamAlias(value = "board")
public class Board {

	/**
	 * Ein zweidimensionales Array mit den Feldern des Spielfeldes.
	 */
  @XStreamConverter(BoardConverter.class)
  private Field[][] fields;
  
  
  /**
   * Eine Liste der bestehenden Verbindungen, siehe {@link Connection}
   */
  @XStreamAlias(value = "connections")
  public List<Connection> connections;
  

  /**
	 * Erzeug ein neues Spielfeld
	 */
  public Board() {
    this.init();
  }

  /**
   * Erzeug ein neues Spielfeld
   * @param init boolean der entscheidet, ob das Spielfeld initialisiert werden soll
   */
  public Board(Boolean init) {
    if (init)
      this.init();
    else
      this.makeClearBoard();
  }

  /**
   * Nur fuer den Server relevant
   * initializes the board
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
    for (int x = 1; x < Constants.SIZE - 1; x++) {
      for (int y = 1; y < Constants.SIZE - 1; y++) {
        fields[x][y] = new Field(FieldType.NORMAL, x, y);
      }
    }
    placeSwamps();
    connections = new ArrayList<Connection>();
  }

  /**
   * Nur fuer den Server relevant
   * places swamps randomly around the board
   */
  private void placeSwamps() {
    SecureRandom rand = new SecureRandom();
    int x, y;
    // big swamp
    x = 1 + rand.nextInt(Constants.SIZE - 4);
    y = 1 + rand.nextInt(Constants.SIZE - 4);
    for(int i = x; i <= x + 2; i++) {
      for(int j = y; j <= y + 2; j++) {
        fields[i][j].setType(FieldType.SWAMP);
      }
    }
    // first medium swamp
    x = 1 + rand.nextInt(Constants.SIZE - 3);
    y = 1 + rand.nextInt(Constants.SIZE - 3);
    for(int i = x; i <= x + 1; i++) {
      for(int j = y; j <= y + 1; j++) {
        fields[i][j].setType(FieldType.SWAMP);
      }
    }
    // second medium swamp
    x = 1 + rand.nextInt(Constants.SIZE - 3);
    y = 1 + rand.nextInt(Constants.SIZE - 3);
    for(int i = x; i <= x + 1; i++) {
      for(int j = y; j <= y + 1; j++) {
        fields[i][j].setType(FieldType.SWAMP);
      }
    }
    // little swamp
    x = 1 + rand.nextInt(Constants.SIZE - 2);
    y = 1 + rand.nextInt(Constants.SIZE - 2);
    fields[x][y].setType(FieldType.SWAMP);
  }

  /**
   * Nur fuer den Server relevant
   * creates a new clear board
   */
  private void makeClearBoard() {
    fields = new Field[Constants.SIZE][Constants.SIZE];
    connections = new ArrayList<Connection>();
  }

  /**
   * Gibt ein Feld zurück
   * @param x x-Koordinate
   * @param y y-Koordinate
   * @return Feld an entsprechenden Koordinaten
   */
  public Field getField(int x, int y) {
    return fields[x][y];
  }

  
  /**
   * Gibt den Besitzer eines Spielfelds zurück
   * @param x x-Koordinate
   * @param y y-Koordinate
   * @return Besitzer des Feldes an entsprechenden Koordinaten
   */
  public PlayerColor getOwner(int x, int y) {
    return fields[x][y].getOwner();
  }

 /**
  * Equals Methode fuer ein Spielfeld
  */
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
      if(b.connections.size() != this.connections.size()) {
        return false;
      }
      for(Connection c : b.connections) {
        if(this.connections.contains(c));
      }
    }
    return true;
  }

  /**
	 * Erzeug eine Deepcopy eines Spielbretts
	 */
  public Object clone() {
    Board clone = new Board(false);
    for (int x = 0; x < Constants.SIZE; x++) {
      for (int y = 0; y < Constants.SIZE; y++) {
        clone.fields[x][y] = this.fields[x][y].clone();
      }
    }
    for(Connection c : this.connections) {
      clone.connections.add(c.clone());
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
   * @param x x-Koordinate
   * @param y y-Koordinate
   *          das Feld, auf das gesetzt wird
   * @param player der setzende Spieler
   */
  public void put(int x, int y, Player player) {
    getField(x, y).setOwner(player.getPlayerColor());
    createNewWires(x, y);
  }

  /**
   * Nur fuer den Server relevant
   * checks whether new wires have to be created and creates them
   * @param x x-coordinate
   * @param y y-coordinate
   */
  private void createNewWires(int x, int y) {
    if (checkPossibleWire(x, y, x - 2, y - 1)) {
      createWire(x, y, x - 2, y - 1);
    }
    if (checkPossibleWire(x, y, x - 1, y - 2)) {
      createWire(x, y, x - 1, y - 2);
    }
    if (checkPossibleWire(x, y, x - 2, y + 1)) {
      createWire(x, y, x - 2, y + 1);
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

  /**
   * Nur fuer den Server relevant
   * creates a wire between two points
   * @param x1 x-coordinate of first point
   * @param y1 y-coordinate of first point
   * @param x2 x-coordinate of second point
   * @param y2 y-coordinate of second point
   */
  private void createWire(int x1, int y1, int x2, int y2) {
    connections.add(new Connection(x1, y1, x2, y2, getField(x1, y1).getOwner()));
//    getField(x1, y1).addConnection(getField(x2, y2));
//    getField(x2, y2).addConnection(getField(x1, y1));

  }

  /**
   * Nur fuer den Server relevant
   * checks whether a wire between two points is possible
   * @param x1 x-coordinate of first point
   * @param y1 y-coordinate of first point
   * @param x2 x-coordinate of second point
   * @param y2 y-coordinate of second point
   * @return true if wire is possible
   */
  private boolean checkPossibleWire(int x1, int y1, int x2, int y2) {
    if (x2 < Constants.SIZE && y2 < Constants.SIZE && x2 >= 0 && y2 >= 0) {
      if (getField(x2, y2).getOwner() == getField(x1, y1).getOwner()) {
        return !existsBlockingWire(x1, y1, x2, y2);
      }
    }
    return false;
  }

  /**
   * Nur fuer den Server relevant
   * checks whether a wire blocks the wire between the two points
   * @param x1 x-coordinate of first point
   * @param y1 y-coordinate of first point
   * @param x2 x-coordinate of second point
   * @param y2 y-coordinate of second point
   * @return true if blocking wire exists
   */
  private boolean existsBlockingWire(int x1, int y1, int x2, int y2) {
    int biggerX = Math.max(x1, x2);
    int smallerX = Math.min(x1, x2);
    int biggerY = Math.max(y1, y2);
    int smallerY = Math.min(y1, y2);
    for (int x = smallerX; x <= biggerX; x++) {
      for (int y = smallerY; y <= biggerY; y++) { // checks all 6 Fields, from
                                                  // where there could be
                                                  // blocking connections
        if (getField(x, y).getOwner() != null && (x != x1 || y != y1)
            && (x != x2 || y != y2)) { // excludes the Fields with no owner and
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
  
  /**
   * Gibt die Leitungen die von einem Feld (x, y) ausgehen zurück, 
   * @param x x-Koordinate
   * @param y y-Koordinate
   * @return die Liste der Leitungen
   */
  public List<Connection> getConnections(int x, int y) {
    List<Connection> xyConnections = new ArrayList<Connection>();
    if(connections != null) {
      for(Connection c : connections) {
        if(c.x1 == x && c.y1 == y ) {
          xyConnections.add(new Connection(x, y, c.x2, c.y2, c.owner));
        }
        if(c.x2 == x && c.y2 == y ) {
          xyConnections.add(new Connection(x, y, c.x1, c.y1, c.owner));
        }
      }
    }
    return xyConnections; 
  }

 /**
  * Nur fuer den Server relevant
  * checks for the wire from (x1, y1) to (x2, y2), if it is blocked by any connection going out from (x,y).
  * @param x1 x-coordinate of first point
  * @param y1 y-coordinate of first point
  * @param x2 x-coordinate of second point
  * @param y2 y-coordinate of second point
  * @param x x-coordinate of possible blocking point
  * @param y y-coordinate of possible blocking point
  * @return true if wire is blocked
  */
  private boolean isWireBlocked(int x1, int y1, int x2, int y2, int x, int y) {
    for(Connection c : getConnections(x, y)) {
      if(Line2D.linesIntersect(x1, y1, x2, y2, x, y, c.x2, c.y2)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Gibt die Felder eines Spielbretts zurück
   * @return fields
   */
  public Field[][] getFields() {
    return this.fields;
  }
  
}
