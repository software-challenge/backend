package sc.plugin2020;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.jetbrains.annotations.NotNull;
import sc.api.plugins.TwoPlayerGameState;
import sc.framework.plugins.Player;
import sc.plugin2020.util.Constants;
import sc.shared.PlayerColor;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias(value = "state")
public class GameState extends TwoPlayerGameState<Player, Move> implements Cloneable {
  private Board board;
  @XStreamAsAttribute
  private int turn;
  private ArrayList<Piece> undeployedRedPieces;
  private ArrayList<Piece> undeployedBluePieces;
  private Player red, blue;

  public GameState() {//Creates a completely new game(state)
    turn = 0;
    board = new Board();
    undeployedBluePieces = parsePiecesString(Constants.PIECES, PlayerColor.BLUE);
    undeployedRedPieces = parsePiecesString(Constants.PIECES, PlayerColor.RED);
  }

  public void setBoard(Board b) {
    board = b;
  }

  //public Player getCurrentPlayer(){ // Ich verstehe die Syntax nicht HELP _________________________________
  //  return ((turn % 2 == 0) ? red : blue);
  //}

  //public Player getOtherPlayer(){
  //  return ((turn % 2 == 0) ? blue : red);
  //}

  private ArrayList<Piece> parsePiecesString(String s, PlayerColor p) {
    ArrayList<Piece> l = new ArrayList<Piece>();
    for(char c : s.toCharArray()) {
      switch(c) {
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

  public List<Piece> getUndeployedPieces(PlayerColor owner) {
    if(owner == PlayerColor.RED)
      return undeployedRedPieces;
    else
      return undeployedBluePieces;
  }

  public int getTurn() {
    return turn;
  }

  public void setTurn(int turn) {
    this.turn = turn;
  }

  public Board getBoard() {
    return board;
  }

  public void addPlayer(Player player) {
    if(player.getColor() == PlayerColor.BLUE)
      blue = player;
    else
      red = player;
  }

  @Override
  public int getRound() {
    return turn / 2; //Ist das richtig?
  }

  @NotNull
  @Override
  public Player getRed() {
    return red;
  }

  @NotNull
  @Override
  public Player getBlue() {
    return blue;
  }

  @Override
  public int getPointsForPlayer(@NotNull PlayerColor playerColor) {
    return turn;
  }

  public int[] getPlayerStats(Player p) {
    return getPlayerStats(p.getColor());
  }

  public int[] getPlayerStats(PlayerColor p) {
    int[] tmp = new int[1];
    tmp[0] = turn;
    return tmp;
  }

  public void setBlue(Player newBlue) {
    blue = newBlue;
  }

  public void setRed(Player newRed) {
    red = newRed;
  }

  public int[][] getGameStats() {
    int[][] tmp = new int[1][1];
    return tmp; // Nur reingeschrieben. Ich weiß nicht genau, wie es funktioniert. KOtlinsyntax
  }

  @NotNull
  @Override
  public String toString() {
    return String.format("GameState Zug %d", this.getTurn());
  }
}
