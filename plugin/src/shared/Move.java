import sc.plugin2020.util.Coord;

public class Move {
  private Coord start, destination;
  private Piece piece;

  public Move(Coord start, Coord destination){
    this.start = start;
    this.destination = destination;
  }

  public Move(Piece piece, Coord destination){
    this.piece = piece;
    this.destination = destination;
  }

  public Coord getStart(){
    return start;
  }

  public Coord getDestination(){
    return destination;
  }

  public MoveType getMoveType(){
    if (piece == null)
      return MoveType.DRAGMOVE;
    else
      return MoveType.SETMOVE;
  }
}
