package sc.guiplugin.interfaces.listener;

public interface IReadyListener {

	/**
	 * Invoked if the game is ready to start, i.e. enough players are connected
	 * to begin the game.
	 */
	void ready();
}
