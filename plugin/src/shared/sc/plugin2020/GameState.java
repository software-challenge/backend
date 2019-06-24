package sc.plugin2020;

import java.util.LinkedList;
import java.util.List;
import sc.shared.PlayerColor;

public class GameState {
  private Board gameBoard;
  private int turn;
  private List<Piece> undeployedRedPieces;
  private List<Piece> undeployedBluePieces;

  public GameState(){
    //Irgendetwas hier machen?
  }

  public List<Piece> getUndeployedPieces(PlayerColor owner){
    if (owner == PlayerColor.RED)
      return undeployedRedPieces;
    else
      return undeployedBluePieces;
  }

  public int getTurn(){
    return turn;
  }

  public void setTurn(int turn){
    this.turn = turn;
  }

  public Board getBoard(){
    return gameBoard;
  }
}
