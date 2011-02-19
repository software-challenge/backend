package sc.plugin2012;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * eine debughinweis ist ein container für einen string der einem move
 * beigefuegt werden kann. beigefuegte debughints werden direkt in der
 * grafischen oberflaeche des plugins angezeit, wenn die debugansiche gewaehlt
 * wurde.
 * 
 * dies ermoeglich das schnellere debuggen von clients und besseres
 * konfigurieren von strategien, denn es muessen keien konsolenausgaben gesucht
 * werden und die hinweise werden immer zum passenden zug angezeigt.
 * 
 * @author tkra
 * 
 */
public final class DebugHint implements Cloneable {

	@XStreamAsAttribute
	public final String content;

	/**
	 * einen neuen hinweis der form key = valuie erstellen
	 * 
	 * @param key
	 *            string vor dem gleichheitszeichen
	 * @param value
	 *            string nach dem gleichheitszeichen
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
	 * ein neuen hinweis mit beliebeigen inhalt erstellen
	 * 
	 * @param content
	 *            der inhalt der angezeigt werden soll
	 */
	public DebugHint(final String content) {
		this.content = content == null ? "" : content;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new DebugHint(content);
	}

}
