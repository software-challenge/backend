package sc.plugin2019;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import sc.plugin2019.util.Constants;
import sc.shared.PlayerColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Ein Spielbrett bestehend aus 8x8 Feldern
 */
@XStreamAlias(value = "board")
public class Board {

  private Separator vertical;

  private  Separator horizontal;

  @XStreamImplicit(itemFieldName = "fields")
  private Field[][] track;

  public Board() {
    this.track = new Field[10][10];
    initialize();
  }

  /**
   * Erstellt eine zufällige Spielbrett. Dazu werden
   */
  private void initialize() {
    for (int x = 0; x < Constants.BOARD_SIZE; x++) {
      for (int y = 0; y < Constants.BOARD_SIZE; y++) {
        track[x][y] = new Field(x,y);
      }
    }
    // place piranhas
    for (int index = 1; index < Constants.BOARD_SIZE - 1; index++) {
      track[0][index].setPiranha(PlayerColor.RED);
      track[Constants.BOARD_SIZE -1][index].setPiranha(PlayerColor.RED);
      track[index][0].setPiranha(PlayerColor.BLUE);
      track[index][Constants.BOARD_SIZE -1].setPiranha(PlayerColor.BLUE);
    }
    // TODO generate separators
    int x1 = ThreadLocalRandom.current().nextInt(0, Constants.BOARD_SIZE);
    int y1 = ThreadLocalRandom.current().nextInt(0, Constants.BOARD_SIZE - 1);
    this.vertical = new Separator(Direction.UP, x1, y1);

    int x2 = ThreadLocalRandom.current().nextInt(0, Constants.BOARD_SIZE - 1);
    int y2 = ThreadLocalRandom.current().nextInt(0, Constants.BOARD_SIZE);
    this.horizontal = new Separator(Direction.RIGHT, x2, y2);
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
        clone.track[x][y] = track[x][y].clone();
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
        b.append(track[x][y].getPiranha());
      }
    }
    return b.toString();
  }

  public Separator getVertical() {
    return vertical;
  }

  public Separator getHorizontal() {
    return horizontal;
  }

  public Field getField(int x, int y) {
    return this.track[x][y];
  }
}

