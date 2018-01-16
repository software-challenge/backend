package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Ein Feld des Spielfelds. Ein Spielfeld ist durch den index eindeutig identifiziert.
 * Das type Attribut gibt an, um welchen Feldtyp es sich handelt.
 */
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

  /**
   * Nur für den Server (für Test) relevant.
   * @param index Index des Feldes
   */
  public void setIndex(int index) {
    this.index = index;
  }

  public FieldType getType() {
    return type;
  }

  /**
   * Nur für den Server (für Test) relevant.
   * @param type Feldtyp
   */
  public void setType(FieldType type) {
    this.type = type;
  }
  
  @Override
  public Field clone() {
    return new Field(this.type, this.index);
  }
  
}
