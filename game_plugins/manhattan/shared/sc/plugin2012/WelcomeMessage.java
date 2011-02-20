package sc.plugin2012;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * nachricht die zu beginn eines spiels geschickt wird, um dem client seine
 * spielrfarbe mitzuteilen.
 * 
 */
@XStreamAlias(value = "manhattan:welcome")
public class WelcomeMessage {
	
	@XStreamAsAttribute
	private String color;
	
	public WelcomeMessage(PlayerColor c) {
		color = c.toString().toLowerCase();
	}

	public PlayerColor getYourColor() {
		return PlayerColor.valueOf(color.toUpperCase());
	}
}
