package sc.plugin2020;

import sc.api.plugins.IBoard;
import sc.api.plugins.IField;
import sc.plugin2020.util.Constants;
import sc.plugin2020.util.Coord;

import java.util.LinkedList;

public class Board implements IBoard {
  private int shift = Constants.BOARD_SIZE-1/2;
  private Field[][] gameField = new Field[Constants.BOARD_SIZE-1][Constants.BOARD_SIZE-1];

  public Field[][] getGameField(){
    return gameField;
  }

  public Board(){
    fillBoard();
  }

  public Board(LinkedList<Field> fields){
    int q;
    int r;
    for (Field f : fields)
    {
      if (f.getPosition().x > shift || f.getPosition().x < -shift || f.getPosition().y > shift || f.getPosition().y < -shift)
        throw new IndexOutOfBoundsException();

      q = f.getPosition().x + shift;
      r = f.getPosition().z + shift;
      gameField[q][r] = f;
    }
    fillBoard();
  }

  private void fillBoard(){
    for(int q = 0; q < Constants.BOARD_SIZE-1; q++){
      for (int r = 0; r < Constants.BOARD_SIZE-1; r++){
        if (gameField[q][r] == null)
          gameField[q][r] = new Field(new Coord(q,r));
          //Hier müssen irgendwie noch die Hindernisse eingebaut werden.
      }
    }
  }

  public Field getField(Coord pos){
    if (pos.x > shift || pos.x < -shift || pos.y > shift || pos.y < -shift)
      throw new IndexOutOfBoundsException();

    int q = pos.x + shift;
    int r = pos.z + shift;
    return gameField[q][r];
  }

  @Override
  public Field getField(int x, int y) {
    if (x > shift || x < -shift || y > shift || y < -shift)
      throw new IndexOutOfBoundsException();

    int q = x + shift;
    int r = (-x)+(-y) + shift;
    return gameField[q][r];
  }

  @Override
  public Field getField(int x, int y, int z) {
    return null;
  }
}