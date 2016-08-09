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
  private List<Tile> tiles;
  

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
      Field field = tile.getField(x, y);
      if(field != null) {
        return field;
      }
    }
    return null;
  }

 /**
  * Equals Methode fuer ein Spielfeld
  */
  public boolean equals(Object o) {
    // TODO 
    return true;
  }

  /**
	 * Erzeug eine Deepcopy eines Spielbretts
	 */
  public Object clone() {
    Board clone = new Board(false);
    // TODO 
    return clone;
  }

  /**
   * Setzt ein Schiff auf das Spielfeld und entfernt das alte. Diese Methode ist nur für den
   * Server relevant, da hier keine Fehlerüberprüfung durchgeführt wird. Zum
   * Ausführen von Zügen die
   * {@link sc.plugin2017.Move#perform(GameState, Player) perform}-Methode
   * benutzen.
   * 
   * @param x x-Koordinate
   * @param y y-Koordinate
   *          das Feld, auf das gesetzt wird
   * @param player der setzende Spieler
   */
  public void put(int x, int y, Player player) {
    // TODO tile und x sowie y setzen, eventuell eventuell nach Gamestate auslagern, da hier keine Spielerinformationen sind
  }

  
  /**
   * Gibt die Felder eines Spielbretts zurück
   * @return fields
   */
  public List<Tile> getTiles() {
    return this.tiles;
  }
  
}
