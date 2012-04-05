package sc.plugin2014.entities;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "qw:field")
public class Field implements Cloneable {

    @XStreamAsAttribute
    private final int posX;

    @XStreamAsAttribute
    private final int posY;

    @XStreamAsAttribute
    private Stone     stone;

    public Field(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Stone getStone() {
        return stone;
    }

    protected void setStone(Stone stone) {
        this.stone = stone;
    }

    public boolean isFree() {
        return getStone() == null;
    }
}
