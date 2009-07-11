package sc.guiplugin.interfaces;

import java.io.IOException;

public interface ISlot {

	/**
	 * Reserves the slot for a human player.
	 */
	void asHuman() throws IOException;

	/**
	 * Reserves the slot for a client.
	 * 
	 * @return the necessary command-line arguments to be passed
	 */
	String[] asClient();
}
