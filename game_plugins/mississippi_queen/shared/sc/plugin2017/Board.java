package sc.plugin2017;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sc.plugin2017.util.BoardConverter;
import sc.plugin2017.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;


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
    Random rnd = new Random(); 
    int[] direction = new int[Constants.NUMBER_OF_TILES];
    ArrayList<Integer> tilesWithPassengers = new ArrayList<Integer>(); // holds all tiles numbers with a passenger field
    for(int i = 0;i < Constants.NUMBER_OF_PASSENGERS; i++) {
      int number = rnd.nextInt(Constants.NUMBER_OF_TILES - 2) + 1;
      while(tilesWithPassengers.contains(number)) {
        number = rnd.nextInt(Constants.NUMBER_OF_TILES - 2) + 1;
      }
      tilesWithPassengers.add(number);
    }
    
    // TODO generate 
    generateStartField();
    for(int i = 1; i < Constants.NUMBER_OF_TILES - 1; i++) {
      generateTile(tilesWithPassengers.contains(i), direction[i]);
    }
  }

  private void generateTile(boolean hasPassenger, int direction) {
    // TODO Auto-generated method stub
    
  }

  private void generateStartField() {
    // TODO Auto-generated method stub
    
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
    ArrayList<Tile> visibleTiles = new ArrayList<Tile>(this.tiles); 
    for (Tile tile : visibleTiles) {
      if(!tile.isVisible()) {
        visibleTiles.remove(tile);
      }
    }
    return visibleTiles;
  }
  
}
