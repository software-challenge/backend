package sc.plugin2020;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.api.plugins.IMove;
import sc.plugin2020.util.CubeCoordinates;

@XStreamAlias(value = "move")
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
  public boolean equals(Object obj) {
    Move to = (Move)obj;
    if (this.isSetMove()) {
      return (to.isSetMove() && this.destination.equals(to.destination) && this.piece.equals(to.piece));
    } else {
      return (to.isDragMove() && this.start.equals(to.start) && this.destination.equals(to.destination));
    }
  }

  @Override
  public String toString() {
    if (isSetMove()) {
      return String.format("Set-move of %s %s to %s", this.piece.getOwner(), this.piece.getPieceType(), this.destination);
    } else {
      return String.format("Drag-move from %s to %s", this.start, this.destination);
    }
  }
}
