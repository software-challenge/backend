package sc.plugin2020;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.shared.PlayerColor;

@XStreamAlias(value = "piece")
public class Piece {

  @XStreamAsAttribute
  private PlayerColor owner;
  @XStreamAsAttribute
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
