package sc.plugin2014.moves;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Ein Debughinweis ist ein Container für einen String der einem Zug
 * beigefuegt werden kann. Beigefuegte Debughints werden direkt in der
 * grafischen Oberflaeche des Plugins angezeigt, wenn die Debugansicht gewaehlt
 * wurde. <br>
 * <br>
 * Dies ermoeglich das schnellere Debuggen von Clients und besseres
 * Konfigurieren von Strategien, denn es muessen keine Konsolenausgaben gesucht
 * werden und die Hinweise werden immer zum passenden Zug angezeigt.
 * 
 * @author tkra
 * 
 */
public class DebugHint implements Cloneable {

    @XStreamAsAttribute
    private final String content;

    public DebugHint() {
        content = "";
    }

    public DebugHint(String key, String value) {

        key = key == null ? "" : key;
        value = value == null ? "" : value;

        if (!(key.equals("") && value.equals(""))) {
            content = key + " = " + value;
        }
        else {
            content = key + value;
        }

    }

    public DebugHint(final String content) {
        this.content = content == null ? "" : content;
    }

    public String getContent() {
        return content;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new DebugHint(getContent());
    }

}
