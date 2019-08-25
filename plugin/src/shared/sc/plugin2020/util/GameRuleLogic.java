package sc.plugin2020.util;

import sc.plugin2020.*;
import sc.shared.PlayerColor;

import java.util.ArrayList;
import java.util.Stack;

public class GameRuleLogic {

  private GameRuleLogic() {
    throw new IllegalStateException("Can't be instantiated.");
  }

  public static ArrayList<Field> getNeighbours(Board b, Coord c){
    ArrayList<Field> tmp = new ArrayList<>();

    for (Direction d : Direction.values()){
      Field n;
      try {
        n = getNeighbourInDirection(b, c, d);
      }
      catch(IndexOutOfBoundsException ex)
      {continue;}

      tmp.add(n);
    }

    return tmp;
  }

  public static Field getNeighbourInDirection(Board b, Coord c, Direction d){
    return b.getField(new Coord(c.x + d.shift(1).x,c.y + d.shift(1).y,c.z + d.shift(1).z));
  }

  public static PlayerColor getCurrentPlayerColor(GameState gs){
    return ((gs.getTurn() % 2 == 0) ? PlayerColor.RED : PlayerColor.BLUE);
  }

  public static void performMove(GameState gs, Move m){

  }

  public static boolean isQueenBlocked(Board b, PlayerColor pc){
    ArrayList<Coord> l = findPiecesOfTypeAndPlayer(b, pc, PieceType.BEE);
    
    if (l.size() == 0)
      return false;

    ArrayList<Field> neighbours = getNeighbours(b, l.get(0));
    //Hier weitermachen.

    return false;
  }

  public static ArrayList<Coord> findPiecesOfTypeAndPlayer(Board b, PlayerColor pc, PieceType pt){
    ArrayList<Coord> tmp = new ArrayList<>();
    Field[][] gameField = b.getGameField();

    for(int i = 0; i < gameField.length; i++) {
      for(int j = 0; j < gameField[i].length; j++) {
        Stack<Piece> s = gameField[i][j].getPieces();
        for (int k = 0; k < s.size(); k++)
        {
          Piece p = s.get(k);
          if (p.getOwner() == pc && p.getPieceType() == pt)
            tmp.add(new Coord(i,j));
        }
      }
    }
    return tmp;
  }
}
