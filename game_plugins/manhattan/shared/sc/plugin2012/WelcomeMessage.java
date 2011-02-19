package sc.plugin2012;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * nachricht die zu beginn eines spiels geschickt wird, um dem client seine
 * spielrfarbe mitzuteilen.
 * 
 */
@XStreamAlias(value = "mh:welcome")
public class WelcomeMessage {
	@XStreamAsAttribute
	private PlayerColor color;

	public WelcomeMessage(PlayerColor c) {
		color = c;
	}

	public PlayerColor getYourColor() {
		return color;
	}
}
