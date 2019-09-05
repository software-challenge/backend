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
      // TODO: Check if swarm will be disconnected

      //If the queen is not placed, there should be no drag move
      if (GameRuleLogic.findPiecesOfTypeAndPlayer(gs.getBoard(), PieceType.BEE, gs.getCurrentPlayerColor()).size() != 1)
        throw new InvalidMoveException("The Queen is not placed. Until then drawmoves are not allowed");

      if (!GameRuleLogic.isOnBoard(m.getDestination()) || !GameRuleLogic.isOnBoard(m.getStart()))
        throw new InvalidMoveException("The Move is out of bounds. Watch out");

      //Is a piece at the start position
      if (gs.getBoard().getField(m.getStart()).getPieces().size() == 0)
        throw new InvalidMoveException("There is no piece to move");

      //Does the piece has the color of the current player
      if (gs.getBoard().getField(m.getStart()).getPieces().peek().getOwner() != gs.getCurrentPlayerColor())
        throw new InvalidMoveException("It is not this players move");

      //No waiting moves allowed
      if (m.getStart().equals(m.getDestination()))
        throw new InvalidMoveException("The destination is the start. No waiting moves allowed.");

      //Only beetles should climb on other pieces
      if (isPosOnFieldOfColour(gs.getBoard(), gs.getCurrentPlayerColor().opponent(), m.getDestination()) || isPosOnFieldOfColour(gs.getBoard(), gs.getCurrentPlayerColor(), m.getDestination()))
        if (gs.getBoard().getField(m.getStart()).getPieces().peek().getPieceType() != PieceType.BEETLE)
          throw new InvalidMoveException("Only beetles are allowed to climb on other Pieces");

      //The general checks are done. Now the piece specific ones
      switch(gs.getBoard().getField(m.getStart()).getPieces().peek().getPieceType()) {
        case ANT:
          //HARD
          break;
        case BEE:
          validateBeeMove(gs.getBoard(), m);
          break;
        case BEETLE:
          break;
        case GRASSHOPPER:
          break;
        case SPIDER:
          break;
      }
    }
    return true;
  }

  public static boolean validateAntMove(GameState gs, Move m) throws InvalidMoveException{
    return false;
  }
  public static void validateBeeMove(Board b, Move m) throws InvalidMoveException{
    if (!isPathToNextFieldClear(b, m.getStart(), m.getDestination()))
      throw new InvalidMoveException("There is no path to your destination");
  }
  public static boolean validateBeetleMove(GameState gs, Move m) throws InvalidMoveException{
    return false;
  }
  public static boolean validateGrasshopperMove(GameState gs, Move m) throws InvalidMoveException{
    return false;
  }
  public static boolean validateSpiderMove(GameState gs, Move m) throws InvalidMoveException{
    return false;
  }

  public static boolean isPathToNextFieldClear(Board b, CubeCoordinates coord1, CubeCoordinates coord2){
    ArrayList<Field> path = sharedNeighboursOfTwoCoords(b, coord1, coord2);
    for (Field i : path)
      if (!i.isObstructed() && i.getPieces().size() == 0)
        return true;

    return false;
  }

  public static boolean twoFieldsOnOneStraight(CubeCoordinates coord1, CubeCoordinates coord2){
    return coord1.x == coord2.x || coord1.y == coord2.y || coord1.z == coord2.z;
  }

  public static ArrayList<Field> sharedNeighboursOfTwoCoords(Board b, CubeCoordinates coord1, CubeCoordinates coord2){
    ArrayList<Field> tmp = new ArrayList<>();

    for ( Field i : getNeighbours(b, coord1)) {
      for (Field j : getNeighbours(b, coord2)){
        if (i.getPosition().equals(j.getPosition()))
          tmp.add(j);
      }
    }
    return tmp;
  }

  public static boolean isPosOnFieldOfColour(Board b, PlayerColor c, CubeCoordinates coord){
    ArrayList<Field> playerFields = fieldsOwnedByPlayer(b, c);

    for (Field i : playerFields)
      if (i.getPosition().equals(coord))
        return true;

    return false;
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
