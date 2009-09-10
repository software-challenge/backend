package sc.guiplugin.interfaces.listener;

import sc.shared.GameResult;

public interface IGameEndedListener {

	/**
	 * Invoked when the active game ended.
	 * 
	 * @param data
	 */
	void gameEnded(GameResult data, String gameResultString);
}
