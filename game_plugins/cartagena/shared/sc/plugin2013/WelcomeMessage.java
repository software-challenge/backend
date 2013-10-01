package sc.plugin2013;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Nachricht, die zu Beginn eines Spiels an einen Client geschickt wird, um ihm
 * seine Spielerfarbe mitzuteilen
 * 
 */
@XStreamAlias(value = "welcome")
public class WelcomeMessage {

	@XStreamAsAttribute
	private String color;

	/**
	 * might be needed by XStream
	 */
	public WelcomeMessage() {
	}

	public WelcomeMessage(PlayerColor c) {
		color = c.toString();
	}

	/** Gibt die Farbe des Spielers zurück
	 * @return
	 */
	public PlayerColor getYourColor() {
		return PlayerColor.valueOf(color.toUpperCase());
	}

}
