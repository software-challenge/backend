import sc.plugin2020.util.Coord;

public class Move {
  Coord start, destination;
  Piece piece;

  Move(Coord start, Coord destination){
    this.start = start;
    this.destination = destination;
  }

  Move(Piece piece, Coord destination){
    this.piece = piece;
    this.destination = destination;
  }

  Coord getStart(){
    return start;
  }

  Coord getDestination(){
    return destination;
  }

  Piece getPiece(){
    return piece;
  }

  MoveType getMoveType(){
    if (piece == null)
      return MoveType.DRAWMOVE;
    else
      return MoveType.SETMOVE;
  }
}
