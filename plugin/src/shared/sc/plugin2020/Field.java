package sc.plugin2020;

import java.util.Stack;

import sc.api.plugins.IField;
import sc.plugin2020.util.Coord;
import sc.shared.PlayerColor;

public class Field implements IField {

  private Stack<Piece> pieces = new Stack<Piece>();
  private boolean obstructed = false;
  private Coord position;

  public Field(Coord position){
    this.position = position;
  }

  public Field(Coord position, boolean obstructed){
    this.position = position;
    this.obstructed = obstructed;
  }

  public Field(Coord position, Stack<Piece> pieces){
    this.pieces = pieces;
    this.position = position;
  }

  public FieldState getFieldState(){
    if (obstructed)
      return FieldState.OBSTRUCTED;

    if (pieces.peek().getOwner() == PlayerColor.RED)
      return FieldState.RED;

    if (pieces.peek().getOwner() == PlayerColor.BLUE)
      return FieldState.BLUE;

    return FieldState.EMPTY;
  }

  public boolean isObstructed(){
    return obstructed;
  }

  public void setObstructed(boolean o){ obstructed = o;}

  public Stack<Piece> getPieces(){
    return(pieces);
  }

  public Coord getPosition(){
    return position;
  }
}
