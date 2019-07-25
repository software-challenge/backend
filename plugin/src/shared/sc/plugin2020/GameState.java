package sc.plugin2020;

import java.util.ArrayList;
import java.util.List;

import sc.plugin2020.util.Constants;
import sc.shared.PlayerColor;
import sc.framework.plugins.Player;

import javax.swing.*;

public class GameState {
  private Board gameBoard;
  private int turn;
  private ArrayList<Piece> undeployedRedPieces;
  private ArrayList<Piece> undeployedBluePieces;
  private Player red, blue;

  public GameState(){//Creates a completely new game(state)
    turn = 0;
    gameBoard = new Board();
    undeployedBluePieces = parsePiecesString(Constants.Pieces, PlayerColor.BLUE);
    undeployedRedPieces = parsePiecesString(Constants.Pieces, PlayerColor.RED);
  }

  public Player getCurrentPlayer(){
    return ((turn % 2 == 0) ? red : blue);
  }

  public Player getOtherPlayer(){
    return ((turn % 2 == 0) ? blue : red);
  }

  private ArrayList<Piece> parsePiecesString(String s, PlayerColor p){
    ArrayList<Piece> l = new ArrayList<Piece>();
    for (char c : s.toCharArray()){
      switch (c) {
        case 'Q':
          l.add(new Piece(p, PieceType.BEE));
          continue;
        case 'S':
          l.add(new Piece(p, PieceType.SPIDER));
          continue;
        case 'G':
          l.add(new Piece(p, PieceType.GRASSHOPPER));
          continue;
        case 'B':
          l.add(new Piece(p, PieceType.BEETLE));
          continue;
        case 'A':
          l.add(new Piece(p, PieceType.ANT));
          continue;
      }
    }
    return l;
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

  public void addPlayer(Player player){
    if (player.getColor() == PlayerColor.BLUE)
      blue = player;
    else
      red = player;
  }
}
