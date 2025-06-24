package sc.plugin2017;

import java.util.ArrayList;
import java.util.Random;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import sc.plugin2017.util.Constants;


@XStreamAlias(value = "board")
public class Board {

	/**
	 * Liste der Spielsegmente
	 */
  private ArrayList<Tile> tiles;


  /**
	 * Erzeugt ein neues, initialisiertes Spielfeld
	 */
  public Board() {
    this.init();
  }

  /**
   * Erzeugt ein neues Spielfeld anhand der gegebenen Segmente
   * @param tiles Spielsegmente des neuen Spielfelds
   */
  public Board(ArrayList<Tile> tiles) {
    this.tiles = tiles;
  }

  /**
   * Erzeugt ein neues Spielfeld
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
    Direction[] direction = new Direction[Constants.NUMBER_OF_TILES];
    int[][] startCoordinates  = new int[Constants.NUMBER_OF_TILES][2];
    ArrayList<Integer> tilesWithPassengers = new ArrayList<Integer>(); // holds all tiles numbers with a passenger field
    for(int i = 0;i < Constants.NUMBER_OF_PASSENGERS; i++) {
      // the cannot be a passenger on the starting tile change to -2 for no passenger on last Tile
      int number;
      do{
        number = rnd.nextInt(Constants.NUMBER_OF_TILES - (Constants.PASSENGER_ON_LAST_TILE ? 1 : 2)) + 1;
      }
      while(tilesWithPassengers.contains(number));
      tilesWithPassengers.add(number);
    }
    direction[0] = Direction.RIGHT;
    startCoordinates[0][0] = 0;
    startCoordinates[0][1] = 0;
    // generate directions of tiles
    for(int i = 1; i < Constants.NUMBER_OF_TILES; i++) {
      int dir;
      if (i == 1) {
        // The tile after the starting tile should always point in the same
        // direction. Otherwise one player would have a disadvantage.
        dir = 0;
      } else {
        if (direction[i-1] == Direction.DOWN_LEFT) {
          // last direction was down left, don't allow more turning to the right (to avoid circles)
          dir = rnd.nextInt(2); // 0 or 1 only straight or turning left
        } else if (direction[i-1] == Direction.UP_LEFT) {
          // last direction was up left, don't allow more turning to the left (to avoid circles)
          dir = rnd.nextInt(2) - 1; // 0 or -1 only straight or turning right
        } else {
          dir = rnd.nextInt(3) - 1; // -1, 0 or 1
        }
      }
      direction[i] = (direction[i-1].getTurnedDirection(dir));
      startCoordinates[i][0] = getXCoordinateInDirection(startCoordinates[i-1][0], direction[i]);
      startCoordinates[i][1] = getYCoordinateInDirection(startCoordinates[i-1][1], direction[i]);
    }
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
  private int getYCoordinateInDirection(int y, Direction direction) {
    switch (direction) {
    case RIGHT:
    case LEFT:
      return y;
    case UP_RIGHT:
    case UP_LEFT:
      return y - 4;
    case DOWN_LEFT:
    case DOWN_RIGHT:
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
  private int getXCoordinateInDirection(int x, Direction direction) {
    switch (direction) {
    case RIGHT:
      return x + 4;
    case LEFT:
      return x - 4;
    case UP_RIGHT:
    case DOWN_RIGHT:
      return x + 2;
    case DOWN_LEFT:
    case UP_LEFT:
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
  private void generateTile(int index, boolean hasPassenger, Direction direction, int x, int y) {
    Random rnd = new Random();
    int blocked = rnd.nextInt(Constants.MAX_ISLANDS - Constants.MIN_ISLANDS + 1) + Constants.MIN_ISLANDS; // 2 to 3 blocked fields
    int special = rnd.nextInt(Constants.MAX_SPECIAL - Constants.MIN_SPECIAL + 1) + Constants.MIN_SPECIAL; // 1 oder 2 special fields
    Tile newTile = new Tile(index, direction.getValue(), x, y, hasPassenger ? 1 : 0, blocked, special);
    tiles.add(newTile);

  }

  private void generateStartField() {
    Tile start = new Tile(0, 0, 0, 0, 0, 0, 0); // generate tile with middle at 0,0 in direction 0
    // with no other fields than WATER fields
    tiles.add(start);
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
   * Gibt ein Feld zurück
   * @param x x-Koordinate
   * @param y y-Koordinate
   * @return Feld an entsprechenden Koordinaten, gibt null zurück, sollte das Feld nicht (mehr) existieren
   */
  protected Field alwaysGetField(int x, int y) {
    for(Tile tile : tiles) {
      Field field = tile.getField(x, y);
      if(field != null) {
        return field;
      }
    }
    return null;
  }

 /**
  * Equals Methode fuer ein Spielbrett
  */
  @Override
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
	 * Erzeugt eine Deepcopy des Spielbretts
	 */
  @Override
  public Board clone() {
    ArrayList<Tile> clonedTiles = new ArrayList<Tile>();
    for (Tile tile : tiles) {
      Tile clonedTile = tile.clone();
      clonedTiles.add(clonedTile);
    }
    Board clone = new Board(clonedTiles);
    return clone;
  }

  /**
   * Gibt eine Liste der sichtbaren Segmente des Spielbretts zurück. Dabei
   * entsprechen die Indizes der Liste NICHT den Nummern der Segmente. Die
   * Nummer des Segmentes kann über die Methode {@link Tile#getIndex()} des
   * Tile-Objektes abgefragt werden.
   * @return fields
   */
  public ArrayList<Tile> getTiles() {
    return this.tiles;
  }

  protected ArrayList<Tile> getVisibleTiles() {
    ArrayList<Tile> visibleTiles = new ArrayList<Tile>();
    for (Tile tile : this.tiles) {
      if(tile.isVisible()) {
        visibleTiles.add(tile);
      }
    }
    return visibleTiles;
  }

  @Override
  public String toString() {
    String toString = "board:\n";
    for (Tile tile : tiles) {
      toString += "\n" + tile.toString();
    }
    return toString;
  }

}
