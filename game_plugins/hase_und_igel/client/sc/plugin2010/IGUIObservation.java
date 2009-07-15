package sc.plugin2010;

import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public interface IGUIObservation
{

	/**
	 * @param playerid
	 * @param displayMoveAction
	 */
	void newTurn(int playerid, String displayMoveAction);

	/**
	 * 
	 */
	void ready();

	/**
	 * @param data
	 */
	void gameEnded(GameResult data);

}
