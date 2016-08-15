package sc.plugin2017;

import java.util.ArrayList;
import java.util.Random;

import sc.plugin2017.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


@XStreamAlias(value = "board")
public class Board {

	/**
	 * Liste der Spielsegmente
	 */
  private ArrayList<Tile> tiles;
  

  /**
	 * Erzeug ein neues Spielfeld
	 */
  public Board() {
    this.init();
  }
  
  public Board(ArrayList<Tile> tiles) {
    this.tiles = tiles;
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
    tiles = new ArrayList<Tile>();
    Random rnd = new Random(); 
    int[] direction = new int[Constants.NUMBER_OF_TILES];
    int[][] startCoordinates  = new int[Constants.NUMBER_OF_TILES][2];
    ArrayList<Integer> tilesWithPassengers = new ArrayList<Integer>(); // holds all tiles numbers with a passenger field
    for(int i = 0;i < Constants.NUMBER_OF_PASSENGERS; i++) {
      // the cannot be a passenger on the starting tile change to -2 for no passenger on last Tile
      int number = rnd.nextInt(Constants.NUMBER_OF_TILES - 2) + 1; 
      while(tilesWithPassengers.contains(number)) {
        number = rnd.nextInt(Constants.NUMBER_OF_TILES - 2) + 1;
      }
      tilesWithPassengers.add(number);
    }
    direction[0] = 0;
    startCoordinates[0][0] = 0;
    startCoordinates[0][1] = 0;
    // generate directions of tiles
    int directionLeft = 0;
    int directionRight = 0;
    for(int i = 1; i < Constants.NUMBER_OF_TILES; i++) {
      int dir = rnd.nextInt(3) - 1; // get a number in {-1,0,1}
      if(directionLeft + dir < -4)  {
        // turn left not allowed
        dir = rnd.nextInt(2);
      } else if(directionRight + dir > 4) {
        // turn right not allowed
        dir = rnd.nextInt(2) - 1;
      }
      direction[i] = (direction[i-1] + dir + 6/*number of directions*/) % 6;
      startCoordinates[i][0] = getXCoordinateInDirection(startCoordinates[i-1][0], direction[i]);
      startCoordinates[i][1] = getYCoordinateInDirection(startCoordinates[i-1][1], direction[i]);
    }
    for(int i = 0; i < Constants.NUMBER_OF_TILES; i++) {
      System.out.print(direction[i] + " ");
    }
     System.out.println(tilesWithPassengers);
    generateStartField();
    for(int i = 1; i < Constants.NUMBER_OF_TILES; i++) {
      generateTile(i, tilesWithPassengers.contains(i), direction[i], startCoordinates[i][0], startCoordinates[i][1]);
    }
  }

  /**
   * Nur fuer den Server relevant. Gibt Koordiante 4 Felder in Richtung zurück
   * @param y y Koordinate
   * @param direction Richtung
   * @return y Koordinate des neuen Feldes
   */
  private int getYCoordinateInDirection(int y, int direction) {
    switch (direction) {
    case 0:
    case 3:
      return y;
    case 1:
    case 2:
      return y - 4;
    case 4:
    case 5:
      return y + 4;
    }
    return 0;
  }

  /**
   * Nur fuer den Server relevant. Gibt Koordiante 4 Felder in Richtung zurück
   * @param x x Koordinate
   * @param direction Richtung
   * @return x Koordinate des neuen Feldes
   */
  private int getXCoordinateInDirection(int x, int direction) {
    switch (direction) {
    case 0:
      return x + 4;
    case 3:
      return x - 4;
    case 1:
    case 5:
      return x + 2;
    case 4:
    case 2:
      return x - 2;
    }
    return 0;
  }

  /**
   * Nur fuer den Server relevant
   * generates tile
   * @param index index of Tile
   * @param hasPassenger has the Tile a passenger?
   * @param direction direction of tile
   * @param x x Coordinate of middle
   * @param y y Coordinate of middle
   */
  private void generateTile(int index, boolean hasPassenger, int direction, int x, int y) {
    Random rnd = new Random();
    int blocked = rnd.nextInt(2) + 2; // 2 to 3 blocked fields
    int special = rnd.nextInt(2) + 1; // 1 oder 2 special fields
    Tile newTile = new Tile(index, direction, x, y, hasPassenger ? 1 : 0, blocked, special);
    tiles.add(newTile);
    
  }

  private void generateStartField() {
    Tile start = new Tile(0, 0, 0, 0, 0, 0, 0); // generate tile with middle at 0,0 in direction 0
    // with no other fields than WATER fields
    tiles.add(start);
    System.out.println("Generated start field");
  }

  /**
   * Nur fuer den Server relevant
   * creates a new clear board
   */
  private void makeClearBoard() {
    tiles = new ArrayList<Tile>();
  }

  /**
   * Gibt ein Feld zurück
   * @param x x-Koordinate
   * @param y y-Koordinate
   * @return Feld an entsprechenden Koordinaten, gibt null zurück, sollte das Feld nicht (mehr) existieren
   */
  public Field getField(int x, int y) {
    for(Tile tile : tiles) {
      if(tile.isVisible()) {
        Field field = tile.getField(x, y);
        if(field != null) {
          return field;
        }
      }
    }
    return null;
  }

 /**
  * Equals Methode fuer ein Spielfeld
  */
  public boolean equals(Object o) {
    if(o instanceof Board) {
      Board board = (Board) o;
      ArrayList<Tile> tiles1 = board.tiles;
      ArrayList<Tile> tiles2 = this.tiles;
      if(tiles1.size() != tiles2.size()) {
        return false;
      }
      for(int i = 0; i < tiles1.size(); i++) {
        if(!tiles1.get(i).equals(tiles2.get(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
	 * Erzeug eine Deepcopy eines Spielbretts
	 */
  public Object clone() {
    ArrayList<Tile> clonedTiles = new ArrayList<Tile>();
    for (Tile tile : tiles) {
      Tile clonedTile = tile.clone();
      clonedTiles.add(clonedTile);
    }
    Board clone = new Board(clonedTiles); 
    return clone;
  }
  
  /**
   * Gibt die Felder eines Spielbretts zurück
   * @return fields
   */
  protected ArrayList<Tile> getTiles() {
    return this.tiles;
  }

  public ArrayList<Tile> getVisibleTiles() {
    ArrayList<Tile> visibleTiles = new ArrayList<Tile>(); 
    for (Tile tile : visibleTiles) {
      if(tile.isVisible()) {
        visibleTiles.add(tile.clone());
      }
    }
    return visibleTiles;
  }
  
}
