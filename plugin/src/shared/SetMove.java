import sc.plugin2020.util.Coord;

public class SetMove implements Move{

  private Piece piece;
  private Coord destination;

  public SetMove(Piece piece, Coord destination){
    this.piece = piece;
    this.destination = destination;
  }

  public MoveType getMoveType(){
    return MoveType.SETMOVE;
  }
}
