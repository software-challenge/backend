package sc.plugin2010;

import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public interface IGUIObservation
{

	/**
	 * @param activePlayerId
	 * @param actionFromOther
	 */
	void newTurn(int activePlayerId, String actionFromOther);

	/**
	 * 
	 */
	void ready();

	/**
	 * @param data
	 */
	void onGameEnded(Object sender, GameResult data);

}
