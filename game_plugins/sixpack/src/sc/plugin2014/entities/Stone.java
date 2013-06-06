package sc.plugin2014.entities;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "stone")
public class Stone implements Cloneable {

    @XStreamAsAttribute
    private final StoneColor color;

    @XStreamAsAttribute
    private final StoneShape shape;

    @XStreamAsAttribute
    private final int        identifier;

    public Stone() {
        identifier = 0;
        color = StoneColor.BLUE;
        shape = StoneShape.ACORN;
    }

    public Stone(StoneColor color, StoneShape shape) {
        identifier = StoneIdentifierGenerator.getNextId();
        this.color = color;
        this.shape = shape;
    }

    public StoneColor getColor() {
        return color;
    }

    public StoneShape getShape() {
        return shape;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Stone)
                && (((Stone) obj).identifier == identifier)
                && (((Stone) obj).color == color)
                && (((Stone) obj).shape == shape);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
