package sc.plugin2014;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Ein Turm an einer Position in einer Stadt.<br/>
 * <br/>
 * Auch für leere Felder nutzbar, dann hat der Turm die Hoehe 0.
 * 
 */
@XStreamAlias(value = "qw:stone")
public class Stone implements Cloneable {

    // index des stadt in der dieser turm steht
    @XStreamAsAttribute
    public final int color;

    // index des slots auf dem dieser turm steht
    @XStreamAsAttribute
    public final int shape;

    /**
     * XStream benötigt eventuell einen parameterlosen Konstruktor
     * bei der Deserialisierung von Objekten aus XML-Nachrichten.
     */
    public Stone() {
        color = -1;
        shape = -1;
    }

    /**
     * Ein neuer Turm an gegebener Position mit Hoehe 0
     * 
     * @param city
     *            Stadt des Turms
     * @param slot
     *            Position des Turms
     */
    public Stone(int color, int shape) {
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
     * Gibt die Anzahl roter Segmente im Turm
     * 
     * @return Anzahl roter Segmente
     */
    public int getColor() {
        return color;
    }

    /**
     * Gibt die Anzahl roter Segmente im Turm
     * 
     * @return Anzahl roter Segmente
     */
    public int getShape() {
        return shape;
    }
}
