package sc.plugin2020;

import sc.api.plugins.IMove;
import sc.plugin2020.util.CubeCoordinates;

public class Move implements IMove {
  private CubeCoordinates start, destination;
  private Piece piece;

  public Move(CubeCoordinates start, CubeCoordinates destination){
    this.start = start;
    this.destination = destination;
  }

  public Move(Piece piece, CubeCoordinates destination){
    this.piece = piece;
    this.destination = destination;
  }

  public CubeCoordinates getStart(){
    return start;
  }

  public CubeCoordinates getDestination(){
    return destination;
  }

  public MoveType getMoveType(){
    if (piece == null)
      return MoveType.DRAGMOVE;
    else
      return MoveType.SETMOVE;
  }
}
