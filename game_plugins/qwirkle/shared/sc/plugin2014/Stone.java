package sc.plugin2014;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "qw:stone")
public class Stone implements Cloneable {

    // index des stadt in der dieser turm steht
    @XStreamAsAttribute
    private final StoneColor color;

    // index des slots auf dem dieser turm steht
    @XStreamAsAttribute
    private final StoneShape shape;

    public Stone() {
        color = StoneColor.BLUE;
        shape = StoneShape.CIRCLE;
    }

    public Stone(StoneColor color, StoneShape shape) {
        this.color = color;
        this.shape = shape;
    }

    /**
     * klont dieses Objekt
     * 
     * @return ein neues Objekt mit gleichen Eigenschaften
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Holt die Farbe des Steines
     * 
     * @return Steinfarbe
     */
    public StoneColor getColor() {
        return color;
    }

    /**
     * Holt die Form des Steines
     * 
     * @return Steinform
     */
    public StoneShape getShape() {
        return shape;
    }
}
