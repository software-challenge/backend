package sc.plugin2020;

import sc.shared.PlayerColor;

public class Piece {
  private PlayerColor owner;
  private PieceType type;

  public Piece (PlayerColor owner, PieceType type){
    this.owner = owner;
    this.type = type;
  }

  public PlayerColor getOwner(){
    return owner;
  }

  public PieceType getPieceType(){
    return type;
  }
}
