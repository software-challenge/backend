import java.util.Stack;
import sc.plugin2020.util.Coord;
import sc.shared.PlayerColor;

public class Field {

  Stack<Piece> pieces = new Stack<Piece>();
  boolean obstructed = false;
  Coord position;

  Field(Coord position){
    this.position = position;
  }

  Field(Coord position, boolean obstructed){
    this.position = position;
    this.obstructed = obstructed;
  }

  Field(Coord position, Stack<Piece> pieces){
    this.pieces = pieces;
  }

  FieldState getFieldState(){
    if (obstructed)
      return FieldState.OBSTRUCTED;

    if (pieces.peek().getOwner() == PlayerColor.RED)
      return FieldState.RED;

    if (pieces.peek().getOwner() == PlayerColor.BLUE)
      return FieldState.BLUE;

    return FieldState.EMPTY;
  }

  boolean isObstructed(){
    return obstructed;
  }

  Stack<Piece> getPieces(){
    return(pieces);
  }

  Coord getPosition(){
    return position;
  }
}
