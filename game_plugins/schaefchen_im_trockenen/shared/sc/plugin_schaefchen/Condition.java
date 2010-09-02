package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

class Condition {

	@XStreamAsAttribute
	public PlayerColor winner = null;

	@XStreamAsAttribute
	public String reason = null;

}
