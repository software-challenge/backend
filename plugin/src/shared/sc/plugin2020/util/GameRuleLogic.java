package sc.plugin2020.util;

import sc.plugin2020.*;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

import java.util.ArrayList;
import java.util.Stack;
import java.util.stream.Stream;

public class GameRuleLogic {

  private GameRuleLogic() {
    throw new IllegalStateException("Can't be instantiated.");
  }

  public static ArrayList<Field> getNeighbours(Board b, CubeCoordinates c){
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

  public static Field getNeighbourInDirection(Board b, CubeCoordinates c, Direction d){
    return b.getField(new CubeCoordinates(c.x + d.shift(1).x,c.y + d.shift(1).y,c.z + d.shift(1).z));
  }

  public static PlayerColor getCurrentPlayerColor(GameState gs){
    return ((gs.getTurn() % 2 == 0) ? PlayerColor.RED : PlayerColor.BLUE);
  }

  public static void performMove(GameState gs, Move m){

  }

  public static boolean isQueenBlocked(Board b, PlayerColor pc){
    ArrayList<CubeCoordinates> l = findPiecesOfTypeAndPlayer(b, PieceType.BEE, pc);

    if (l.size() == 0)
      return false;

    ArrayList<Field> neighbours = getNeighbours(b, l.get(0));
    return neighbours.stream().allMatch((field) -> (field != null && (field.isObstructed() || !field.getPieces().empty())));
  }

  public static ArrayList<Field> fieldsOwnedByPlayer(Board b, PlayerColor color) {
    ArrayList<Field> fields = new ArrayList<>();
    Field[][] gameField = b.getGameField();
    for(int i = 0; i < gameField.length; i++) {
      for(int j = 0; j < gameField[i].length; j++) {
        if (gameField[i][j] != null) {
          Stack<Piece> s = gameField[i][j].getPieces();
          if (!s.empty() && s.peek().getOwner() == color) {
            fields.add(gameField[i][j]);
          }
        }
      }
    }
    return fields;
  }

  public static ArrayList<CubeCoordinates> findPiecesOfTypeAndPlayer(Board b, PieceType pt, PlayerColor pc){
    ArrayList<CubeCoordinates> tmp = new ArrayList<>();

    GameRuleLogic.fieldsOwnedByPlayer(b, pc).forEach((field) -> {
      if (field.getPieces().stream().anyMatch((piece -> piece.getPieceType() == pt))) {
        tmp.add(new CubeCoordinates(field.getPosition()));
      }
    });
    return tmp;
  }

  public static boolean isOnBoard(CubeCoordinates c) {
    int shift = (Constants.BOARD_SIZE - 1) / 2;
    return -shift <= c.x && c.x <= shift && -shift <= c.y && c.y <= shift;
  }

  public static boolean validateMove(GameState gs, Move m) throws InvalidMoveException {
    if (m.isSetMove()) {
      ArrayList<Field> ownedFields = GameRuleLogic.fieldsOwnedByPlayer(gs.getBoard(), gs.getCurrentPlayerColor());
      if (ownedFields.isEmpty()) {
        ArrayList<Field> otherPlayerFields = GameRuleLogic.fieldsOwnedByPlayer(gs.getBoard(), gs.getOtherPlayerColor());
        if (otherPlayerFields.isEmpty()) {
          if (!GameRuleLogic.isOnBoard(m.getDestination())) {
            throw new InvalidMoveException(
                    String.format(
                            "Piece has to be placed on board. Destination (%d,%d) is out of bounds.",
                            m.getDestination().x, m.getDestination().y, m.getDestination().z
                    )
            );
          };
        } else {
          // NOTE that other player should have exactly one piece on the board here, so working with a list of fields is not really neccessary
          Stream<Field> emptyNeighbours = otherPlayerFields.stream().flatMap((field) -> {
            return GameRuleLogic.getNeighbours(gs.getBoard(), field.getPosition()).stream().filter((neighbour) -> {
              return neighbour.getFieldState() == FieldState.EMPTY;
            });
          });
          if (emptyNeighbours.noneMatch((field) -> field.getPosition().equals(m.getDestination()))) {
            throw new InvalidMoveException("Piece has to be placed next to other players piece");
          }
        }
      } else {
        if (GameRuleLogic.findPiecesOfTypeAndPlayer(gs.getBoard(), PieceType.BEE, gs.getCurrentPlayerColor()).size() != 1)
          if (GameRuleLogic.fieldsOwnedByPlayer(gs.getBoard(), gs.getCurrentPlayerColor()).size() == 3)
            throw new InvalidMoveException("The Bee must be placed at least as fourth piece");

        if (!gs.getUndeployedPieces(gs.getCurrentPlayerColor()).contains(m.getPiece()))
          throw new InvalidMoveException("Piece is not a undeployed piece of the current player");

        if (isPosNeighbourOfColour(gs.getBoard(), gs.getCurrentPlayerColor(), m.getDestination())) {
          if(isPosNeighbourOfColour(gs.getBoard(), gs.getCurrentPlayerColor().opponent(), m.getDestination())){
            throw new InvalidMoveException("The destination of the move is too close to the pieces of the opponent");
          }
        }else{
          throw new InvalidMoveException("The destination of the move is too far away from the own pieces");
        }
      }
    } else {
      if (GameRuleLogic.findPiecesOfTypeAndPlayer(gs.getBoard(), PieceType.BEE, gs.getCurrentPlayerColor()).size() != 1)
        throw new InvalidMoveException("The Queen is not placed. Until then drawmoves are not allowed");
      if (m.getStart().equals(m.getDestination()))
        throw new InvalidMoveException("The destination is the start. No waiting moves allowed.");
    }
    return true;
  }

  public static boolean isPosNeighbourOfColour(Board b, PlayerColor c, CubeCoordinates coord){
    ArrayList<Field> playerFields = fieldsOwnedByPlayer(b, c);

    for (Field i : playerFields)
      for(Field j : getNeighbours(b, i.getPosition()))
        if(j.getPosition().equals(coord))
          return true;

    return false;
  }

  public static boolean boardIsEmpty(Board b){
    return (fieldsOwnedByPlayer(b, PlayerColor.BLUE).isEmpty() && fieldsOwnedByPlayer(b, PlayerColor.RED).isEmpty());
  }
  public static ArrayList<Move> getPossibleMoves(GameState gs) {

    //Gather all setMoves
    ArrayList<Move> setMoves = new ArrayList<>();

    //if(boardIsEmpty(gs.getBoard()))

    return setMoves;
  }
}
