package sc.plugin2014.entities;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "qw:stone")
public class Stone implements Cloneable {

    @XStreamAsAttribute
    private final StoneColor color;

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

    public StoneColor getColor() {
        return color;
    }

    public StoneShape getShape() {
        return shape;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
