package sc.plugin2020;

import java.util.Stack;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.collections.ArrayConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;
import sc.api.plugins.IField;
import sc.plugin2020.util.CubeCoordinates;
import sc.shared.PlayerColor;

@XStreamAlias("field")
public class Field implements IField {

  @XStreamImplicit
  private Stack<Piece> pieces = new Stack<Piece>();
  @XStreamAsAttribute
  private boolean obstructed = false;

  // NOTE that we use primitives instead of CubeCoordinates to let XStream searialize coordinates as attributes of field tag
  @XStreamAsAttribute
  private int x;
  @XStreamAsAttribute
  private int y;
  @XStreamAsAttribute
  private int z;

  public Field(CubeCoordinates position){
    this.x = position.x; this.y = position.y; this.z = position.z;
  }

  public Field(CubeCoordinates position, boolean obstructed){
    this.x = position.x; this.y = position.y; this.z = position.z;
    this.obstructed = obstructed;
  }

  public Field(CubeCoordinates position, Stack<Piece> pieces){
    this.x = position.x; this.y = position.y; this.z = position.z;
    this.pieces = pieces;
  }

  public FieldState getFieldState(){
    if (obstructed)
      return FieldState.OBSTRUCTED;

    if (!pieces.isEmpty()) {

      if(pieces.peek().getOwner() == PlayerColor.RED)
        return FieldState.RED;

      if(pieces.peek().getOwner() == PlayerColor.BLUE)
        return FieldState.BLUE;
    }
    return FieldState.EMPTY;
  }

  public boolean isObstructed(){
    return obstructed;
  }

  public void setObstructed(boolean o){ obstructed = o;}

  public Stack<Piece> getPieces(){
    return(pieces);
  }

  public CubeCoordinates getPosition(){
    return new CubeCoordinates(this.x, this.y, this.z);
  }

  public int getX() {
    return getPosition().x;
  }
}
