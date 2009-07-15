package sc.guiplugin.interfaces;

import java.io.IOException;

public interface ISlot {

	/**
	 * Reserves the slot for a human player.
	 */
	void asHuman(String player_one, String player_two) throws IOException;

	/**
	 * Reserves the slot for a client.
	 * 
	 * @return the necessary command-line arguments to be passed
	 */
	String[] asClient();

	/**
	 * Free's this slot for another client to connect.
	 */
	void asRemote();
}
