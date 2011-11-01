package sc.guiplugin.interfaces.listener;

public interface INewTurnListener {

	/**
	 * Invoked if a new turn of a player has been processed.
	 * 
	 * @param info
	 */
	void newTurn(int id, String info);
}
