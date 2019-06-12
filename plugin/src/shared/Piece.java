import sc.shared.PlayerColor;

public class Piece {
  PlayerColor owner;
  PieceType type;

  Piece (PlayerColor owner, PieceType type){
    this.owner = owner;
    this.type = type;
  }

  PlayerColor getOwner (){
    return owner;
  }

  PieceType getPieceType(){
    return type;
  }
}
