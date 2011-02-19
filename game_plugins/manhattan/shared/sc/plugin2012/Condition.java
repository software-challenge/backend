package sc.plugin2012;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

class Condition {

	@XStreamAsAttribute
	public final PlayerColor winner;

	@XStreamAsAttribute
	public final String reason ;
	
	public Condition(PlayerColor winner, String reason) {
		this.winner = winner;
		this.reason = reason;
	}
	
	

}
