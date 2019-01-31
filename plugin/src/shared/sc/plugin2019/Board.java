package sc.plugin2019;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import sc.api.plugins.IBoard;
import sc.plugin2019.util.Constants;
import sc.shared.PlayerColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static sc.plugin2019.FieldState.OBSTRUCTED;

/** Spielbrett für Piranhas mit {@link Constants#BOARD_SIZE}² Feldern. */
@XStreamAlias(value = "board")
public class Board implements IBoard {

  @XStreamImplicit(itemFieldName = "fields")
  private Field[][] fields;

  private Board(boolean doInit) {
    this.fields = new Field[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
    if(doInit)
      initialize();
  }

  public Board() {
    this(true);
  }

  public Board(Board boardToClone) {
    this(false);
    for(int x = 0; x < Constants.BOARD_SIZE; x++) {
      for(int y = 0; y < Constants.BOARD_SIZE; y++) {
        fields[x][y] = boardToClone.fields[x][y].clone();
      }
    }
  }

  @Override
  public Board clone() {
    return new Board(this);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Board && Arrays.equals(((Board) obj).fields, this.fields);
  }

  /** Erstellt eine zufälliges Spielbrett. */
  private void initialize() {
    for(int x = 0; x < Constants.BOARD_SIZE; x++) {
      for(int y = 0; y < Constants.BOARD_SIZE; y++) {
        fields[x][y] = new Field(x, y);
      }
    }
    // place piranhas
    for(int index = 1; index < Constants.BOARD_SIZE - 1; index++) {
      fields[0][index].setPiranha(PlayerColor.RED);
      fields[Constants.BOARD_SIZE - 1][index].setPiranha(PlayerColor.RED);
      fields[index][0].setPiranha(PlayerColor.BLUE);
      fields[index][Constants.BOARD_SIZE - 1].setPiranha(PlayerColor.BLUE);
    }
    // place obstacles
    // create a list of coordinates for fields which may be blocked
    List<Field> blockableFields = new ArrayList<>();
    for(int x = Constants.OBSTACLES_START; x < Constants.OBSTACLES_END; x++) {
      for(int y = Constants.OBSTACLES_START; y < Constants.OBSTACLES_END; y++) {
        blockableFields.add(this.getField(x, y));
      }
    }
    // set fields with randomly selected coordinates to blocked
    // coordinates may not lay on same horizontal, vertical or diagonal lines with other selected coordinates
    for(int i = 0; i < Constants.NUM_OBSTACLES; i++) {
      int indexOfFieldToBlock = (int) Math.floor(Math.random() * blockableFields.size());
      Field selectedField = blockableFields.get(indexOfFieldToBlock);
      selectedField.setState(OBSTRUCTED);
      blockableFields = blockableFields.stream().filter(
              field -> (!(field.getX() == selectedField.getX() ||
                      field.getY() == selectedField.getY() ||
                      field.getX() - field.getY() == selectedField.getX() - selectedField.getY() ||
                      field.getX() + field.getY() == selectedField.getX() + selectedField.getY()))
      ).collect(Collectors.toList());
    }
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder("Board {");
    for(int x = 0; x < Constants.BOARD_SIZE; x++) {
      for(int y = 0; y < Constants.BOARD_SIZE; y++) {
        b.append(fields[x][y].getPiranha());
      }
    }
    return b.append('}').toString();
  }

  public Field getField(int x, int y) {
    return this.fields[x][y];
  }

}

