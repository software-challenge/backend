package sc.guiplugin.interfaces;

import java.io.IOException;

import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.INewTurnListener;
import sc.guiplugin.interfaces.listener.IReadyListener;

public interface IObservation {

	/**
	 * Starts a prepared game.
	 */
	void start();

	/**
	 * Pauses an active game.
	 */
	void pause();

	/**
	 * Unpauses a paused game.
	 */
	void unpause();

	/**
	 * Cancels an active game.
	 */
	void cancel();

	boolean hasNext();

	boolean hasPrevious();

	/**
	 * Saves the replay of the last ended game to the given
	 * <code>filename</code>.
	 * 
	 * @param filename
	 */
	void saveReplayToFile(String filename) throws IOException;

	/**
	 * Moves back a turn of a replay.
	 */
	void back();

	/**
	 * Moves forward a turn of a replay or, in an active game, sends the current
	 * move to the server.
	 */
	void next();

	void addReadyListener(IReadyListener listener);

	void removeReadyListener(IReadyListener listener);

	void addNewTurnListener(INewTurnListener listener);

	void removeNewTurnListener(INewTurnListener listener);

	void addGameEndedListener(IGameEndedListener listener);

	void removeGameEndedListener(IGameEndedListener listener);
}
