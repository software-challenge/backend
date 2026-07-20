package sc.plugin2017;

/**
 * @author ffi
 * 
 */
public interface IGUIObservation {

	/**
	 * @param activePlayerId Id of active player
	 * @param actionFromOther action of other player
	 */
	void newTurn(int activePlayerId, String actionFromOther);

	/**
	 * 
	 */
	void ready();

}
