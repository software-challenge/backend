package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "field")
public class Field {

  @XStreamAsAttribute
  private int index;
  
  @XStreamAsAttribute
  private FieldType type;
  
  public Field(FieldType type) {
    this(type, -1);
  }
  
  private Field(FieldType type, int index) {
    this.index = index;
    this.type = type;
  }
  
  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public FieldType getType() {
    return type;
  }

  public void setType(FieldType type) {
    this.type = type;
  }
  
  @Override
  public Field clone() throws CloneNotSupportedException {
    return new Field(this.type, this.index);
  }
  
}
