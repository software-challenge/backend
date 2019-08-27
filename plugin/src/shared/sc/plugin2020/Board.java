package sc.plugin2020;

import sc.api.plugins.IBoard;
import sc.plugin2020.util.Constants;
import sc.plugin2020.util.CubeCoordinates;

import java.util.LinkedList;

public class Board implements IBoard {
  private int shift = (Constants.BOARD_SIZE-1)/2;
  private Field[][] gameField = new Field[Constants.BOARD_SIZE][Constants.BOARD_SIZE];

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

    for (int x = -shift; x <= shift; x++) {
      for (int y = Math.max(-shift, -x-shift); y <= Math.min(shift, -x+shift); y++) {
        if (gameField[x+shift][y+shift] == null) {
          gameField[x+shift][y+shift] = new Field(new CubeCoordinates(x, y));
        }
      }
    }
  }

  public Field getField(CubeCoordinates pos){
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