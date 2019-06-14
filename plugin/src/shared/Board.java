import sc.plugin2020.util.Constants;
import sc.plugin2020.util.Coord;

public class Board {
  private int shift = Constants.BOARD_SIZE-1/2;
  private Field[][] gameField = new Field[Constants.BOARD_SIZE-1][Constants.BOARD_SIZE-1];

  public Board(){
    //Implement me!
  }

  public Field getField(Coord pos){
    if (pos.x > shift || pos.x < -shift || pos.y > shift || pos.y < -shift)
      throw new IndexOutOfBoundsException();

    int q = pos.x + shift;
    int r = pos.z + shift;
    return gameField[q][r];
  }
}
