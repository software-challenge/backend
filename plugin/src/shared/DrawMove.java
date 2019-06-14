import sc.plugin2020.util.Coord;

public class DrawMove implements Move{
  private Coord start, destination;

  public DrawMove(Coord start, Coord destination){
    this.start = start;
    this.destination = destination;
  }

  public Coord getStart(){
    return start;
  }

  public Coord getDestination(){
    return destination;
  }

  public MoveType getMoveType(){
    return MoveType.DRAWMOVE;
  }
}
