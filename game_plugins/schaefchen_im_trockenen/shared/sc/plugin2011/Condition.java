package sc.plugin2011;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

class Condition {

	@XStreamAsAttribute
	public PlayerColor winner = null;

	@XStreamAsAttribute
	public String reason = null;

}
