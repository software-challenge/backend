package sc.plugin2019;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import sc.plugin2019.util.Constants;
import sc.shared.PlayerColor;

import static sc.plugin2019.FieldState.EMPTY;
import static sc.plugin2019.FieldState.OBSTRUCTED;

/**
 * Ein Spielbrett bestehend aus 8x8 Feldern
 */
@XStreamAlias(value = "board")
public class Board {

  @XStreamImplicit(itemFieldName = "fields")
  private Field[][] fields;

  public Board() {
    this.fields = new Field[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
    initialize();
  }

  /**
   * Erstellt eine zufällige Spielbrett. Dazu werden
   */
  private void initialize() {
    for (int x = 0; x < Constants.BOARD_SIZE; x++) {
      for (int y = 0; y < Constants.BOARD_SIZE; y++) {
        fields[x][y] = new Field(x,y);
      }
    }
    // place piranhas
    for (int index = 1; index < Constants.BOARD_SIZE - 1; index++) {
      fields[0][index].setPiranha(PlayerColor.RED);
      fields[Constants.BOARD_SIZE -1][index].setPiranha(PlayerColor.RED);
      fields[index][0].setPiranha(PlayerColor.BLUE);
      fields[index][Constants.BOARD_SIZE -1].setPiranha(PlayerColor.BLUE);
    }
    int firstX = 0, firstY = 0;
    for(int i = 0; i < Constants.NUM_OBSTICLES; i++){
      int x,y;

      // Generate x y coordinate on empty field
      // obstructed fields are in the inner 6x6 field and are not allowed to be in same vertical, diagonal or horizontal line
      do{
        x = (int) (Math.random()*6+2);
        y = (int) (Math.random()*6+2);
      } while(fields[x][y].getState() != EMPTY &&
              (i == 0 || // if first field was generated
                      (firstX == x || firstY == y || // check the generation conditions for second field
                        firstX - firstY == x - y ||
                        firstX + firstY == x + y)));

      fields[x][y].setState(OBSTRUCTED);
      firstX = x;
      firstY = y;
    }

  }

  /**
   * erzeugt eine Deepcopy dieses Objekts
   *
   * @return ein neues Objekt mit gleichen Eigenschaften
   */
  @Override
  public Board clone() {
    Board clone = new Board();
    for (int x = 0; x < Constants.BOARD_SIZE; x++) {
      for (int y = 0; y < Constants.BOARD_SIZE; y++) {
        clone.fields[x][y] = fields[x][y].clone();
      }
    }
    return clone;
  }

  @Override
  public String toString() {
    String toString = "Board:\n";
    StringBuilder b = new StringBuilder(toString);
    for (int x = 0; x < Constants.BOARD_SIZE; x++) {
      for (int y = 0; y < Constants.BOARD_SIZE; y++) {
        b.append(fields[x][y].getPiranha());
      }
    }
    return b.toString();
  }

  public Field getField(int x, int y) {
    return this.fields[x][y];
  }
}

