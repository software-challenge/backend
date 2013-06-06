package sc.plugin2014.entities;

import java.awt.Point;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "field")
public class Field implements Cloneable {

    @XStreamAsAttribute
    private int   posX;

    @XStreamAsAttribute
    private int   posY;

    @XStreamAsAttribute
    private Stone stone;

    public Field() {}

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

    public Point getPosAsPoint() {
        return new Point(posX, posY);
    }

    public Stone getStone() {
        return stone;
    }

    public void setStone(Stone stone) {
        this.stone = stone;
    }

    public boolean isFree() {
        return getStone() == null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Field) {
            Field otherField = (Field) o;
            return (otherField.getPosX() == getPosX())
                    && (otherField.getPosY() == getPosY());
        }
        return false;
    }

    @Override
    public String toString() {
        return "Field: (" + getPosX() + ", " + getPosY() + ")";
    }
}
