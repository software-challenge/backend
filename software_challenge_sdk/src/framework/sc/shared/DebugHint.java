package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Ein Debughinweis ist ein Container für einen String, der einem Zug beigefuegt
 * werden kann. Beigefuegte Debughints werden direkt in der grafischen
 * Oberflaeche des Plugins angezeigt, wenn die Debugansicht gewaehlt wurde. <br>
 * <br>
 * Dies ermoeglich das schnellere Debuggen von Clients und besseres
 * Konfigurieren von Strategien, denn es muessen keine Konsolenausgaben gesucht
 * werden und die Hinweise werden immer zum passenden Zug angezeigt.
 */
public class DebugHint implements Cloneable {

	@XStreamAsAttribute
	public final String content;

	/**
	 * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
	 * Deserialisierung von Objekten aus XML-Nachrichten.
	 */
	public DebugHint() {
		content = null;
	}

	/**
	 * einen neuen Hinweis der form key = value erstellen
	 *
	 * @param key
	 *            string vor dem Gleichheitszeichen
	 * @param value
	 *            string nach dem Gleichheitszeichen
	 */
	public DebugHint(String key, String value) {

		key = key == null ? "" : key;
		value = value == null ? "" : value;

		if (!(key.equals("") && value.equals(""))) {
			content = key + " = " + value;
		} else {
			content = key + value;
		}

	}

	/**
	 * Gibt den Inhalt des Debughints zurück.
	 *
	 * @return den Inhalt
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * ein neuen Hinweis mit beliebigem Inhalt erstellen
	 *
	 * @param content
	 *            der Inhalt, der angezeigt werden soll
	 */
	public DebugHint(final String content) {
		this.content = content == null ? "" : content;
	}

	/**
	 * klont dieses Objekt
	 *
	 * @return ein neues Objekt mit gleichen Eigenschaften
	 * @throws CloneNotSupportedException falls Objekt nicht geklont werden kann
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new DebugHint(content);
	}

}
