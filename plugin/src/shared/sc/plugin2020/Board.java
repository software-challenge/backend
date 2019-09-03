package sc.plugin2020;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.collections.ArrayConverter;
import com.thoughtworks.xstream.converters.extended.ToStringConverter;
import sc.api.plugins.IBoard;
import sc.plugin2020.util.Constants;
import sc.plugin2020.util.CubeCoordinates;

import java.util.LinkedList;

@XStreamAlias(value = "board")
public class Board implements IBoard {
  @XStreamOmitField
  private int shift = (Constants.BOARD_SIZE - 1) / 2;

  // NOTE that this adds <null/> to the XML where fields of the array are null. This is required for proper deserialization, maybe we find a better way
  @XStreamConverter(value = ArrayConverter.class, nulls = {ToStringConverter.class})
  @XStreamImplicit(itemFieldName = "fields")
  private Field[][] gameField = new Field[Constants.BOARD_SIZE][Constants.BOARD_SIZE];

  public Field[][] getGameField() {
    return gameField;
  }

  public Board() {
    fillBoard();
  }

  public Board(LinkedList<Field> fields) {
    int x;
    int y;
    for(Field f : fields) {
      if(f.getPosition().x > shift || f.getPosition().x < -shift || f.getPosition().y > shift || f.getPosition().y < -shift)
        throw new IndexOutOfBoundsException();
      x = f.getPosition().x + shift;
      y = f.getPosition().y + shift;
      gameField[x][y] = f;
    }
    fillBoard();
  }

  private void fillBoard() {
    for(int x = -shift; x <= shift; x++) {
      for(int y = Math.max(-shift, -x - shift); y <= Math.min(shift, -x + shift); y++) {
        if(gameField[x + shift][y + shift] == null) {
          gameField[x + shift][y + shift] = new Field(new CubeCoordinates(x, y));
        }
      }
    }
  }

  public Field getField(CubeCoordinates pos) {
    return gameField[pos.x + shift][pos.y + shift];
  }

  @Override
  public Field getField(int cubeX, int cubeY) {
    return this.getField(new CubeCoordinates(cubeX, cubeY));
  }

  @Override
  public Field getField(int cubeX, int cubeY, int cubeZ) {
    return this.getField(new CubeCoordinates(cubeX, cubeY));
  }
}