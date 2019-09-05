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

  public Piece getPiece() { return piece; }

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

  public boolean isSetMove() {
    return this.getMoveType() == MoveType.SETMOVE;
  }

  public boolean isDragMove() {
    return this.getMoveType() == MoveType.DRAGMOVE;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;

    if (o == null || getClass() != o.getClass())
      return false;

    Move m = (Move) o;

    if ((m.getMoveType() != this.getMoveType()) || (this.getDestination() == m.getDestination()))
      return false;

    if (this.getMoveType() == MoveType.SETMOVE)
      return this.getPiece() == m.getPiece();
    else
      return this.getStart() == m.getStart();
  }
}
