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

    for(int i = 0; i < Constants.NUM_OBSTICLES; i++){
      int x,y;

      // Generate x y coordinate on empty field
      do{
        x = (int) (Math.random()*8+1);
        y = (int) (Math.random()*8+1);
      } while(fields[x][y].getState() != EMPTY);

      fields[x][y].setState(OBSTRUCTED);
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

