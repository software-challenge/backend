package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Condition {

	public enum State {
		CONTINUING, ENDED;
	}

	@XStreamAsAttribute
	public State state = State.CONTINUING;

	@XStreamAsAttribute
	public PlayerColor winner = null;

	@XStreamAsAttribute
	public String reason = null;

}
